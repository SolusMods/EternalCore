package io.github.solusmods.eternalcore.spiritual_root.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public interface SpiritualRootEvents {
    Event<SpiritualRootAddEvent> ADD = EventFactory.createEventResult();
    Event<SpiritualRootMasteringEvent> MASTERING = EventFactory.createEventResult();
    Event<SpiritualRootAdvanceEvent> ADVANCE = EventFactory.createEventResult();

    @FunctionalInterface
    interface SpiritualRootAddEvent {
        EventResult add(SpiritualRootInstance instance, LivingEntity living, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> rootMessage);
    }

    @FunctionalInterface
    interface SpiritualRootMasteringEvent {
        EventResult mastering(SpiritualRootInstance instance, LivingEntity entity, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> rootMessage);
    }

    @FunctionalInterface
    interface SpiritualRootAdvanceEvent {
        EventResult advance(SpiritualRootInstance instance, LivingEntity entity, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> rootMessage);
    }
}
