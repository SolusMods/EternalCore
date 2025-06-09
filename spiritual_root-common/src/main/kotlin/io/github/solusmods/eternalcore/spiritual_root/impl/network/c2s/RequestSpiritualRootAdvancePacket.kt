package io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

@JvmRecord
data class RequestSpiritualRootAdvancePacket(
    val spiritual_root: ResourceLocation?
) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readResourceLocation())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeResourceLocation(this.spiritual_root)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.environment != Env.SERVER) return
        context.queue { ClientAccess.handle(this, context.player) }
    }

    override fun type(): CustomPacketPayload.Type<RequestSpiritualRootAdvancePacket?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<RequestSpiritualRootAdvancePacket?> =
            CustomPacketPayload.Type<RequestSpiritualRootAdvancePacket?>(EternalCoreSpiritualRoot.create("request_realm_breakthrough"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, RequestSpiritualRootAdvancePacket?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, RequestSpiritualRootAdvancePacket?>(
                { obj: RequestSpiritualRootAdvancePacket?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                { buf: FriendlyByteBuf? ->
                    RequestSpiritualRootAdvancePacket(
                        buf!!
                    )
                })
    }
}
