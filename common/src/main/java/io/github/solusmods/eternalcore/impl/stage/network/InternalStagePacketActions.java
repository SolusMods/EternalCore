package io.github.solusmods.eternalcore.impl.stage.network;

import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import io.github.solusmods.eternalcore.impl.stage.network.c2s.RequestStageBreakthroughPacket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalStagePacketActions {

    /**
     * This Method sends packet for the {@link AbstractStage} Break.
     * Only executes on client using the dist executor.
     */
    public static void sendStageBreakthroughPacket(ResourceLocation stage) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new RequestStageBreakthroughPacket(stage));
    }
}
