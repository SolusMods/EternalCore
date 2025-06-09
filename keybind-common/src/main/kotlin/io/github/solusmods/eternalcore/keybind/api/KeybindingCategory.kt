package io.github.solusmods.eternalcore.keybind.api

import io.github.solusmods.eternalcore.keybind.ModuleConstants

class KeybindingCategory private constructor(private val name: String) {

    val categoryString: String
        get() = String.format(
            "%s.category.%s",
            ModuleConstants.MOD_ID,
            this.name
        )

    companion object {
        @JvmStatic
        fun of(name: String): KeybindingCategory {
            return KeybindingCategory(name)
        }
    }
}
