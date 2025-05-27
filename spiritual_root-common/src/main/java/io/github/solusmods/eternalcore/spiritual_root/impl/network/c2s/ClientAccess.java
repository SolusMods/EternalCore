package io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s;

import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRoot;
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootAPI;
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootInstance;
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRoots;
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {


    static void handle(SyncSpiritualRootStoragePayload packet, ServerPlayer player) {
        player.eternalCore$getStorageOptional(SpiritualRootStorage.getKey()).
                ifPresent(storage -> storage.load(packet.data()));
    }
    
    static void handle(RequestSpiritualRootAdvancePacket packet, Player player){
        if (player == null) return;

        SpiritualRoots storage = SpiritualRootAPI.getSpiritualRootFrom(player);
        Optional<SpiritualRootInstance> optional = Optional.ofNullable(storage.getSpiritualRoots().get(packet.spiritual_root()).getPreviousDegree(player).createDefaultInstance());
        if (optional.isEmpty()) return;

        SpiritualRoot spiritualRoot = SpiritualRootAPI.getSpiritualRootRegistry().get(packet.spiritual_root());
        if (spiritualRoot == null) return;

        SpiritualRootInstance instance = optional.get();
        if (!instance.getFirstDegree(player).equals(spiritualRoot)) {
        }

        storage.advanceSpiritualRoot(spiritualRoot);
    }
}
