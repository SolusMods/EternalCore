package io.github.solusmods.eternalcore.realm.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.realm.EternalCoreRealm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record SyncRealmStoragePayload(CompoundTag data) implements CustomPacketPayload {
    public static final Type<SyncRealmStoragePayload> TYPE = new Type<>(EternalCoreRealm.create("sync_realms"));
    public static final StreamCodec<FriendlyByteBuf, SyncRealmStoragePayload> STREAM_CODEC = CustomPacketPayload.codec(SyncRealmStoragePayload::encode, SyncRealmStoragePayload::new);

    public SyncRealmStoragePayload(FriendlyByteBuf buf) {
        this(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ClientAccess.handle(this, (ServerPlayer) context.getPlayer()));
    }

    /**
     * @return 
     */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
