package io.github.solusmods.eternalcore.impl.realm.network.c2s;

import io.github.solusmods.eternalcore.api.realm.RealmAPI;
import io.github.solusmods.eternalcore.api.realm.Realms;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {

    static void handle(RequestRealmBreakthroughPacket packet, Player player) {
        if (player == null) return;

        Realms storage = RealmAPI.getRealmFrom(player);
        val realm = RealmAPI.getRealmRegistry().get(packet.realm());
        if (realm == null) return;

        storage.breakthroughRealm(realm);
    }
}
