package io.github.solusmods.eternalcore.realm.impl.network;

import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.network.api.util.StorageType;
import io.github.solusmods.eternalcore.realm.api.Realm;
import io.github.solusmods.eternalcore.realm.impl.network.c2s.RequestRealmBreakthroughPacket;
import io.github.solusmods.eternalcore.realm.impl.network.c2s.SyncRealmStoragePayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InternalRealmPacketActions {


    /**
     * This Method sends packet for the {@link Realm} Break.
     * Only executes on client using the dist executor.
     */
    public static void sendRealmBreakthroughPacket(ResourceLocation realm) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new RequestRealmBreakthroughPacket(realm));
    }

    public static void sendSyncStoragePayload(CompoundTag data) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new SyncRealmStoragePayload(data));
    }
}
