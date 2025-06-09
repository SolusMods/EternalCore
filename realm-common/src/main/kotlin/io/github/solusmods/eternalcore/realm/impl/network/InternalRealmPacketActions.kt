package io.github.solusmods.eternalcore.realm.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.realm.impl.network.c2s.RequestRealmBreakthroughPacket
import io.github.solusmods.eternalcore.realm.impl.network.c2s.SyncRealmStoragePayload
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object InternalRealmPacketActions {
    /**
     * This Method sends packet for the [Realm] Break.
     * Only executes on client using the dist executor.
     */
    fun sendRealmBreakthroughPacket(realm: ResourceLocation?) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        if (player == null) return
        NetworkManager.sendToServer(RequestRealmBreakthroughPacket(realm))
    }

    fun sendSyncStoragePayload(data: CompoundTag?) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        if (player == null) return
        NetworkManager.sendToServer(SyncRealmStoragePayload(data))
    }
}
