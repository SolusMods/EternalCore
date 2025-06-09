package io.github.solusmods.eternalcore.keybind

import dev.architectury.event.events.client.ClientLifecycleEvent
import io.github.solusmods.eternalcore.keybind.api.KeybindingManager
import net.minecraft.client.Minecraft

object EternalCoreKeybindClient {
    fun init() {
        ClientLifecycleEvent.CLIENT_SETUP.register(ClientLifecycleEvent.ClientState { instance: Minecraft -> KeybindingManager.init() })
    }
}
