package io.github.solusmods.eternalcore.stage.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.stage.impl.network.c2s.RequestStageBreakthroughPacket
import io.github.solusmods.eternalcore.stage.impl.network.c2s.SyncStagesStoragePayload
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object InternalStagePacketActions {
    /**
     * This Method sends packet for the [Stage] Break.
     * Only executes on client using the dist executor.
     */
    fun sendStageBreakthroughPacket(stage: ResourceLocation?) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        if (player == null) return
        NetworkManager.sendToServer(RequestStageBreakthroughPacket(stage))
    }


    fun sendSyncStoragePayload(data: CompoundTag?) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        if (player == null) return
        NetworkManager.sendToServer(SyncStagesStoragePayload(data))
    }
}
