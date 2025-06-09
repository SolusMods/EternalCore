package io.github.solusmods.eternalcore.abilities.impl

import io.github.solusmods.eternalcore.abilities.api.Abilities
import io.github.solusmods.eternalcore.abilities.api.Ability
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance
import lombok.Getter
import net.minecraft.world.entity.LivingEntity

/**
 * This is the Registry Object for Ticking Abilities when a [Ability] is held down in specific mode.
 */
class TickingAbility(val ability: Ability, val mode: Int) {
    private var duration = 0

    fun tick(storage: Abilities, entity: LivingEntity): Boolean {
        if (!entity.isAlive) return false
        val optional = storage.getAbility(ability)
        if (optional!!.isEmpty) return false

        val instance = optional.get()
        if (reachedMaxDuration(instance, entity)) return false

        if (!instance.canInteractAbility(entity)) return false
        return instance.onHeld(entity, this.duration++, mode)
    }

    fun reachedMaxDuration(instance: AbilityInstance, entity: LivingEntity?): Boolean {
        val maxDuration = instance.getMaxHeldTime(entity)
        if (maxDuration == -1) return false
        return duration >= maxDuration
    }
}
