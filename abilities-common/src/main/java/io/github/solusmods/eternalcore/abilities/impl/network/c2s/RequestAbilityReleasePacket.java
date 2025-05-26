package io.github.solusmods.eternalcore.abilities.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player == null) return;
            AbilityStorage storage = StorageManager.getStorage(player, AbilityStorage.getKey());
            if (storage == null) return;
            storage.handleAbilityRelease(abilityId, heldTick, keyNumber, mode);
        });
    }

    public @NotNull Type<RequestAbilityReleasePacket> type() {
        return TYPE;
    }
}
