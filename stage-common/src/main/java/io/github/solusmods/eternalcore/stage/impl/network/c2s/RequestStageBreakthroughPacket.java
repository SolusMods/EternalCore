package io.github.solusmods.eternalcore.stage.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.stage.EternalCoreStage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestStageBreakthroughPacket(
        ResourceLocation stage
) implements CustomPacketPayload {
    public static final Type<RequestStageBreakthroughPacket> TYPE = new Type<>(EternalCoreStage.create("request_stage_breakthrough"));
    public static final StreamCodec<FriendlyByteBuf, RequestStageBreakthroughPacket> STREAM_CODEC = CustomPacketPayload.codec(RequestStageBreakthroughPacket::encode, RequestStageBreakthroughPacket::new);

    public RequestStageBreakthroughPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.stage);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ClientAccess.handle(this, context.getPlayer()));
    }

    public @NotNull Type<RequestStageBreakthroughPacket> type() {
        return TYPE;
    }
}
