package io.github.solusmods.eternalcore.stage.impl.network.c2s

import io.github.solusmods.eternalcore.stage.api.StageAPI
import io.github.solusmods.eternalcore.stage.impl.StageStorage
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.function.Consumer

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object ClientAccess {
    fun handle(packet: SyncStagesStoragePayload, player: ServerPlayer?) {
        player!!.getStorageOptional<StageStorage?>(StageStorage.key).ifPresent(
            Consumer { storage: StageStorage? -> storage!!.load(packet.data!!) })
    }

    fun handle(packet: RequestStageBreakthroughPacket, player: Player?) {
        if (player == null) return

        val storage = StageAPI.getStageFrom(player)
        val optional = storage!!.stage!!
        if (optional.isEmpty) return

        val stage = StageAPI.stageRegistry!!.get(packet.stage)
        if (stage == null) return

        val instance = optional.get()
        if (!instance.getNextBreakthroughs(player)!!.contains(stage)) {
        }

        storage.breakthroughStage(stage)
    }
}
