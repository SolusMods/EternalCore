package io.github.solusmods.eternalcore.abilities.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RequestAbilityScrollPacket(
        double delta,
        List<ResourceLocation> abilityList
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestAbilityScrollPacket> TYPE = new CustomPacketPayload.Type<>(EternalCoreAbilities.create("request_ability_scroll"));
    public static final StreamCodec<FriendlyByteBuf, RequestAbilityScrollPacket> STREAM_CODEC = CustomPacketPayload.codec(RequestAbilityScrollPacket::encode, RequestAbilityScrollPacket::new);

    public RequestAbilityScrollPacket(FriendlyByteBuf buf) {
        this(buf.readDouble(), validateList(buf.readList(FriendlyByteBuf::readResourceLocation)));
    }

    private static List<ResourceLocation> validateList(List<ResourceLocation> list) {
        int maxSize = 100;
        if (list.size() > maxSize) throw new IllegalArgumentException("Ability list exceeds maximum size of " + maxSize);
        return list;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.delta);
        buf.writeCollection(this.abilityList, FriendlyByteBuf::writeResourceLocation);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> ClientAccess.handle(this, context.getPlayer()));
    }

    public @NotNull Type<RequestAbilityScrollPacket> type() {
        return TYPE;
    }
}
