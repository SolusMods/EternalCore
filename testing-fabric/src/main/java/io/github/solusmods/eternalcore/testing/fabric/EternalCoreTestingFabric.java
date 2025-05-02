package io.github.solusmods.eternalcore.testing.fabric;

import io.github.solusmods.eternalcore.testing.EternalCoreTesting;
import net.fabricmc.api.ModInitializer;

public final class EternalCoreTestingFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreTesting.init();
    }
}
