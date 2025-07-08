package io.github.solusmods.eternalcore.impl.storage.network.s2c;


import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.EternalCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncEntityStoragePayload(
        boolean isUpdate,
        int entityId,
        CompoundTag storageTag
) implements StorageSyncPayload {
    public static final Type<SyncEntityStoragePayload> TYPE = new Type<>(EternalCore.create("sync_entity_storage"));
    public static final StreamCodec<FriendlyByteBuf, SyncEntityStoragePayload> STREAM_CODEC = CustomPacketPayload.codec(SyncEntityStoragePayload::encode, SyncEntityStoragePayload::new);

    public SyncEntityStoragePayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readInt(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isUpdate);
        buf.writeInt(entityId);
        buf.writeNbt(storageTag);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.CLIENT) return;
        context.queue(() -> ClientAccess.handle(this));
    }

    @Override
    public Type<SyncEntityStoragePayload> type() {
        return TYPE;
    }
}
