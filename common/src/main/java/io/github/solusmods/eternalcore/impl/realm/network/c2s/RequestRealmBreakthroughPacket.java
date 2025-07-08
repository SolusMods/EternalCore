package io.github.solusmods.eternalcore.impl.realm.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.EternalCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestRealmBreakthroughPacket(
        ResourceLocation realm
) implements CustomPacketPayload {
    public static final Type<RequestRealmBreakthroughPacket> TYPE = new Type<>(EternalCore.create("request_realm_breakthrough"));
    public static final StreamCodec<FriendlyByteBuf, RequestRealmBreakthroughPacket> STREAM_CODEC = CustomPacketPayload.codec(RequestRealmBreakthroughPacket::encode, RequestRealmBreakthroughPacket::new);

    public RequestRealmBreakthroughPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.realm);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ClientAccess.handle(this, context.getPlayer()));
    }

    public @NotNull Type<RequestRealmBreakthroughPacket> type() {
        return TYPE;
    }
}
