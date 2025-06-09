package io.github.solusmods.eternalcore.spiritual_root.api

import dev.architectury.platform.Platform
import dev.architectury.registry.registries.Registrar
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootRegistry
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootStorage
import io.github.solusmods.eternalcore.spiritual_root.impl.network.InternalSpiritualRootPacketActions
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity

object SpiritualRootAPI {
    val spiritualRootRegistry: Registrar<SpiritualRoot>
        /**
         * This Method returns the [SpiritualRoot] Registry.
         * It can be used to load [SpiritualRoot]s from the Registry.
         */
        get() = SpiritualRootRegistry.SPIRITUAL_ROOTS

    val spiritualRootRegistryKey: ResourceKey<Registry<SpiritualRoot>>
        /**
         * This Method returns the Registry Key of the [SpiritualRootRegistry].
         * It can be used to create [DeferredRegister] instances
         */
        get() = SpiritualRootRegistry.KEY

    /**
     * Can be used to load the [SpiritualRootStorage] from an [LivingEntity].
     */
    fun getSpiritualRootFrom(entity: LivingEntity): SpiritualRoots? {
        return entity.getStorage<SpiritualRootStorage?>(SpiritualRootStorage.key)
    }

    /**
     * Send [InternalSpiritualRootPacketActions.sendSpiritualRootAdvancePacket] with a DistExecutor on client side.
     * Used when player advance his a spiritualRoot.
     *
     * @see InternalSpiritualRootPacketActions.sendSpiritualRootAdvancePacket
     */
    @JvmStatic
    fun spiritualRootAdvancePacket(location: ResourceLocation?) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalSpiritualRootPacketActions.sendSpiritualRootAdvancePacket(location)
        }
    }
}
