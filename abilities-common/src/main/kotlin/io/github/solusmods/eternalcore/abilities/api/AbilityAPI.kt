package io.github.solusmods.eternalcore.abilities.api

import dev.architectury.platform.Platform
import dev.architectury.registry.registries.Registrar
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.abilities.impl.AbilityRegistry
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage
import io.github.solusmods.eternalcore.abilities.impl.network.InternalAbilityPacketActions
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import java.util.Optional


object AbilityAPI {

    /**
     * This Method returns the [Ability] Registry.
     * It can be used to load [Ability]s from the Registry.
     */
    @JvmField
    val abilityRegistry: Registrar<Ability> = AbilityRegistry.ABILITIES



    /**
     * This Method returns the Registry Key of the [AbilityRegistry].
     * It can be used to create [dev.architectury.registry.registries.DeferredRegister] instances
     */
    @JvmField
    val abilityRegistryKey: ResourceKey<Registry<Ability>> = AbilityRegistry.KEY


    /**
     * Can be used to load the [AbilityStorage] from an [LivingEntity].
     */
    @JvmStatic
    fun getAbilitiesFrom(entity: LivingEntity): Abilities? {
        return entity.getStorage(AbilityStorage.key)
    }

    @JvmStatic
    fun getStorageOptional(entity: LivingEntity): Optional<AbilityStorage> {
        return entity.getStorageOptional(AbilityStorage.key)
    }

    /**
     * Send [InternalAbilityPacketActions.sendAbilityActivationPacket] with a DistExecutor on client side.
     * Used when player press an ability activation key bind.
     *
     * @see InternalAbilityPacketActions.sendAbilityActivationPacket
     */
    @JvmStatic
    fun abilityActivationPacket(ability: ResourceLocation?, keyNumber: Int, mode: Int) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalAbilityPacketActions.sendAbilityActivationPacket(ability, keyNumber, mode)
        }
    }

    /**
     * Send [InternalAbilityPacketActions.sendAbilityReleasePacket] with a DistExecutor on client side.
     * Used when player release an ability activation key bind.
     *
     * @see InternalAbilityPacketActions.sendAbilityReleasePacket
     */
    @JvmStatic
    fun abilityReleasePacket(ability: ResourceLocation?, keyNumber: Int, mode: Int, heldTicks: Int) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalAbilityPacketActions.sendAbilityReleasePacket(ability, keyNumber, mode, heldTicks)
        }
    }

    /**
     * Send [InternalAbilityPacketActions.sendAbilityTogglePacket] with a DistExecutor on client side.
     * Used when player press a ability toggle key bind.
     *
     * @see InternalAbilityPacketActions.sendAbilityTogglePacket
     */
    @JvmStatic
    fun abilityTogglePacket(ability: ResourceLocation?) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalAbilityPacketActions.sendAbilityTogglePacket(ability)
        }
    }
}
