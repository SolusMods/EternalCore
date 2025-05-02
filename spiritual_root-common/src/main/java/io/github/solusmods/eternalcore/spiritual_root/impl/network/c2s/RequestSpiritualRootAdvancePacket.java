package io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot;
import io.github.solusmods.eternalcore.spiritual_root.ModuleConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
        context.queue(() -> {
//            Player player = context.getPlayer();
//            if (player == null) return;
//
//            Realms storage = RealmAPI.getRealmFrom(player);
//            Optional<RealmInstance> optional = storage.getRealm();
//            if (optional.isEmpty()) return;
//
//            Realm realm1 = RealmAPI.getRealmRegistry().get(spiritual_root);
//            if (realm1 == null) return;
//
//            RealmInstance instance = optional.get();
//            if (!instance.getNextBreakthroughs(player).contains(realm1)) {
//            }

//            storage.evolveRace(realm1);
        });
    }

    public @NotNull Type<RequestSpiritualRootAdvancePacket> type() {
        return TYPE;
    }
}
