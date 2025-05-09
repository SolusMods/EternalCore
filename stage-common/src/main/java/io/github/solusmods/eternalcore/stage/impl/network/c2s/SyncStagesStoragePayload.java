package io.github.solusmods.eternalcore.stage.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.stage.EternalCoreStage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record SyncStagesStoragePayload(CompoundTag data) implements CustomPacketPayload {
    public static final Type<SyncStagesStoragePayload> TYPE = new Type<>(EternalCoreStage.create("sync_stages"));
    public static final StreamCodec<FriendlyByteBuf, SyncStagesStoragePayload> STREAM_CODEC = CustomPacketPayload.codec(SyncStagesStoragePayload::encode, SyncStagesStoragePayload::new);

    public SyncStagesStoragePayload(FriendlyByteBuf buf) {
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
