package io.github.solusmods.eternalcore.abilities.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestAbilityReleasePacket(
        int heldTick,
        int keyNumber,
        int mode,
        ResourceLocation abilityId
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestAbilityReleasePacket> TYPE = new CustomPacketPayload.Type<>(EternalCoreAbilities.create("request_ability_release"));
    public static final StreamCodec<FriendlyByteBuf, RequestAbilityReleasePacket> STREAM_CODEC = CustomPacketPayload.codec(RequestAbilityReleasePacket::encode, RequestAbilityReleasePacket::new);

    public RequestAbilityReleasePacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.heldTick);
        buf.writeInt(this.keyNumber);
        buf.writeInt(this.mode);
        buf.writeResourceLocation(this.abilityId);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ClientAccess.handle(this, context.getPlayer()));
    }

    public @NotNull Type<RequestAbilityReleasePacket> type() {
        return TYPE;
    }
}
