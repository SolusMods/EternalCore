package io.github.solusmods.eternalcore.abilities.fabric;

import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import net.fabricmc.api.ModInitializer;

public final class EternalCoreAbilitiesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EternalCoreAbilities.init();
    }
}
