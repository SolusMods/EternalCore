package io.github.solusmods.eternalcore.storage.impl.network.s2c;


import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;

import static io.github.solusmods.eternalcore.network.ModuleConstants.MOD_ID;

public record SyncChunkStoragePayload(
        boolean isUpdate,
        ChunkPos chunkPos,
        CompoundTag storageTag
) implements StorageSyncPayload {
    public static final Type<SyncChunkStoragePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sync_chunk_storage"));
    public static final StreamCodec<FriendlyByteBuf, SyncChunkStoragePayload> STREAM_CODEC = CustomPacketPayload.codec(SyncChunkStoragePayload::encode, SyncChunkStoragePayload::new);

    public SyncChunkStoragePayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readChunkPos(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isUpdate);
        buf.writeChunkPos(chunkPos);
        buf.writeNbt(storageTag);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.CLIENT) return;
        context.queue(() -> ClientAccess.handle(this));
    }

    @Override
    public Type<SyncChunkStoragePayload> type() {
        return TYPE;
    }
}
