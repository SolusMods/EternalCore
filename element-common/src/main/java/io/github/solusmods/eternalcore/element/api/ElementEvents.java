package io.github.solusmods.eternalcore.element.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public interface ElementEvents {
    Event<SetElementEvent> SET_ELEMENT = EventFactory.createEventResult();
    Event<ElementTickEvent> ELEMENT_PRE_TICK = EventFactory.createLoop();
    Event<ElementTickEvent> ELEMENT_POST_TICK = EventFactory.createLoop();
    Event<AddElementEvent> ADD_ELEMENT = EventFactory.createEventResult();

    @FunctionalInterface
    interface SetElementEvent {
        EventResult set(ElementInstance instance, LivingEntity owner, ElementInstance newInstance, boolean breakthrough, Changeable<Boolean> notify, Changeable<MutableComponent> realmMessage);
    }

    @FunctionalInterface
    interface ElementTickEvent {
        void tick(ElementInstance instance, LivingEntity owner);
    }

    @FunctionalInterface
    interface AddElementEvent {
        EventResult add(ElementInstance instance, LivingEntity owner, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> realmMessage);
    }
}
