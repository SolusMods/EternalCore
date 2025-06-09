package io.github.solusmods.eternalcore.spiritual_root

import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootRegistry
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootStorage
import io.github.solusmods.eternalcore.spiritual_root.impl.network.SpiritualRootNetwork
import net.minecraft.resources.ResourceLocation


object EternalCoreSpiritualRoot {
    fun create(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, path)
    }

    @JvmStatic
    fun init() {
        SpiritualRootNetwork.init()
        SpiritualRootRegistry.init()
        SpiritualRootStorage.Companion.init()
    }
}
