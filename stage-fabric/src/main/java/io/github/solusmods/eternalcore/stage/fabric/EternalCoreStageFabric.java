package io.github.solusmods.eternalcore.stage.fabric;

import io.github.solusmods.eternalcore.stage.EternalCoreStage;
import net.fabricmc.api.ModInitializer;

public final class EternalCoreStageFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreStage.init();
    }
}
