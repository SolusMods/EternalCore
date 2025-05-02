package io.github.solusmods.eternalcore.storage;

import dev.architectury.event.events.common.LifecycleEvent;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import io.github.solusmods.eternalcore.storage.impl.network.EternalStorageNetwork;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalCoreStorage {
    public static final Logger LOG = LoggerFactory.getLogger("EternalCore - Storage");

    public static void init() {
        EternalStorageNetwork.init();
        LifecycleEvent.SETUP.register(StorageManager::init);
    }
}
