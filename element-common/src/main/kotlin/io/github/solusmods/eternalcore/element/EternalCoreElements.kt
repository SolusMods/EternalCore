package io.github.solusmods.eternalcore.element

import io.github.solusmods.eternalcore.element.impl.ElementRegistry
import io.github.solusmods.eternalcore.element.impl.ElementsStorage
import io.github.solusmods.eternalcore.element.impl.network.ElementsNetwork
import net.minecraft.resources.ResourceLocation

object EternalCoreElements {
    fun create(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, path)
    }

    @JvmStatic
    fun init() {
        ElementRegistry.init()
        ElementsStorage.init()
        ElementsNetwork.init()
    }
}
