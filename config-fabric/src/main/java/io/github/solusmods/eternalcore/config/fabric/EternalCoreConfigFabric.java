package io.github.solusmods.eternalcore.config.fabric;

import io.github.solusmods.eternalcore.config.EternalCoreConfig;
import net.fabricmc.api.ModInitializer;

public class EternalCoreConfigFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreConfig.init();
    }
}
