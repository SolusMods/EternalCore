package io.github.solusmods.eternalcore.abilities.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamEncoder
import net.minecraft.network.codec.StreamMemberEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

@JvmRecord
data class RequestAbilityScrollPacket(
    val delta: Double,
    val abilityList: MutableList<ResourceLocation?>?
) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(
        buf.readDouble(), validateList(
            buf.readList<ResourceLocation?>(
                StreamDecoder { obj: FriendlyByteBuf? -> obj!!.readResourceLocation() })
        )
    )

    fun encode(buf: FriendlyByteBuf) {
        buf.writeDouble(this.delta)
        buf.writeCollection<ResourceLocation?>(
            this.abilityList,
            StreamEncoder { obj: FriendlyByteBuf?, resourceLocation: ResourceLocation? ->
                obj!!.writeResourceLocation(resourceLocation)
            })
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.getEnvironment() != Env.SERVER) return
        context.queue(Runnable { ClientAccess.handle(this, context.getPlayer()) })
    }

    override fun type(): CustomPacketPayload.Type<RequestAbilityScrollPacket?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<RequestAbilityScrollPacket?> =
            CustomPacketPayload.Type<RequestAbilityScrollPacket?>(EternalCoreAbilities.create("request_ability_scroll"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, RequestAbilityScrollPacket?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, RequestAbilityScrollPacket?>(
                StreamMemberEncoder { obj: RequestAbilityScrollPacket?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                StreamDecoder { buf: FriendlyByteBuf? ->
                    RequestAbilityScrollPacket(
                        buf!!
                    )
                })

        private fun validateList(list: MutableList<ResourceLocation?>): MutableList<ResourceLocation?> {
            val maxSize = 100
            require(list.size <= maxSize) { "Ability list exceeds maximum size of " + maxSize }
            return list
        }
    }
}
