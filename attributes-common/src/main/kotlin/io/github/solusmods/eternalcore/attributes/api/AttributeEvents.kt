package io.github.solusmods.eternalcore.attributes.api

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity

object AttributeEvents {
    fun interface CriticalAttackChanceEvent {
        fun applyCrit(
            attacker: LivingEntity,
            target: Entity,
            originalMultiplier: Float,
            multiplier: Changeable<Float?>?,
            chance: Changeable<Double?>?
        ): EventResult
    }

    fun interface GlideEvent {
        fun glide(glider: LivingEntity?, canGlide: Changeable<Boolean?>?): EventResult
    }

    @JvmField
    val CRITICAL_ATTACK_CHANCE_EVENT: Event<CriticalAttackChanceEvent> =
        EventFactory.createEventResult<CriticalAttackChanceEvent>()
    @JvmField
    val START_GLIDE_EVENT: Event<GlideEvent> = EventFactory.createEventResult<GlideEvent>()
    @JvmField
    val CONTINUE_GLIDE_EVENT: Event<GlideEvent> = EventFactory.createEventResult<GlideEvent>()
}