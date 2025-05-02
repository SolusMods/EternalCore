package io.github.solusmods.eternalcore.stage.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public interface StageEvents {
    Event<SetStageEvent> SET_STAGE = EventFactory.createEventResult();
    Event<StageTickEvent> STAGE_PRE_TICK = EventFactory.createLoop();
    Event<StageTickEvent> STAGE_POST_TICK = EventFactory.createLoop();
    Event<TrackStageEvent> TRACK_STAGE = EventFactory.createEventResult();
    Event<ReachStageEvent> REACH_STAGE = EventFactory.createEventResult();

    @FunctionalInterface
    interface SetStageEvent {
        EventResult set(StageInstance instance, LivingEntity owner, StageInstance newInstance, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> stageMessage);
    }

    @FunctionalInterface
    interface StageTickEvent {
        void tick(StageInstance instance, LivingEntity owner);
    }

    @FunctionalInterface
    interface TrackStageEvent {
        EventResult track(StageInstance instance, LivingEntity owner, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> stageMessage);
    }

    @FunctionalInterface
    interface ReachStageEvent {
        EventResult reach(StageInstance instance, LivingEntity owner, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> stageMessage);
    }
}
