package io.github.solusmods.eternalcore.realm.impl.network.c2s;

import io.github.solusmods.eternalcore.realm.api.Realm;
import io.github.solusmods.eternalcore.realm.api.RealmAPI;
import io.github.solusmods.eternalcore.realm.api.RealmInstance;
import io.github.solusmods.eternalcore.realm.api.Realms;
import io.github.solusmods.eternalcore.realm.impl.RealmStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {


    static void handle(SyncRealmStoragePayload packet, ServerPlayer player) {
        player.eternalCore$getStorageOptional(RealmStorage.getKey()).
                ifPresent(storage -> storage.load(packet.data()));
    }


    static void handle(RequestRealmBreakthroughPacket packet, Player player){
        if (player == null) return;

        Realms storage = RealmAPI.getRealmFrom(player);
        Optional<RealmInstance> optional = storage.getRealm();
        if (optional.isEmpty()) return;

        Realm realm = RealmAPI.getRealmRegistry().get(packet.realm());
        if (realm == null) return;

        RealmInstance instance = optional.get();
        if (!instance.getNextBreakthroughs(player).contains(realm)) {
        }

        storage.breakthroughRealm(realm);
    }
}
