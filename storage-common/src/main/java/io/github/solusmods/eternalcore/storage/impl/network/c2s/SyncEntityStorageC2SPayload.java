package io.github.solusmods.eternalcore.storage.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static io.github.solusmods.eternalcore.network.ModuleConstants.MOD_ID;

public record SyncEntityStorageC2SPayload(boolean isUpdate, int entityId, CompoundTag storageTag, StorageKey<? extends Storage> storageKey) implements StoragesSyncPayload {
    public static final Type<SyncEntityStorageC2SPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sync_entity_c2s_storage"));
    public static final StreamCodec<FriendlyByteBuf, SyncEntityStorageC2SPayload> STREAM_CODEC = CustomPacketPayload.codec(SyncEntityStorageC2SPayload::encode, SyncEntityStorageC2SPayload::new);

    public SyncEntityStorageC2SPayload(FriendlyByteBuf buf) {
        this(
                buf.readBoolean(),
                buf.readInt(),
                buf.readNbt(),
                new StorageKey<>(buf.readResourceLocation(), Storage.class) // Спрощуємо передачу класу
        );
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isUpdate);
        buf.writeInt(entityId);
        buf.writeNbt(storageTag);
        buf.writeResourceLocation(storageKey.id());
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ServerAccess.handle(this));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public StorageKey<?> key() {
        return storageKey;
    }
}