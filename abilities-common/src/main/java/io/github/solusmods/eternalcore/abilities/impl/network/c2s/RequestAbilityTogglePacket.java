package io.github.solusmods.eternalcore.abilities.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestAbilityTogglePacket(
        ResourceLocation abilityId
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestAbilityTogglePacket> TYPE = new CustomPacketPayload.Type<>(EternalCoreAbilities.create("request_ability_toggle"));
    public static final StreamCodec<FriendlyByteBuf, RequestAbilityTogglePacket> STREAM_CODEC = CustomPacketPayload.codec(RequestAbilityTogglePacket::encode, RequestAbilityTogglePacket::new);

    public RequestAbilityTogglePacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.abilityId);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ClientAccess.handle(this, context.getPlayer()));
    }

    public @NotNull Type<RequestAbilityTogglePacket> type() {
        return TYPE;
    }
}
