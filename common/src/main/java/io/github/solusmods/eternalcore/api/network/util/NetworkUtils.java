package io.github.solusmods.eternalcore.api.network.util;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@UtilityClass
public class NetworkUtils {
    /**
     * Registers a custom S2C payload.
     */
    public static <T extends CustomPacketPayload> void registerS2CPayload(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> receiver) {
        if (Platform.getEnv() == EnvType.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), id, codec, receiver);
        } else {
            NetworkManager.registerS2CPayloadType(id, codec);
        }
    }

    /**
     * Registers a custom C2S payload.
     */
    public static <T extends CustomPacketPayload> void registerC2SPayload(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> receiver) {
        NetworkManager.registerReceiver(NetworkManager.c2s(), id, codec, receiver);
    }
}
