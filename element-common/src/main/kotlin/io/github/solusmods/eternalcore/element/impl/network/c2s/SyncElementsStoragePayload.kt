package io.github.solusmods.eternalcore.element.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.element.EternalCoreElements
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamMemberEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer

@JvmRecord
data class SyncElementsStoragePayload(val data: CompoundTag?) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readNbt())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(data)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.environment != Env.SERVER) return
        context.queue { ClientAccess.handle(this, context.player as ServerPlayer) }
    }

    /**
     * @return
     */
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncElementsStoragePayload?> =
            CustomPacketPayload.Type<SyncElementsStoragePayload?>(EternalCoreElements.create("sync_elements"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, SyncElementsStoragePayload?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, SyncElementsStoragePayload?>(
                { obj: SyncElementsStoragePayload?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                { buf: FriendlyByteBuf? ->
                    SyncElementsStoragePayload(
                        buf!!
                    )
                })
    }
}
