package io.github.solusmods.eternalcore.keybind;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;

public class EternalCoreKeybind {
    public static void init() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            EternalCoreKeybindClient.init();
        }
    }
}
