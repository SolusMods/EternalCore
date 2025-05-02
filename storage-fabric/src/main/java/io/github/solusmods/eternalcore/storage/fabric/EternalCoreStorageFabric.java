package io.github.solusmods.eternalcore.storage.fabric;

import io.github.solusmods.eternalcore.storage.EternalCoreStorage;
import net.fabricmc.api.ModInitializer;

public final class EternalCoreStorageFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreStorage.init();
    }
}
