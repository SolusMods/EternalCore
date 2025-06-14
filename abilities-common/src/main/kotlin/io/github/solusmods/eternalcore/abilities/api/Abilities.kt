package io.github.solusmods.eternalcore.abilities.api

import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import java.util.*
import java.util.function.BiConsumer

interface Abilities {
    fun markDirty()

    val learnedAbilities: MutableCollection<AbilityInstance>

    /**
     * Updates a ability instance and optionally synchronizes the change across the network.
     *
     *
     * @param updatedInstance The instance to update
     * @param sync If true, synchronizes the change to all clients/server
     */
    fun updateAbility(updatedInstance: AbilityInstance, sync: Boolean)

    fun learnAbility(abilityId: ResourceLocation): Boolean {
        return learnAbility(AbilityAPI.abilityRegistry!!.get(abilityId)!!.createDefaultInstance())
    }

    fun learnAbility(abilityId: ResourceLocation, component: MutableComponent?): Boolean {
        return learnAbility(AbilityAPI.abilityRegistry!!.get(abilityId)!!.createDefaultInstance(), component)
    }

    fun learnAbility(ability: Ability): Boolean {
        return learnAbility(ability.createDefaultInstance())
    }

    fun learnAbility(ability: Ability, component: MutableComponent?): Boolean {
        return learnAbility(ability.createDefaultInstance(), component)
    }

    fun learnAbility(instance: AbilityInstance): Boolean {
        return learnAbility(
            instance,
            Component.translatable("eternalcore.ability.learn_ability", instance.getChatDisplayName(true))
        )
    }

    fun learnAbility(instance: AbilityInstance, component: MutableComponent?): Boolean

    fun getAbility(abilityId: ResourceLocation): Optional<AbilityInstance?>?

    fun getAbility(ability: Ability): Optional<AbilityInstance?>? {
        return getAbility(ability.registryName!!)
    }

    fun forgetAbility(abilityId: ResourceLocation, component: MutableComponent?)

    fun forgetAbility(abilityId: ResourceLocation) {
        forgetAbility(abilityId, null)
    }

    fun forgetAbility(ability: Ability, component: MutableComponent?) {
        forgetAbility(ability.registryName!!, component)
    }

    fun forgetAbility(ability: Ability) {
        forgetAbility(ability.registryName!!)
    }

    fun forgetAbility(instance: AbilityInstance, component: MutableComponent?) {
        forgetAbility(instance.abilityId!!, component)
    }

    fun forgetAbility(instance: AbilityInstance) {
        forgetAbility(instance.abilityId!!)
    }

    fun forEachAbility(abilityInstanceConsumer: BiConsumer<AbilityStorage, AbilityInstance>)
}
