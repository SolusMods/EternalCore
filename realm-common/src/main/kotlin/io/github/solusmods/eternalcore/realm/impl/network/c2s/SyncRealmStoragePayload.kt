package io.github.solusmods.eternalcore.realm.impl.network.c2s

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.realm.EternalCoreRealm
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamMemberEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer


data class SyncRealmStoragePayload(val data: CompoundTag?) : CustomPacketPayload {
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
        val TYPE: CustomPacketPayload.Type<SyncRealmStoragePayload?> =
            CustomPacketPayload.Type<SyncRealmStoragePayload?>(EternalCoreRealm.create("sync_realms"))
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf?, SyncRealmStoragePayload?> =
            CustomPacketPayload.codec<FriendlyByteBuf?, SyncRealmStoragePayload?>(
                { obj: SyncRealmStoragePayload?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                { buf: FriendlyByteBuf? ->
                    SyncRealmStoragePayload(
                        buf!!
                    )
                })
    }
}
