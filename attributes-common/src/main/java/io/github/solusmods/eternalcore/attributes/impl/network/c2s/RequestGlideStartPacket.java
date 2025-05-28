package io.github.solusmods.eternalcore.attributes.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributeUtils;
import io.github.solusmods.eternalcore.network.ModuleConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record RequestGlideStartPacket() implements CustomPacketPayload {
    public static final Type<RequestGlideStartPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, "request_glide_start"));
    public static final StreamCodec<FriendlyByteBuf, RequestGlideStartPacket> STREAM_CODEC = CustomPacketPayload.codec(RequestGlideStartPacket::encode, RequestGlideStartPacket::new);

    public RequestGlideStartPacket(FriendlyByteBuf buf) {
        this();
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player == null) return;
            player.stopFallFlying();
            if (EternalCoreAttributeUtils.canElytraGlide(player, !player.isFallFlying() && !player.isInLiquid()))
                player.startFallFlying();
        });
    }

    public @NotNull Type<RequestGlideStartPacket> type() {
        return TYPE;
    }
}
