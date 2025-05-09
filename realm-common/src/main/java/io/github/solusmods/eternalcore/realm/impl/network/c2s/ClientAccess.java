package io.github.solusmods.eternalcore.realm.impl.network.c2s;

import io.github.solusmods.eternalcore.realm.impl.RealmStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {


    static void handle(SyncRealmStoragePayload packet, ServerPlayer player) {
        player.eternalCore$getStorageOptional(RealmStorage.getKey()).
                ifPresent(storage -> storage.load(packet.data()));
    }

}
