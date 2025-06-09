package io.github.solusmods.eternalcore.abilities.impl

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities
import io.github.solusmods.eternalcore.abilities.ModuleConstants
import io.github.solusmods.eternalcore.abilities.api.Abilities
import io.github.solusmods.eternalcore.abilities.api.Ability
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents.AbilityDamageCalculationEvent
import io.github.solusmods.eternalcore.entity.api.EntityEvents
import io.github.solusmods.eternalcore.entity.api.EntityEvents.LivingEffectAddedEvent
import io.github.solusmods.eternalcore.entity.api.EntityEvents.ProjectileHitEvent
import io.github.solusmods.eternalcore.entity.api.ProjectileHitResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ProjectileDeflection
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object AbilityRegistry {
    private val registryId: ResourceLocation = EternalCoreAbilities.create("abilities")
    val ABILITIES: Registrar<Ability?> = RegistrarManager.get(ModuleConstants.MOD_ID).builder<Ability?>(registryId)
        .syncToClients()
        .build()
    val KEY: ResourceKey<Registry<Ability?>?>? = ABILITIES.key() as ResourceKey<Registry<Ability?>?>?

    fun init() {
        EntityEvents.LIVING_EFFECT_ADDED.register(LivingEffectAddedEvent {
            entity: LivingEntity?, source: Entity?, changeableTarget: Changeable<MobEffectInstance?>? ->
            for (instance in AbilityAPI.getAbilitiesFrom(
                entity!!
            )?.learnedAbilities!!) {
                if (!instance!!.canInteractAbility(entity)) continue
                if (!instance.onEffectAdded(
                        entity,
                        source,
                        changeableTarget
                    )
                ) return@LivingEffectAddedEvent EventResult.interruptFalse()
            }
            EventResult.pass()
        })

        EntityEvents.LIVING_CHANGE_TARGET.register(EntityEvents.LivingChangeTargetEvent {
            entity: LivingEntity?, changeableTarget: Changeable<LivingEntity?>? ->
            if (!changeableTarget!!.isPresent) return@LivingChangeTargetEvent EventResult.pass()
            val owner = changeableTarget.get()
            if (owner == null) return@LivingChangeTargetEvent EventResult.pass()

            for (instance in AbilityAPI.getAbilitiesFrom(owner)?.learnedAbilities!!) {
                if (!instance!!.canInteractAbility(owner)) continue
                if (!instance.onBeingTargeted(changeableTarget, entity)) return@LivingChangeTargetEvent EventResult.interruptFalse()
            }
            EventResult.pass()
        })

        EntityEvent.LIVING_HURT.register(EntityEvent.LivingHurt { entity: LivingEntity?, source: DamageSource?, amount: Float ->
            for (instance in AbilityAPI.getAbilitiesFrom(
                entity!!
            )?.learnedAbilities!!) {
                if (!instance!!.canInteractAbility(entity)) continue
                if (!instance.onBeingDamaged(entity, source, amount)) return@LivingHurt EventResult.interruptFalse()
            }
            EventResult.pass()
        })

        AbilityEvents.Companion.ABILITY_DAMAGE_PRE_CALCULATION.register(AbilityDamageCalculationEvent {
            storage: Abilities?, target: LivingEntity?, source: DamageSource?, amount: Changeable<Float?>? ->
            if (source!!.entity !is LivingEntity) return@AbilityDamageCalculationEvent EventResult.pass()
            val owner = source!!.entity as LivingEntity
            for (instance in AbilityAPI.getAbilitiesFrom(owner)?.learnedAbilities!!) {
                if (!instance!!.canInteractAbility(owner)) continue
                if (!instance.onDamageEntity(
                        owner,
                        target,
                        source,
                        amount
                    )
                ) return@AbilityDamageCalculationEvent EventResult.interruptFalse()
            }
            EventResult.pass()
        })

        AbilityEvents.Companion.ABILITY_DAMAGE_POST_CALCULATION.register(AbilityDamageCalculationEvent { storage: Abilities?, target: LivingEntity?, source: DamageSource?, amount: Changeable<Float?>? ->
            if (source!!.getEntity() !is LivingEntity) return@AbilityDamageCalculationEvent EventResult.pass()
            val owner = source!!.entity as LivingEntity
            for (instance in AbilityAPI.getAbilitiesFrom(owner)?.learnedAbilities!!) {
                if (!instance!!.canInteractAbility(owner)) continue
                if (!instance.onTouchEntity(owner, target, source, amount)) return@AbilityDamageCalculationEvent EventResult.interruptFalse()
            }
            EventResult.pass()
        })

        EntityEvents.LIVING_DAMAGE.register(EntityEvents.LivingDamageEvent { entity: LivingEntity?, source: DamageSource?, amount: Changeable<Float?>? ->
            for (instance in AbilityAPI.getAbilitiesFrom(
                entity!!
            )?.learnedAbilities!!) {
                if (!instance!!.canInteractAbility(entity)) continue
                if (!instance.onTakenDamage(entity, source, amount)) return@LivingDamageEvent EventResult.interruptFalse()
            }
            EventResult.pass()
        })

        EntityEvent.LIVING_DEATH.register(EntityEvent.LivingDeath { entity: LivingEntity?, source: DamageSource? ->
            for (instance in AbilityAPI.getAbilitiesFrom(entity!!)?.learnedAbilities!!) {
                if (!instance!!.canInteractAbility(entity)) continue
                if (!instance.onDeath(entity, source)) return@LivingDeath EventResult.interruptFalse()
            }
            EventResult.pass()
        })

        PlayerEvent.PLAYER_RESPAWN.register(PlayerEvent.PlayerRespawn { newPlayer: ServerPlayer?, conqueredEnd: Boolean, removalReason: Entity.RemovalReason? ->
            for (instance in AbilityAPI.getAbilitiesFrom(
                newPlayer!!
            )?.learnedAbilities!!) {
                if (!instance!!.canInteractAbility(newPlayer)) continue
                instance.onRespawn(newPlayer, conqueredEnd)
            }
        })

        EntityEvents.PROJECTILE_HIT.register(ProjectileHitEvent { result: HitResult?, projectile: Projectile?, deflectionChangeable: Changeable<ProjectileDeflection?>?, hitResultChangeable: Changeable<ProjectileHitResult?>? ->
            if (result !is EntityHitResult) return@ProjectileHitEvent
            if (result.getEntity() !is LivingEntity) return@ProjectileHitEvent
            val hitEntity = result.getEntity() as LivingEntity
            for (instance in AbilityAPI.getAbilitiesFrom(hitEntity)?.learnedAbilities!!) {
                if (!instance!!.canInteractAbility(hitEntity)) continue
                instance.onProjectileHit(hitEntity, result, projectile, deflectionChangeable, hitResultChangeable)
            }
        })
    }
}
