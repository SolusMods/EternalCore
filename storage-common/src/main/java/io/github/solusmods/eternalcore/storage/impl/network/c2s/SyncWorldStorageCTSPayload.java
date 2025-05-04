package io.github.solusmods.eternalcore.storage.impl.network.c2s;


import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.storage.impl.network.s2c.StorageSyncPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static io.github.solusmods.eternalcore.network.ModuleConstants.MOD_ID;

public record SyncWorldStorageCTSPayload(
        boolean isUpdate,
        CompoundTag storageTag
) implements StorageSyncPayload {
    public static final Type<SyncWorldStorageCTSPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "sync_world_c2s_storage"));
    public static final StreamCodec<FriendlyByteBuf, SyncWorldStorageCTSPayload> STREAM_CODEC = CustomPacketPayload.codec(SyncWorldStorageCTSPayload::encode, SyncWorldStorageCTSPayload::new);

    public SyncWorldStorageCTSPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isUpdate);
        buf.writeNbt(storageTag);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ServerAccess.handle(this));
    }

    @Override
    public Type<SyncWorldStorageCTSPayload> type() {
        return TYPE;
    }
}
