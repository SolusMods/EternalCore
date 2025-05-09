package io.github.solusmods.eternalcore.stage.impl.network.c2s;

import io.github.solusmods.eternalcore.stage.impl.StageStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {

    static void handle(SyncStagesStoragePayload packet, ServerPlayer player) {
        player.eternalCore$getStorageOptional(StageStorage.getKey()).
                ifPresent(storage -> storage.load(packet.data()));
    }
}
