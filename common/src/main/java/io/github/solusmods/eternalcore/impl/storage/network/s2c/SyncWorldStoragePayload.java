package io.github.solusmods.eternalcore.impl.storage.network.s2c;


import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.EternalCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncWorldStoragePayload(
        boolean isUpdate,
        CompoundTag storageTag
) implements StorageSyncPayload {
    public static final Type<SyncWorldStoragePayload> TYPE = new Type<>(EternalCore.create("sync_world_storage"));
    public static final StreamCodec<FriendlyByteBuf, SyncWorldStoragePayload> STREAM_CODEC = CustomPacketPayload.codec(SyncWorldStoragePayload::encode, SyncWorldStoragePayload::new);

    public SyncWorldStoragePayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isUpdate);
        buf.writeNbt(storageTag);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.CLIENT) return;
        context.queue(() -> ClientAccess.handle(this));
    }

    @Override
    public Type<SyncWorldStoragePayload> type() {
        return TYPE;
    }
}
