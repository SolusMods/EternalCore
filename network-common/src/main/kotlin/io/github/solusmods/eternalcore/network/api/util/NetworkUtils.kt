package io.github.solusmods.eternalcore.network.api.util

import dev.architectury.networking.NetworkManager
import dev.architectury.platform.Platform
import lombok.experimental.UtilityClass
import net.fabricmc.api.EnvType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object NetworkUtils {
    /**
     * Registers a custom S2C payload.
     */
    @JvmStatic
    fun <T : CustomPacketPayload> registerS2CPayload(
        id: CustomPacketPayload.Type<T>,
        codec: StreamCodec<in RegistryFriendlyByteBuf, T>,
        receiver: NetworkManager.NetworkReceiver<T>
    ) {
        if (Platform.getEnv() == EnvType.CLIENT) {
            NetworkManager.registerReceiver<T?>(NetworkManager.s2c(), id, codec, receiver)
        } else {
            NetworkManager.registerS2CPayloadType<T?>(id, codec)
        }
    }

    /**
     * Registers a custom C2S payload.
     */
    @JvmStatic
    fun <T : CustomPacketPayload?> registerC2SPayload(
        id: CustomPacketPayload.Type<T?>,
        codec: StreamCodec<in RegistryFriendlyByteBuf?, T?>?,
        receiver: NetworkManager.NetworkReceiver<T?>
    ) {
        NetworkManager.registerReceiver<T?>(NetworkManager.c2s(), id, codec, receiver)
    }
}
