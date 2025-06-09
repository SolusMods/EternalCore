package io.github.solusmods.eternalcore.keybind

import dev.architectury.platform.Platform
import dev.architectury.utils.Env

object EternalCoreKeybind {
    @JvmStatic
    fun init() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            EternalCoreKeybindClient.init()
        }
    }
}
