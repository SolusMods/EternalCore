package io.github.solusmods.eternalcore.spiritual_root.impl.network;

import dev.architectury.networking.NetworkManager;

import io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s.RequestSpiritualRootAdvancePacket;
import io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s.SyncSpiritualRootStoragePayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InternalSpiritualRootPacketActions {

    /**
     * This Method sends packet for the realm Break.
     * Only executes on client using the dist executor.
     */
    public static void sendSpiritualRootMasteringPacket(ResourceLocation realm) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new RequestSpiritualRootAdvancePacket(realm));
    }

    public static void sendSyncStoragePayload(CompoundTag data) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new SyncSpiritualRootStoragePayload(data));
    }
}
