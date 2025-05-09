package io.github.solusmods.eternalcore.element.impl.network;

import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.element.impl.network.c2s.SyncElementsStoragePayload;
import io.github.solusmods.eternalcore.network.api.util.StorageType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalStorageActions {

    public static void sendSyncStoragePayload(StorageType type, CompoundTag data) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new SyncElementsStoragePayload(data));
    }
}
