package io.github.solusmods.eternalcore.impl.spiritual_root.network.c2s;

import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.spiritual_root.SpiritualRootAPI;
import io.github.solusmods.eternalcore.api.spiritual_root.SpiritualRoots;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {


    static void handle(RequestSpiritualRootAdvancePacket packet, Player player) {
        if (player == null) return;

        SpiritualRoots storage = SpiritualRootAPI.getSpiritualRootFrom(player);
        AbstractSpiritualRoot spiritualRoot = SpiritualRootAPI.getSpiritualRootRegistry().get(packet.spiritual_root());
        if (spiritualRoot == null) return;

        storage.advanceSpiritualRoot(spiritualRoot);
    }
}
