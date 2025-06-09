package io.github.solusmods.eternalcore.stage.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.stage.EternalCoreStage
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamMemberEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

data class RequestStageBreakthroughPacket(
    val stage: ResourceLocation?
) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readResourceLocation())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeResourceLocation(this.stage)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.getEnvironment() != Env.SERVER) return
        context.queue { ClientAccess.handle(this, context.getPlayer()) }
    }

    override fun type(): CustomPacketPayload.Type<RequestStageBreakthroughPacket?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<RequestStageBreakthroughPacket?> =
            CustomPacketPayload.Type<RequestStageBreakthroughPacket?>(EternalCoreStage.create("request_stage_breakthrough"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, RequestStageBreakthroughPacket?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, RequestStageBreakthroughPacket?>(
                { obj: RequestStageBreakthroughPacket?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                { buf: FriendlyByteBuf? ->
                    RequestStageBreakthroughPacket(
                        buf!!
                    )
                })
    }
}
