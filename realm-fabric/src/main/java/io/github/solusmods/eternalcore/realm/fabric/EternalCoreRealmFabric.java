package io.github.solusmods.eternalcore.realm.fabric;

import io.github.solusmods.eternalcore.realm.EternalCoreRealm;
import net.fabricmc.api.ModInitializer;

public final class EternalCoreRealmFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreRealm.init();
    }
}
