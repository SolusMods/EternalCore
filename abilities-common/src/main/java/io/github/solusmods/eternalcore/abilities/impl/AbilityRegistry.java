package io.github.solusmods.eternalcore.abilities.impl;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import io.github.solusmods.eternalcore.abilities.ModuleConstants;
import io.github.solusmods.eternalcore.abilities.api.Ability;
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI;
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents;
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance;
import io.github.solusmods.eternalcore.stage.api.entity.EntityEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

public class AbilityRegistry {
    private static final ResourceLocation registryId = EternalCoreAbilities.create("abilities");
    public static final Registrar<Ability> ABILITIES = RegistrarManager.get(ModuleConstants.MOD_ID).<Ability>builder(registryId)
            .syncToClients()
            .build();
    public static final ResourceKey<Registry<Ability>> KEY = (ResourceKey<Registry<Ability>>) ABILITIES.key();

    public static void init() {
        EntityEvents.LIVING_EFFECT_ADDED.register((entity, source, changeableTarget) -> {
            for (AbilityInstance instance : AbilityAPI.getAbilitiesFrom(entity).getLearnedAbilities()) {
                if (!instance.canInteractAbility(entity)) continue;
                if (!instance.onEffectAdded(entity, source, changeableTarget)) return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });

        EntityEvents.LIVING_CHANGE_TARGET.register((entity, changeableTarget) -> {
            if (!changeableTarget.isPresent()) return EventResult.pass();
            LivingEntity owner = changeableTarget.get();
            if (owner == null) return EventResult.pass();

            for (AbilityInstance instance : AbilityAPI.getAbilitiesFrom(owner).getLearnedAbilities()) {
                if (!instance.canInteractAbility(owner)) continue;
                if (!instance.onBeingTargeted(changeableTarget, entity)) return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });

        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            for (AbilityInstance instance : AbilityAPI.getAbilitiesFrom(entity).getLearnedAbilities()) {
                if (!instance.canInteractAbility(entity)) continue;
                if (!instance.onBeingDamaged(entity, source, amount)) return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });

        AbilityEvents.ABILITY_DAMAGE_PRE_CALCULATION.register((storage, target, source, amount) -> {
            if (!(source.getEntity() instanceof LivingEntity owner)) return EventResult.pass();

            for (AbilityInstance instance : AbilityAPI.getAbilitiesFrom(owner).getLearnedAbilities()) {
                if (!instance.canInteractAbility(owner)) continue;
                if (!instance.onDamageEntity(owner, target, source, amount)) return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });

        AbilityEvents.ABILITY_DAMAGE_POST_CALCULATION.register((storage, target, source, amount) -> {
            if (!(source.getEntity() instanceof LivingEntity owner)) return EventResult.pass();

            for (AbilityInstance instance : AbilityAPI.getAbilitiesFrom(owner).getLearnedAbilities()) {
                if (!instance.canInteractAbility(owner)) continue;
                if (!instance.onTouchEntity(owner, target, source, amount)) return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });

        EntityEvents.LIVING_DAMAGE.register((entity, source, amount) -> {
            for (AbilityInstance instance : AbilityAPI.getAbilitiesFrom(entity).getLearnedAbilities()) {
                if (!instance.canInteractAbility(entity)) continue;
                if (!instance.onTakenDamage(entity, source, amount)) return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });

        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            for (AbilityInstance instance : AbilityAPI.getAbilitiesFrom(entity).getLearnedAbilities()) {
                if (!instance.canInteractAbility(entity)) continue;
                if (!instance.onDeath(entity, source)) return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });

        PlayerEvent.PLAYER_RESPAWN.register((newPlayer, conqueredEnd, removalReason) -> {
            for (AbilityInstance instance : AbilityAPI.getAbilitiesFrom(newPlayer).getLearnedAbilities()) {
                if (!instance.canInteractAbility(newPlayer)) continue;
                instance.onRespawn(newPlayer, conqueredEnd);
            }
        });

        EntityEvents.PROJECTILE_HIT.register((result, projectile, deflectionChangeable, hitResultChangeable) -> {
            if (!(result instanceof EntityHitResult hitResult)) return;
            if (!(hitResult.getEntity() instanceof LivingEntity hitEntity)) return;

            for (AbilityInstance instance : AbilityAPI.getAbilitiesFrom(hitEntity).getLearnedAbilities()) {
                if (!instance.canInteractAbility(hitEntity)) continue;
                instance.onProjectileHit(hitEntity, hitResult, projectile, deflectionChangeable, hitResultChangeable);
            }
        });
    }
}
