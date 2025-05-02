package io.github.solusmods.eternalcore.spiritual_root.fabric;

import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot;
import net.fabricmc.api.ModInitializer;

public final class EternalCoreSpiritualRootFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreSpiritualRoot.init();
    }
}
