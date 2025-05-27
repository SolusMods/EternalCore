package io.github.solusmods.eternalcore.keybind.api;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;

import java.util.ArrayList;

public class KeybindingManager {
    private static final ArrayList<EternalKeybinding> keybindings = new ArrayList<>();

    public static <T extends EternalKeybinding> void register(T... keybindings) {
        for (final EternalKeybinding keybinding : keybindings) {
            register(keybinding);
        }
    }

    public static <T extends EternalKeybinding> T register(T keybinding) {
        keybindings.add(keybinding);
        KeyMappingRegistry.register(keybinding);
        return keybinding;
    }

    public static void init() {
        // Execute Actions on press
        ClientTickEvent.CLIENT_POST.register(instance -> {
            for (final EternalKeybinding keybinding : keybindings) {
                if (keybinding.isDown()) {
                    keybinding.getAction().onPress();
                } else if (keybinding.getRelease() != null) {
                    keybinding.getRelease().run();
                }
            }
        });
    }
}
