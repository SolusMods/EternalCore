package io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s

import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootAPI
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootInstance
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootStorage
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.*
import java.util.function.Consumer

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object ClientAccess {
    fun handle(packet: SyncSpiritualRootStoragePayload, player: ServerPlayer?) {
        player!!.getStorageOptional<SpiritualRootStorage?>(SpiritualRootStorage.key).ifPresent(
            Consumer { storage: SpiritualRootStorage? -> storage!!.load(packet.data!!) })
    }

    fun handle(packet: RequestSpiritualRootAdvancePacket, player: Player?) {
        if (player == null) return

        val storage = SpiritualRootAPI.getSpiritualRootFrom(player)
        val optional = Optional.ofNullable<SpiritualRootInstance?>(
            storage!!.spiritualRoots.get(packet.spiritual_root)!!.getPreviousDegree(player)!!.createDefaultInstance()
        )
        if (optional.isEmpty()) return

        val spiritualRoot = SpiritualRootAPI.spiritualRootRegistry!!.get(packet.spiritual_root)
        if (spiritualRoot == null) return

        val instance = optional.get()
        if (instance.getFirstDegree(player) != spiritualRoot) {
        }

        storage.advanceSpiritualRoot(spiritualRoot)
    }
}
