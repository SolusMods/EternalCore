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
data class RequestAbilityTogglePacket(
    val abilityId: ResourceLocation?
) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readResourceLocation())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeResourceLocation(this.abilityId)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.getEnvironment() != Env.SERVER) return
        context.queue(Runnable { ClientAccess.handle(this, context.getPlayer()) })
    }

    override fun type(): CustomPacketPayload.Type<RequestAbilityTogglePacket?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<RequestAbilityTogglePacket?> =
            CustomPacketPayload.Type<RequestAbilityTogglePacket?>(EternalCoreAbilities.create("request_ability_toggle"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, RequestAbilityTogglePacket?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, RequestAbilityTogglePacket?>(
                StreamMemberEncoder { obj: RequestAbilityTogglePacket?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                StreamDecoder { buf: FriendlyByteBuf? ->
                    RequestAbilityTogglePacket(
                        buf!!
                    )
                })
    }
}
