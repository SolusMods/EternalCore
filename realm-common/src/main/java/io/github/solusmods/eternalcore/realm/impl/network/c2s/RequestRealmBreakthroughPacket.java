package io.github.solusmods.eternalcore.realm.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.realm.EternalCoreRealm;
import io.github.solusmods.eternalcore.realm.api.Realm;
import io.github.solusmods.eternalcore.realm.api.RealmAPI;
import io.github.solusmods.eternalcore.realm.api.RealmInstance;
import io.github.solusmods.eternalcore.realm.api.Realms;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RequestRealmBreakthroughPacket(
        ResourceLocation realm
) implements CustomPacketPayload {
    public static final Type<RequestRealmBreakthroughPacket> TYPE = new Type<>(EternalCoreRealm.create("request_realm_breakthrough"));
    public static final StreamCodec<FriendlyByteBuf, RequestRealmBreakthroughPacket> STREAM_CODEC = CustomPacketPayload.codec(RequestRealmBreakthroughPacket::encode, RequestRealmBreakthroughPacket::new);

    public RequestRealmBreakthroughPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.realm);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player == null) return;

            Realms storage = RealmAPI.getRealmFrom(player);
            Optional<RealmInstance> optional = storage.getRealm();
            if (optional.isEmpty()) return;

            Realm realm1 = RealmAPI.getRealmRegistry().get(realm);
            if (realm1 == null) return;

            RealmInstance instance = optional.get();
            if (!instance.getNextBreakthroughs(player).contains(realm1)) {
            }

//            storage.evolveRace(realm1);
        });
    }

    public @NotNull Type<RequestRealmBreakthroughPacket> type() {
        return TYPE;
    }
}
