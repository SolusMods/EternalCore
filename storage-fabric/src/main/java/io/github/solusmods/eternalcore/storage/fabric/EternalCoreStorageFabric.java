package io.github.solusmods.eternalcore.storage.fabric;

import io.github.solusmods.eternalcore.storage.EternalCoreStorage;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;

public final class EternalCoreStorageFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreStorage.init();
        EntityTrackingEvents.START_TRACKING.register((entity, serverPlayer) -> StorageManager.syncTarget(entity, serverPlayer));
    }
}
