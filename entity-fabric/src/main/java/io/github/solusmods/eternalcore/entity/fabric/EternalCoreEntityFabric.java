package io.github.solusmods.eternalcore.entity.fabric;

import io.github.solusmods.eternalcore.entity.EternalCoreEntity;
import net.fabricmc.api.ModInitializer;

public class EternalCoreEntityFabric implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        EternalCoreEntity.init();
    }
}
