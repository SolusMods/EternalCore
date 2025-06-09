package io.github.solusmods.eternalcore.storage.impl.network.s2c

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.network.ModuleConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation


@JvmRecord
data class SyncEntityStoragePayload(
    override val isUpdate: Boolean,
    val entityId: Int,
    override val storageTag: CompoundTag
) : StorageSyncPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readBoolean(), buf.readInt(), buf.readNbt()!!)

    fun encode(buf: FriendlyByteBuf) {
        buf.writeBoolean(isUpdate)
        buf.writeInt(entityId)
        buf.writeNbt(storageTag)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.environment != Env.CLIENT) return
        context.queue { ClientAccess.handle(this) }
    }

    override fun type(): CustomPacketPayload.Type<SyncEntityStoragePayload> {
        return TYPE
    }

    companion object {
        @JvmField
        val TYPE: CustomPacketPayload.Type<SyncEntityStoragePayload> =
            CustomPacketPayload.Type<SyncEntityStoragePayload>(
                ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, "sync_entity_storage")
            )
        @JvmField
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, SyncEntityStoragePayload> =
            CustomPacketPayload.codec<FriendlyByteBuf, SyncEntityStoragePayload>(
                { obj: SyncEntityStoragePayload, buf: FriendlyByteBuf -> obj.encode(buf) },
                { buf: FriendlyByteBuf ->
                    SyncEntityStoragePayload(
                        buf
                    )
                })
    }
}