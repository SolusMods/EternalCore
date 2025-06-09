package io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer


data class SyncSpiritualRootStoragePayload(val data: CompoundTag?) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readNbt())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(data)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.environment != Env.SERVER) return
        context.queue { ClientAccess.handle(this, context.player as ServerPlayer?) }
    }

    /**
     * @return
     */
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncSpiritualRootStoragePayload?> =
            CustomPacketPayload.Type<SyncSpiritualRootStoragePayload?>(EternalCoreSpiritualRoot.create("sync_spiritual_roots_storage"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, SyncSpiritualRootStoragePayload?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, SyncSpiritualRootStoragePayload?>(
                { obj: SyncSpiritualRootStoragePayload?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                { buf: FriendlyByteBuf? ->
                    SyncSpiritualRootStoragePayload(
                        buf!!
                    )
                })
    }
}
