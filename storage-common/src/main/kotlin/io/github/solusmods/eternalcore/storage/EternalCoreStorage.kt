package io.github.solusmods.eternalcore.storage

import dev.architectury.event.events.common.LifecycleEvent
import io.github.solusmods.eternalcore.storage.impl.StorageManager
import io.github.solusmods.eternalcore.storage.impl.network.EternalStorageNetwork
import lombok.AccessLevel
import lombok.NoArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object EternalCoreStorage {
    val LOG: Logger = LoggerFactory.getLogger("EternalCore - Storage")

    @JvmStatic
    fun init() {
        EternalStorageNetwork.init()
        LifecycleEvent.SETUP.register(StorageManager::init)
    }
}