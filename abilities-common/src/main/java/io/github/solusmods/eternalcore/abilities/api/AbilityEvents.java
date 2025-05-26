package io.github.solusmods.eternalcore.abilities.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface AbilityEvents {
    Event<UnlockAbilityEvent> UNLOCK_ABILITY = EventFactory.createEventResult();
    Event<RemoveAbilityEvent> REMOVE_ABILITY = EventFactory.createEventResult();
    Event<AbilityActivationEvent> ACTIVATE_ABILITY = EventFactory.createEventResult();
    Event<AbilityReleaseEvent> RELEASE_ABILITY = EventFactory.createEventResult();
    Event<AbilityToggleEvent> TOGGLE_ABILITY = EventFactory.createEventResult();
    Event<AbilityScrollEvent> ABILITY_SCROLL = EventFactory.createEventResult();
    Event<AbilityScrollClientEvent> ABILITY_SCROLL_CLIENT = EventFactory.createEventResult();
    Event<AbilityTickEvent> ABILITY_PRE_TICK = EventFactory.createEventResult();
    Event<AbilityPostTickEvent> ABILITY_POST_TICK = EventFactory.createLoop();
    Event<AbilityUpdateCooldownEvent> ABILITY_UPDATE_COOLDOWN = EventFactory.createEventResult();
    Event<AbilityDamageCalculationEvent> ABILITY_DAMAGE_PRE_CALCULATION = EventFactory.createEventResult();
    Event<AbilityDamageCalculationEvent> ABILITY_DAMAGE_CALCULATION = EventFactory.createEventResult();
    Event<AbilityDamageCalculationEvent> ABILITY_DAMAGE_POST_CALCULATION = EventFactory.createEventResult();


    @FunctionalInterface
    interface UnlockAbilityEvent {
        EventResult unlockAbility(AbilityInstance instance, LivingEntity owner, Changeable<MutableComponent> unlockMessage);
    }

    @FunctionalInterface
    interface RemoveAbilityEvent {
        EventResult removeAbility(AbilityInstance instance, LivingEntity owner, Changeable<MutableComponent> forgetMessage);
    }

    @FunctionalInterface
    interface AbilityActivationEvent {
        EventResult activateAbility(Changeable<AbilityInstance> abilityInstance, LivingEntity owner, int keyNumber, int mode);
    }

    @FunctionalInterface
    interface AbilityReleaseEvent {
        EventResult releaseAbility(Changeable<AbilityInstance> abilityInstance, LivingEntity owner, int keyNumber, int mode, int heldTicks);
    }

    @FunctionalInterface
    interface AbilityToggleEvent {
        EventResult toggleAbility(Changeable<AbilityInstance> abilityInstance, LivingEntity owner);
    }

    @FunctionalInterface
    interface AbilityScrollEvent {
        EventResult scroll(Changeable<AbilityInstance> abilityInstance, LivingEntity owner, Changeable<Double> delta);
    }

    @FunctionalInterface
    interface AbilityScrollClientEvent {
        EventResult scroll(AbilityInstance instance, LivingEntity owner, double delta);
    }

    @FunctionalInterface
    interface AbilityTickEvent {
        EventResult tick(AbilityInstance instance, LivingEntity owner);
    }

    @FunctionalInterface
    interface AbilityPostTickEvent {
        void tick(AbilityInstance instance, LivingEntity owner);
    }

    @FunctionalInterface
    interface AbilityUpdateCooldownEvent {
        EventResult cooldown(AbilityInstance instance, LivingEntity owner, int currentCooldown, int mode);
    }

    @FunctionalInterface
    interface AbilityDamageCalculationEvent {
        EventResult calculate(Abilities storage, LivingEntity entity, DamageSource source, Changeable<Float> amount);
    }
}
