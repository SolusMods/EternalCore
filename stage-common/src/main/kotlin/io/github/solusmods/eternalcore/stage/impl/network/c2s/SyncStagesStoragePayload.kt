package io.github.solusmods.eternalcore.stage.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.stage.EternalCoreStage
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer

data class SyncStagesStoragePayload(val data: CompoundTag?) : CustomPacketPayload {
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
        val TYPE: CustomPacketPayload.Type<SyncStagesStoragePayload?> =
            CustomPacketPayload.Type<SyncStagesStoragePayload?>(EternalCoreStage.create("sync_stages"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, SyncStagesStoragePayload?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, SyncStagesStoragePayload?>(
                { obj: SyncStagesStoragePayload?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                { buf: FriendlyByteBuf? ->
                    SyncStagesStoragePayload(
                        buf!!
                    )
                })
    }
}
