package io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s;

import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {


    static void handle(SyncSpiritualRootStoragePayload packet, ServerPlayer player) {
        player.eternalCore$getStorageOptional(SpiritualRootStorage.getKey()).
                ifPresent(storage -> storage.load(packet.data()));
    }
}
