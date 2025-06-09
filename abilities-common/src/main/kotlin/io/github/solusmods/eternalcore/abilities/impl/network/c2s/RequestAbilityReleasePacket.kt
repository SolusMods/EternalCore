package io.github.solusmods.eternalcore.abilities.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamMemberEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

@JvmRecord
data class RequestAbilityReleasePacket(
    val heldTick: Int,
    val keyNumber: Int,
    val mode: Int,
    val abilityId: ResourceLocation?
) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readResourceLocation())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(this.heldTick)
        buf.writeInt(this.keyNumber)
        buf.writeInt(this.mode)
        buf.writeResourceLocation(this.abilityId)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.getEnvironment() != Env.SERVER) return
        context.queue(Runnable { ClientAccess.handle(this, context.getPlayer()) })
    }

    override fun type(): CustomPacketPayload.Type<RequestAbilityReleasePacket?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<RequestAbilityReleasePacket?> =
            CustomPacketPayload.Type<RequestAbilityReleasePacket?>(EternalCoreAbilities.create("request_ability_release"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, RequestAbilityReleasePacket?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, RequestAbilityReleasePacket?>(
                StreamMemberEncoder { obj: RequestAbilityReleasePacket?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                StreamDecoder { buf: FriendlyByteBuf? ->
                    RequestAbilityReleasePacket(
                        buf!!
                    )
                })
    }
}
