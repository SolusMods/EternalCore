package io.github.solusmods.eternalcore.spiritual_root.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s.RequestSpiritualRootAdvancePacket
import io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s.SyncSpiritualRootStoragePayload
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object InternalSpiritualRootPacketActions {
    /**
     * This Method sends packet for the spiritualRoot Advance.
     * Only executes on client using the dist executor.
     */
    fun sendSpiritualRootAdvancePacket(spiritualRoot: ResourceLocation?) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        if (player == null) return
        NetworkManager.sendToServer(RequestSpiritualRootAdvancePacket(spiritualRoot))
    }

    fun sendSyncStoragePayload(data: CompoundTag?) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        if (player == null) return
        NetworkManager.sendToServer(SyncSpiritualRootStoragePayload(data))
    }
}
