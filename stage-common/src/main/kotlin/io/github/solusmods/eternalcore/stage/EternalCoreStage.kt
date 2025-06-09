package io.github.solusmods.eternalcore.stage

import io.github.solusmods.eternalcore.stage.impl.StageRegistry
import io.github.solusmods.eternalcore.stage.impl.StageStorage
import io.github.solusmods.eternalcore.stage.impl.network.StagesNetwork
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object EternalCoreStage {
    val LOG: Logger? = LoggerFactory.getLogger("EternalCore - Stage")

    fun create(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, path)
    }

    @JvmStatic
    fun init() {
        StageStorage.Companion.init()
        StagesNetwork.init()
        StageRegistry.init()
    }
}
