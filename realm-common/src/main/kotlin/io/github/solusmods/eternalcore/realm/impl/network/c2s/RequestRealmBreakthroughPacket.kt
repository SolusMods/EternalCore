package io.github.solusmods.eternalcore.realm.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.realm.EternalCoreRealm
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamMemberEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation


data class RequestRealmBreakthroughPacket(
    val realm: ResourceLocation?
) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readResourceLocation())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeResourceLocation(this.realm)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.getEnvironment() != Env.SERVER) return
        context.queue(Runnable { ClientAccess.handle(this, context.getPlayer()) })
    }

    override fun type(): CustomPacketPayload.Type<RequestRealmBreakthroughPacket?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<RequestRealmBreakthroughPacket?> =
            CustomPacketPayload.Type<RequestRealmBreakthroughPacket?>(EternalCoreRealm.create("request_realm_breakthrough"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, RequestRealmBreakthroughPacket?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, RequestRealmBreakthroughPacket?>(
                { obj: RequestRealmBreakthroughPacket?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                { buf: FriendlyByteBuf? ->
                    RequestRealmBreakthroughPacket(
                        buf!!
                    )
                })
    }
}
