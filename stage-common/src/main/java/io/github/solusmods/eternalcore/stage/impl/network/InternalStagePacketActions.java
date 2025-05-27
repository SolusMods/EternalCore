package io.github.solusmods.eternalcore.stage.impl.network;

import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.stage.api.Stage;
import io.github.solusmods.eternalcore.stage.impl.network.c2s.RequestStageBreakthroughPacket;
import io.github.solusmods.eternalcore.stage.impl.network.c2s.SyncStagesStoragePayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalStagePacketActions {

    /**
     * This Method sends packet for the {@link Stage} Break.
     * Only executes on client using the dist executor.
     */
    public static void sendStageBreakthroughPacket(ResourceLocation stage) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new RequestStageBreakthroughPacket(stage));
    }


    public static void sendSyncStoragePayload(CompoundTag data) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new SyncStagesStoragePayload(data));
    }
}
