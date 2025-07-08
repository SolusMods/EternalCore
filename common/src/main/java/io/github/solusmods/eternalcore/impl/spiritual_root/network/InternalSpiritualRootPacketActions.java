package io.github.solusmods.eternalcore.impl.spiritual_root.network;

import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.impl.spiritual_root.network.c2s.RequestSpiritualRootAdvancePacket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InternalSpiritualRootPacketActions {

    /**
     * This Method sends packet for the spiritualRoot Advance.
     * Only executes on client using the dist executor.
     */
    public static void sendSpiritualRootAdvancePacket(ResourceLocation spiritualRoot) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new RequestSpiritualRootAdvancePacket(spiritualRoot));
    }
}
