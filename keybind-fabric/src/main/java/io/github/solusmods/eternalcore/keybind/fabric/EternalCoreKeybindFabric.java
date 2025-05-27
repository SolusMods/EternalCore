package io.github.solusmods.eternalcore.keybind.fabric;

import io.github.solusmods.eternalcore.keybind.EternalCoreKeybind;
import net.fabricmc.api.ModInitializer;

public class EternalCoreKeybindFabric implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        EternalCoreKeybind.init();
    }
}
