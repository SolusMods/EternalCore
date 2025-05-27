package io.github.solusmods.eternalcore.testing.client;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import io.github.solusmods.eternalcore.testing.configs.TestConfig;
import net.minecraft.client.Minecraft;

public class EternalCoreTestingClient {
    public static void init() {
        KeybindingTest.init();
        ClientChatEvent.RECEIVED.register((type, message) -> {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                TestConfig.printTestConfig(player);
            }
            return CompoundEventResult.pass();
        });
    }
}
