package io.github.solusmods.eternalcore.storage.impl.network.s2c

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.network.ModuleConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ChunkPos


@JvmRecord
data class SyncChunkStoragePayload(
    override val isUpdate: Boolean,
    val chunkPos: ChunkPos,
    override val storageTag: CompoundTag
) : StorageSyncPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readBoolean(), buf.readChunkPos(), buf.readNbt()!!)

    fun encode(buf: FriendlyByteBuf) {
        buf.writeBoolean(isUpdate)
        buf.writeChunkPos(chunkPos)
        buf.writeNbt(storageTag)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.environment != Env.CLIENT) return
        context.queue { ClientAccess.handle(this) }
    }

    override fun type(): CustomPacketPayload.Type<SyncChunkStoragePayload> {
        return TYPE
    }

    companion object {
        @JvmField
        val TYPE: CustomPacketPayload.Type<SyncChunkStoragePayload> =
            CustomPacketPayload.Type<SyncChunkStoragePayload>(
                ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, "sync_chunk_storage")
            )
        @JvmField
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, SyncChunkStoragePayload> =
            CustomPacketPayload.codec<FriendlyByteBuf, SyncChunkStoragePayload>(
                { obj: SyncChunkStoragePayload, buf: FriendlyByteBuf -> obj.encode(buf) },
                { buf: FriendlyByteBuf ->
                    SyncChunkStoragePayload(
                        buf
                    )
                })
    }
}