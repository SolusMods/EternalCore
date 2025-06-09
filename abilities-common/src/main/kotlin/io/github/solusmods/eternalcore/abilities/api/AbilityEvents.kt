package io.github.solusmods.eternalcore.abilities.api

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity

interface AbilityEvents {
    fun interface UnlockAbilityEvent {
        fun unlockAbility(
            instance: AbilityInstance?,
            owner: LivingEntity?,
            unlockMessage: Changeable<MutableComponent?>?
        ): EventResult?
    }

    fun interface RemoveAbilityEvent {
        fun removeAbility(
            instance: AbilityInstance?,
            owner: LivingEntity?,
            forgetMessage: Changeable<MutableComponent?>?
        ): EventResult?
    }

    fun interface AbilityActivationEvent {
        fun activateAbility(
            abilityInstance: Changeable<AbilityInstance?>?,
            owner: LivingEntity?,
            keyNumber: Int,
            mode: Int
        ): EventResult?
    }

    fun interface AbilityReleaseEvent {
        fun releaseAbility(
            abilityInstance: Changeable<AbilityInstance?>?,
            owner: LivingEntity?,
            keyNumber: Int,
            mode: Int,
            heldTicks: Int
        ): EventResult?
    }

    fun interface AbilityToggleEvent {
        fun toggleAbility(abilityInstance: Changeable<AbilityInstance?>?, owner: LivingEntity?): EventResult?
    }

    fun interface AbilityScrollEvent {
        fun scroll(
            abilityInstance: Changeable<AbilityInstance?>?,
            owner: LivingEntity?,
            delta: Changeable<Double?>?
        ): EventResult?
    }

    fun interface AbilityScrollClientEvent {
        fun scroll(instance: AbilityInstance?, owner: LivingEntity?, delta: Double): EventResult?
    }

    fun interface AbilityTickEvent {
        fun tick(instance: AbilityInstance?, owner: LivingEntity?): EventResult?
    }

    fun interface AbilityPostTickEvent {
        fun tick(instance: AbilityInstance?, owner: LivingEntity?)
    }

    fun interface AbilityUpdateCooldownEvent {
        fun cooldown(instance: AbilityInstance?, owner: LivingEntity?, currentCooldown: Int, mode: Int): EventResult?
    }

    fun interface AbilityDamageCalculationEvent {
        fun calculate(
            storage: Abilities?,
            entity: LivingEntity?,
            source: DamageSource?,
            amount: Changeable<Float?>?
        ): EventResult?
    }

    companion object {
        val UNLOCK_ABILITY: Event<UnlockAbilityEvent?> = EventFactory.createEventResult<UnlockAbilityEvent?>()
        val REMOVE_ABILITY: Event<RemoveAbilityEvent?> = EventFactory.createEventResult<RemoveAbilityEvent?>()
        val ACTIVATE_ABILITY: Event<AbilityActivationEvent?> = EventFactory.createEventResult<AbilityActivationEvent?>()
        val RELEASE_ABILITY: Event<AbilityReleaseEvent?> = EventFactory.createEventResult<AbilityReleaseEvent?>()
        val TOGGLE_ABILITY: Event<AbilityToggleEvent?> = EventFactory.createEventResult<AbilityToggleEvent?>()
        val ABILITY_SCROLL: Event<AbilityScrollEvent?> = EventFactory.createEventResult<AbilityScrollEvent?>()
        val ABILITY_SCROLL_CLIENT: Event<AbilityScrollClientEvent?> =
            EventFactory.createEventResult<AbilityScrollClientEvent?>()
        val ABILITY_PRE_TICK: Event<AbilityTickEvent?> = EventFactory.createEventResult<AbilityTickEvent?>()
        val ABILITY_POST_TICK: Event<AbilityPostTickEvent?> = EventFactory.createLoop<AbilityPostTickEvent?>()
        val ABILITY_UPDATE_COOLDOWN: Event<AbilityUpdateCooldownEvent?> =
            EventFactory.createEventResult<AbilityUpdateCooldownEvent?>()
        val ABILITY_DAMAGE_PRE_CALCULATION: Event<AbilityDamageCalculationEvent?> =
            EventFactory.createEventResult<AbilityDamageCalculationEvent?>()
        val ABILITY_DAMAGE_CALCULATION: Event<AbilityDamageCalculationEvent?> =
            EventFactory.createEventResult<AbilityDamageCalculationEvent?>()
        val ABILITY_DAMAGE_POST_CALCULATION: Event<AbilityDamageCalculationEvent?> =
            EventFactory.createEventResult<AbilityDamageCalculationEvent?>()
    }
}
