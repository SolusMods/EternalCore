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
data class RequestAbilityActivationPacket(
    val keyNumber: Int,
    val abilityId: ResourceLocation?,
    val mode: Int
) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readResourceLocation(), buf.readInt())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(this.keyNumber)
        buf.writeResourceLocation(this.abilityId)
        buf.writeInt(this.mode)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.getEnvironment() != Env.SERVER) return
        context.queue(Runnable { ClientAccess.handle(this, context.getPlayer()) })
    }


    override fun type(): CustomPacketPayload.Type<RequestAbilityActivationPacket?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<RequestAbilityActivationPacket?> =
            CustomPacketPayload.Type<RequestAbilityActivationPacket?>(EternalCoreAbilities.create("request_ability_activation"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, RequestAbilityActivationPacket?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, RequestAbilityActivationPacket?>(
                StreamMemberEncoder { obj: RequestAbilityActivationPacket?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                StreamDecoder { buf: FriendlyByteBuf? ->
                    RequestAbilityActivationPacket(
                        buf!!
                    )
                })
    }
}
