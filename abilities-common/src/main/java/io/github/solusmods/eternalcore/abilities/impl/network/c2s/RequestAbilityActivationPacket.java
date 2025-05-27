package io.github.solusmods.eternalcore.abilities.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RequestAbilityActivationPacket(int keyNumber,
                                             ResourceLocation abilityId,
                                             int mode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RequestAbilityActivationPacket> TYPE = new CustomPacketPayload.Type<>(EternalCoreAbilities.create("request_ability_activation"));
    public static final StreamCodec<FriendlyByteBuf, RequestAbilityActivationPacket> STREAM_CODEC = CustomPacketPayload.codec(RequestAbilityActivationPacket::encode, RequestAbilityActivationPacket::new);

    public RequestAbilityActivationPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readResourceLocation(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.keyNumber);
        buf.writeResourceLocation(this.abilityId);
        buf.writeInt(this.mode);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ClientAccess.handle(this, context.getPlayer()));
    }



    public @NotNull Type<RequestAbilityActivationPacket> type() {
        return TYPE;
    }
}
