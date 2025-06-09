package io.github.solusmods.eternalcore.attributes.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.attributes.impl.network.c2s.RequestGlideStartPacket
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils.registerC2SPayload
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object EternalCoreAttributeNetwork {
    fun init() {
        registerC2SPayload(
            RequestGlideStartPacket.Companion.TYPE as CustomPacketPayload.Type<RequestGlideStartPacket?>,
            RequestGlideStartPacket.Companion.STREAM_CODEC as StreamCodec<in RegistryFriendlyByteBuf?, RequestGlideStartPacket?>?
        ) { obj: RequestGlideStartPacket?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
    }
}