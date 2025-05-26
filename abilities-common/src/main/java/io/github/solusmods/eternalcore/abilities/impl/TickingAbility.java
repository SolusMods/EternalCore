package io.github.solusmods.eternalcore.abilities.impl;

import io.github.solusmods.eternalcore.abilities.api.Abilities;
import io.github.solusmods.eternalcore.abilities.api.Ability;
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance;
import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

/**
 * This is the Registry Object for Ticking Abilities when a {@link Ability} is held down in specific mode.
 */
public class TickingAbility {
    private int duration = 0;
    @Getter
    private final Ability ability;
    @Getter
    private final int mode;
    public TickingAbility(Ability ability, int mode) {
        this.ability = ability;
        this.mode = mode;
    }

    public boolean tick(Abilities storage, LivingEntity entity) {
        if (!entity.isAlive()) return false;
        Optional<AbilityInstance> optional = storage.getAbility(ability);
        if (optional.isEmpty()) return false;

        AbilityInstance instance = optional.get();
        if (reachedMaxDuration(instance, entity)) return false;

        if (!instance.canInteractAbility(entity)) return false;
        return instance.onHeld(entity, this.duration++, mode);
    }

    public boolean reachedMaxDuration(AbilityInstance instance, LivingEntity entity) {
        int maxDuration = instance.getMaxHeldTime(entity);
        if (maxDuration == -1) return false;
        return duration >= maxDuration;
    }
}
