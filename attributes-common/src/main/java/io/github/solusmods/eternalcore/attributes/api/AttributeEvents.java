package io.github.solusmods.eternalcore.attributes.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface AttributeEvents {
    Event<CriticalAttackChanceEvent> CRITICAL_ATTACK_CHANCE_EVENT = EventFactory.createEventResult();
    Event<GlideEvent> START_GLIDE_EVENT = EventFactory.createEventResult();
    Event<GlideEvent> CONTINUE_GLIDE_EVENT = EventFactory.createEventResult();

    @FunctionalInterface
    interface CriticalAttackChanceEvent {
        EventResult applyCrit(LivingEntity attacker, Entity target, float originalMultiplier, Changeable<Float> multiplier, Changeable<Double> chance);
    }

    @FunctionalInterface
    interface GlideEvent {
        EventResult glide(LivingEntity glider, Changeable<Boolean> canGlide);
    }
}
