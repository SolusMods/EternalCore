package io.github.solusmods.eternalcore.impl.realm.network;

import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.impl.realm.network.c2s.RequestRealmBreakthroughPacket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InternalRealmPacketActions {


    /**
     * This Method sends packet for the {@link io.github.solusmods.eternalcore.api.realm.AbstractRealm} Break.
     * Only executes on client using the dist executor.
     */
    public static void sendRealmBreakthroughPacket(ResourceLocation realm) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new RequestRealmBreakthroughPacket(realm));
    }
}
