package io.github.solusmods.eternalcore.stage.api

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity

interface StageEvents {
    fun interface SetStageEvent {
        fun set(
            instance: StageInstance,
            owner: LivingEntity,
            newInstance: StageInstance,
            advancement: Boolean?,
            notifyPlayer: Changeable<Boolean?>?,
            stageMessage: Changeable<MutableComponent?>?
        ): EventResult
    }

    fun interface StageTickEvent {
        fun tick(instance: StageInstance, owner: LivingEntity)
    }

    fun interface TrackStageEvent {
        fun track(
            instance: StageInstance,
            owner: LivingEntity,
            advancement: Boolean?,
            notifyPlayer: Changeable<Boolean?>?,
            stageMessage: Changeable<MutableComponent?>?
        ): EventResult
    }

    fun interface ReachStageEvent {
        fun reach(
            instance: StageInstance,
            owner: LivingEntity,
            advancement: Boolean?,
            notifyPlayer: Changeable<Boolean?>?,
            stageMessage: Changeable<MutableComponent?>?
        ): EventResult
    }

    companion object {
        val SET_STAGE: Event<SetStageEvent> = EventFactory.createEventResult<SetStageEvent>()
        val STAGE_PRE_TICK: Event<StageTickEvent> = EventFactory.createLoop<StageTickEvent>()
        val STAGE_POST_TICK: Event<StageTickEvent> = EventFactory.createLoop<StageTickEvent>()
        val TRACK_STAGE: Event<TrackStageEvent> = EventFactory.createEventResult<TrackStageEvent>()
        val REACH_STAGE: Event<ReachStageEvent> = EventFactory.createEventResult<ReachStageEvent>()
    }
}
