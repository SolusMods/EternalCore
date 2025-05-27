package io.github.solusmods.eternalcore.attributes.fabric;

import io.github.solusmods.eternalcore.attributes.EternalCoreAttribute;
import net.fabricmc.api.ModInitializer;

public class EternalCoreAttributeFabric implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        EternalCoreAttribute.init();
    }
}
