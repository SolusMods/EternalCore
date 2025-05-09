package io.github.solusmods.eternalcore.element.impl.network.c2s;

import io.github.solusmods.eternalcore.element.impl.ElementsStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {

    static void handle(SyncElementsStoragePayload packet, ServerPlayer player) {
        player.eternalCore$getStorageOptional(ElementsStorage.getKey()).
                ifPresent(storage -> storage.load(packet.data()));
    }
}
