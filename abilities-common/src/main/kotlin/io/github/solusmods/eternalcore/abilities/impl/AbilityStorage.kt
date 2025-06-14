package io.github.solusmods.eternalcore.abilities.impl

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import dev.architectury.event.EventResult
import dev.architectury.event.events.common.PlayerEvent
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities
import io.github.solusmods.eternalcore.abilities.api.Abilities
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance
import io.github.solusmods.eternalcore.entity.api.EntityEvents
import io.github.solusmods.eternalcore.network.api.util.Changeable
import io.github.solusmods.eternalcore.storage.EternalCoreStorage
import io.github.solusmods.eternalcore.storage.api.Storage
import io.github.solusmods.eternalcore.storage.api.StorageEvents
import io.github.solusmods.eternalcore.storage.api.StorageHolder
import io.github.solusmods.eternalcore.storage.api.StorageKey
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer

@Suppress("unchecked_cast")
open class AbilityStorage protected constructor(holder: StorageHolder) : Storage(holder), Abilities {
    private val abilityInstance: MutableMap<ResourceLocation?, AbilityInstance> =
        mutableMapOf()
    private var hasRemovedAbilities = false
    override val learnedAbilities: MutableCollection<AbilityInstance>
        get() {
            return abilityInstance.values
        }

    override fun updateAbility(updatedInstance: AbilityInstance, sync: Boolean) {
        updatedInstance.markDirty()
        this.abilityInstance.put(updatedInstance.abilityId, updatedInstance)
        if (sync) markDirty()
    }

    override fun learnAbility(instance: AbilityInstance, component: MutableComponent?): Boolean {
        if (this.abilityInstance.containsKey(instance.abilityId)) {
//            EternalCoreAbilities.log.debug("Tried to register a deduplicate of {}.", instance.abilityId)
            return false
        }

        val unlockMessage = Changeable.of(component)
        val result: EventResult? = AbilityEvents.UNLOCK_ABILITY.invoker()?.unlockAbility(
            instance,
            this.owner, unlockMessage
        )
        if (result!!.isFalse) return false

        instance.markDirty()
        this.abilityInstance.put(instance.abilityId, instance)
        if (unlockMessage.isPresent) this.owner.sendSystemMessage(unlockMessage.get()!!)
        instance.onLearnAbility(this.owner)
        markDirty()
        return true
    }

    override fun getAbility(abilityId: ResourceLocation): Optional<AbilityInstance?> {
        return Optional.ofNullable<AbilityInstance?>(this.abilityInstance[abilityId]) as Optional<AbilityInstance?>
    }

    override fun forgetAbility(abilityId: ResourceLocation, component: MutableComponent?) {
        if (!this.abilityInstance.containsKey(abilityId)) return
        val instance: AbilityInstance = this.abilityInstance[abilityId]!!

        val forgetMessage = Changeable.of(component)
        val result: EventResult? = AbilityEvents.REMOVE_ABILITY.invoker()!!.removeAbility(
            instance,
            this.owner, forgetMessage
        )
        if (result!!.isFalse) return

        if (forgetMessage.isPresent) this.owner.sendSystemMessage(forgetMessage.get()!!)
        instance.onForgetAbility(this.owner)
        instance.markDirty()

        this.learnedAbilities.remove(instance)
        this.hasRemovedAbilities = true
        markDirty()
    }

    override fun forEachAbility(abilityInstanceConsumer: BiConsumer<AbilityStorage, AbilityInstance>) {
        learnedAbilities.forEach { instance -> abilityInstanceConsumer.accept(this, instance) }
        markDirty()
    }

    fun handleAbilityRelease(skillId: ResourceLocation, heldTick: Int, keyNumber: Int, mode: Int) {
        getAbility(skillId).ifPresent { abilityInstance ->
            val changeable = Changeable.of(abilityInstance)
            if (AbilityEvents.RELEASE_ABILITY.invoker().releaseAbility(
                    changeable,
                    this.owner, keyNumber, mode, heldTick
                ).isFalse
            ) return@ifPresent
            val ability = changeable.get()
            if (ability == null) return@ifPresent

            if (ability.canInteractAbility(this.owner) && mode < ability.modes) {
                if (!ability.onCoolDown(mode) || ability.canIgnoreCoolDown(this.owner, mode)) {
                    ability.onRelease(this.owner, heldTick, keyNumber, mode)
                    if (ability.dirty) markDirty()
                }
            }

            ability.removeAttributeModifiers(this.owner, mode)
            val ownerID = this.owner.getUUID()
            if (tickingAbilities.containsKey(ownerID)) tickingAbilities.get(ownerID)
                .removeIf { tickingAbility: TickingAbility -> tickingAbility.ability === ability.ability }
        }
    }

    override fun save(data: CompoundTag) {
        val skillList = ListTag()
        this.abilityInstance.values.forEach(Consumer { instance: AbilityInstance? ->
            skillList.add(instance!!.toNBT())
            instance.resetDirty()
        })
        data.put(ABILITY_LIST_KEY, skillList)
    }

    override fun load(data: CompoundTag) {
        if (data.contains("resetExistingData")) {
            this.abilityInstance.clear()
        }

        for (tag in data.getList(ABILITY_LIST_KEY, Tag.TAG_COMPOUND.toInt())) {
            try {
                val instance: AbilityInstance = AbilityInstance.fromNBT(tag as CompoundTag?)
                this.abilityInstance.put(instance.abilityId, instance)
            } catch (e: Exception) {
                EternalCoreStorage.LOG.error("Failed to load ability instance from NBT", e)
            }
        }
    }

    override fun saveOutdated(data: CompoundTag) {
        if (this.hasRemovedAbilities) {
            this.hasRemovedAbilities = false
            data.putBoolean("resetExistingData", true)
            super.saveOutdated(data)
        } else {
            val skillList = ListTag()
            for (instance in this.abilityInstance.values) {
                if (!instance.dirty) continue
                skillList.add(instance.toNBT())
                instance.resetDirty()
            }
            data.put(ABILITY_LIST_KEY, skillList)
        }
    }

    protected val owner: LivingEntity
        get() = this.holder as LivingEntity

    companion object {
        var key: StorageKey<AbilityStorage>? = null
        const val INSTANCE_UPDATE: Int = 20
        const val PASSIVE_SKILL: Int = 100
        val tickingAbilities: Multimap<UUID?, TickingAbility> = ArrayListMultimap.create<UUID?, TickingAbility?>() as Multimap<UUID?, TickingAbility>
        private const val ABILITY_LIST_KEY = "abilities"
        private val ID: ResourceLocation = EternalCoreAbilities.create("ability_storage")

        fun init() {
            StorageEvents.REGISTER_ENTITY_STORAGE.register { registry: StorageEvents.StorageRegistry<Entity> ->
                key = registry.register(
                    ID,
                    AbilityStorage::class.java,
                    { obj: Entity -> LivingEntity::class.java.isInstance(obj) },
                    { target: Entity -> AbilityStorage(target) })
            }

            EntityEvents.LIVING_HURT.register { entity, source, changeable: Changeable<Float?>? ->
                val skills = AbilityAPI.getAbilitiesFrom(
                    entity!!
                ) ?: return@register EventResult.interruptFalse()
                if (AbilityEvents.ABILITY_DAMAGE_PRE_CALCULATION.invoker()
                    .calculate(skills, entity, source, changeable).isFalse
                ) return@register EventResult.interruptFalse()
                if (AbilityEvents.ABILITY_DAMAGE_CALCULATION.invoker()
                    !!.calculate(skills, entity, source, changeable).isFalse
                ) return@register EventResult.interruptFalse()
                if (AbilityEvents.ABILITY_DAMAGE_POST_CALCULATION.invoker()
                    !!.calculate(skills, entity, source, changeable).isFalse
                ) return@register EventResult.interruptFalse()
                EventResult.pass()
            }

            EntityEvents.LIVING_POST_TICK.register { entity: LivingEntity? ->
                val level = entity!!.level()
                if (level.isClientSide()) return@register
                val storage = AbilityAPI.getAbilitiesFrom(entity)
                handleAbilityTick(entity, level, storage!!)
                if (entity is Player) handleAbilityHeldTick(entity, storage)
                storage.markDirty()
            }

            PlayerEvent.PLAYER_QUIT.register { player: ServerPlayer? ->
                val multimap: Multimap<UUID?, TickingAbility> = tickingAbilities
                if (multimap.containsKey(player!!.getUUID())) {
                    for (skill in multimap.get(player.getUUID())) {
                        val instance = AbilityAPI.getAbilitiesFrom(player)!!.getAbility(skill.ability)
                        if (instance!!.isEmpty) continue
                        skill.ability.removeAttributeModifiers(instance.get(), player, skill.mode)
                    }
                    multimap.removeAll(player.getUUID())
                }
            }
        }

        private fun handleAbilityTick(entity: LivingEntity, level: Level, storage: Abilities) {
            val server = level.server
            if (server == null) return

            val shouldPassiveConsume = server.tickCount % INSTANCE_UPDATE == 0
            if (!shouldPassiveConsume) return
            checkPlayerOnlyEffects(entity, storage)

            val passiveAbilityActivate = server.tickCount % PASSIVE_SKILL == 0
            if (!passiveAbilityActivate) return

            tickAbilities(entity, storage)
        }

        private fun tickAbilities(entity: LivingEntity, storage: Abilities) {
            val tickingAbilities: MutableList<AbilityInstance> = ArrayList<AbilityInstance>()
            for (instance in storage.learnedAbilities) {
                val optional = storage.getAbility(instance.ability!!)
                if (optional!!.isEmpty) continue

                val skillInstance = optional.get()
                if (!skillInstance.canInteractAbility(entity)) continue
                if (!skillInstance.canTick(entity)) continue
                if (AbilityEvents.ABILITY_PRE_TICK.invoker()!!.tick(skillInstance, entity).isFalse) continue
                tickingAbilities.add(skillInstance)
            }

            for (instance in tickingAbilities) {
                instance.onTick(entity)
                AbilityEvents.ABILITY_POST_TICK.invoker()!!.tick(instance, entity)
            }
        }

        private fun checkPlayerOnlyEffects(entity: LivingEntity?, storage: Abilities) {
            if (entity !is Player) return
            val toBeRemoved: MutableList<AbilityInstance> = ArrayList<AbilityInstance>()

            for (instance in storage.learnedAbilities) {
                // Update cooldown
                for (i in 0..<instance.modes) {
                    if (!instance.onCoolDown(i)) continue
                    if (!AbilityEvents.ABILITY_UPDATE_COOLDOWN.invoker()
                            !!.cooldown(instance, entity, instance.getCoolDown(i), i).isFalse
                    ) instance.decreaseCoolDown(1, i)
                }

                // Update temporary skill timer
//                if (instance.isTemporaryAbility()) continue
                instance.decreaseRemoveTime(1)
                if (!instance.shouldRemove()) continue
                toBeRemoved.add(instance)
            }

            // Remove temporary skills
            for (instance in toBeRemoved) {
                storage.forgetAbility(instance)
            }
        }

        private fun handleAbilityHeldTick(player: Player, storage: Abilities) {
            if (!tickingAbilities.containsKey(player.getUUID())) return
            tickingAbilities.get(player.getUUID()).removeIf { tickingAbility: TickingAbility ->
                if (!tickingAbility.tick(storage, player)) {
                    val instance = storage.getAbility(tickingAbility.ability)
                    if (instance!!.isEmpty) return@removeIf true
                    tickingAbility.ability
                        .removeAttributeModifiers(instance.get(), player, tickingAbility.mode)
                    return@removeIf true
                }
                false
            }
        }
    }
}
