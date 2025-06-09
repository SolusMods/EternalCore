package io.github.solusmods.eternalcore.keybind.api

import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.minecraft.client.Minecraft

object KeybindingManager {
    private val keybindings = ArrayList<EternalKeybinding>()

    @JvmStatic
    fun <T : EternalKeybinding?> register(vararg keybindings: T?) {
        for (keybinding in keybindings) {
            KeybindingManager.register<EternalKeybinding?>(keybinding)
        }
    }

    fun <T : EternalKeybinding?> register(keybinding: T?): T? {
        keybindings.add(keybinding!!)
        KeyMappingRegistry.register(keybinding)
        return keybinding
    }

    fun init() {
        // Execute Actions on press
        ClientTickEvent.CLIENT_POST.register(ClientTickEvent.Client { instance: Minecraft ->
            for (keybinding in keybindings) {
                if (keybinding.isDown) {
                    keybinding.action!!.onPress()
                } else keybinding.release?.run()
            }
        })
    }
}
