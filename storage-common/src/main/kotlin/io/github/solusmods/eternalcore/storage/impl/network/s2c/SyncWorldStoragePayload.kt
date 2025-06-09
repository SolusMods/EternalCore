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
data class SyncWorldStoragePayload(
    override val isUpdate: Boolean,
    override val storageTag: CompoundTag
) : StorageSyncPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readBoolean(), buf.readNbt()!!)

    fun encode(buf: FriendlyByteBuf) {
        buf.writeBoolean(isUpdate)
        buf.writeNbt(storageTag)
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.environment != Env.CLIENT) return
        context.queue { ClientAccess.handle(this) }
    }

    override fun type(): CustomPacketPayload.Type<SyncWorldStoragePayload> {
        return TYPE
    }

    companion object {
        @JvmField
        val TYPE: CustomPacketPayload.Type<SyncWorldStoragePayload> =
            CustomPacketPayload.Type<SyncWorldStoragePayload>(
                ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, "sync_world_storage")
            )
        @JvmField
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, SyncWorldStoragePayload> =
            CustomPacketPayload.codec<FriendlyByteBuf, SyncWorldStoragePayload>(
                { obj: SyncWorldStoragePayload, buf: FriendlyByteBuf -> obj.encode(buf) },
                { buf: FriendlyByteBuf ->
                    SyncWorldStoragePayload(
                        buf
                    )
                })
    }
}