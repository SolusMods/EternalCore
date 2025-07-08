package io.github.solusmods.eternalcore.api.stage;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
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
        /**
         * Викликається при встановленні нової стадії.
         *
         * @param current      Стара стадія або null
         * @param owner        Сутність власника
         * @param newStage     Нова стадія
         * @param advancement  Чи це прорив
         * @param notifyPlayer Чи показувати повідомлення
         * @param stageMessage Повідомлення для гравця
         * @return Результат події
         */
        EventResult set(AbstractStage current, LivingEntity owner, AbstractStage newStage, boolean advancement,
                        Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> stageMessage);
    }


    @FunctionalInterface
    interface StageTickEvent {
        /**
         * Викликається кожен тік для активної стадії.
         *
         * @param stage Активна стадія
         * @param owner Сутність власника
         */
        void tick(AbstractStage stage, LivingEntity owner);
    }


    @FunctionalInterface
    interface TrackStageEvent {
        /**
         * Викликається при початку відстеження стадії.
         *
         * @param stage        Стадія, яку почали відстежувати
         * @param owner        Сутність власника
         * @param advancement  Чи це прорив
         * @param notifyPlayer Чи показувати повідомлення
         * @param stageMessage Повідомлення для гравця
         * @return Результат події
         */
        EventResult track(AbstractStage stage, LivingEntity owner, boolean advancement,
                          Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> stageMessage);
    }


    @FunctionalInterface
    interface ReachStageEvent {
        /**
         * Викликається при досягненні стадії.
         *
         * @param stage        Стадія, яку досягли
         * @param owner        Сутність власника
         * @param advancement  Чи це прорив
         * @param notifyPlayer Чи показувати повідомлення
         * @param stageMessage Повідомлення для гравця
         * @return Результат події
         */
        EventResult reach(AbstractStage stage, LivingEntity owner, boolean advancement,
                          Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> stageMessage);
    }
}
