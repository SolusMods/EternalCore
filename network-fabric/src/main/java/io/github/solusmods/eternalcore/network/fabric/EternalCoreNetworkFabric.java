package io.github.solusmods.eternalcore.network.fabric;

import io.github.solusmods.eternalcore.network.EternalCoreNetwork;
import net.fabricmc.api.ModInitializer;

public final class EternalCoreNetworkFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreNetwork.init();
    }
}
