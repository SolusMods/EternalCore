package io.github.solusmods.eternalcore.attributes.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributeUtils
import io.github.solusmods.eternalcore.network.ModuleConstants
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamMemberEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

class RequestGlideStartPacket() : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf?) : this()

    fun encode(buf: FriendlyByteBuf?) {
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.environment != Env.SERVER) return
        context.queue {
            val player = context.player
            if (player == null) return@queue
            player.stopFallFlying()
            if (EternalCoreAttributeUtils.canElytraGlide(
                    player,
                    !player.isFallFlying && !player.isInLiquid
                )
            ) player.startFallFlying()
        }
    }

    override fun type(): CustomPacketPayload.Type<RequestGlideStartPacket> {
        return TYPE
    }

    companion object {
        @JvmField
        val TYPE: CustomPacketPayload.Type<RequestGlideStartPacket> =
            CustomPacketPayload.Type<RequestGlideStartPacket>(
                ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, "request_glide_start")
            )
        @JvmField
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, RequestGlideStartPacket> =
            CustomPacketPayload.codec<FriendlyByteBuf, RequestGlideStartPacket>(
                { obj: RequestGlideStartPacket, buf: FriendlyByteBuf -> obj.encode(buf) },
                { buf: FriendlyByteBuf? -> RequestGlideStartPacket(buf) })
    }
}