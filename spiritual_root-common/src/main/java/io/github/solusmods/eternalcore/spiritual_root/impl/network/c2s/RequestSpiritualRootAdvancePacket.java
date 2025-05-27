package io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestSpiritualRootAdvancePacket(
        ResourceLocation spiritual_root
) implements CustomPacketPayload {
    public static final Type<RequestSpiritualRootAdvancePacket> TYPE = new Type<>(EternalCoreSpiritualRoot.create("request_realm_breakthrough"));
    public static final StreamCodec<FriendlyByteBuf, RequestSpiritualRootAdvancePacket> STREAM_CODEC = CustomPacketPayload.codec(RequestSpiritualRootAdvancePacket::encode, RequestSpiritualRootAdvancePacket::new);

    public RequestSpiritualRootAdvancePacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.spiritual_root);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ClientAccess.handle(this, context.getPlayer()));
    }

    public @NotNull Type<RequestSpiritualRootAdvancePacket> type() {
        return TYPE;
    }
}
