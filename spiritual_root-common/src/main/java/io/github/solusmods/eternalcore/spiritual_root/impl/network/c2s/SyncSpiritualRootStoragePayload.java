package io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record SyncSpiritualRootStoragePayload(CompoundTag data) implements CustomPacketPayload {
    public static final Type<SyncSpiritualRootStoragePayload> TYPE = new Type<>(EternalCoreSpiritualRoot.create("sync_spiritual_roots_storage"));
    public static final StreamCodec<FriendlyByteBuf, SyncSpiritualRootStoragePayload> STREAM_CODEC = CustomPacketPayload.codec(SyncSpiritualRootStoragePayload::encode, SyncSpiritualRootStoragePayload::new);

    public SyncSpiritualRootStoragePayload(FriendlyByteBuf buf) {
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
