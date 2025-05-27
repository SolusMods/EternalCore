package io.github.solusmods.eternalcore.keybind;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import io.github.solusmods.eternalcore.keybind.api.KeybindingManager;

public class EternalCoreKeybindClient {
    public static void init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(instance -> KeybindingManager.init());
    }
}
