package io.github.solusmods.eternalcore.element.fabric;

import io.github.solusmods.eternalcore.element.EternalCoreElements;
import net.fabricmc.api.ModInitializer;

public final class EternalCoreElementFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreElements.init();
    }
}
