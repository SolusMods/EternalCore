package io.github.solusmods.eternalcore.element.impl.network.c2s

import io.github.solusmods.eternalcore.element.impl.ElementsStorage
import io.github.solusmods.eternalcore.storage.api.StorageKey
import net.minecraft.server.level.ServerPlayer

object ClientAccess {
    fun handle(packet: SyncElementsStoragePayload, player: ServerPlayer?) {
        player!!.getStorageOptional(ElementsStorage.key as StorageKey<ElementsStorage?>?).ifPresent {
            storage: ElementsStorage? -> storage!!.load(packet.data!!)
        }
    }
}
