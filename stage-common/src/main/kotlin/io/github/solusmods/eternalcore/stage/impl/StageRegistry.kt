package io.github.solusmods.eternalcore.stage.impl

import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import io.github.solusmods.eternalcore.stage.EternalCoreStage
import io.github.solusmods.eternalcore.stage.ModuleConstants
import io.github.solusmods.eternalcore.stage.api.Stage
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object StageRegistry {
    private val registryId: ResourceLocation = EternalCoreStage.create("stages")

    // endregion
    val STAGES: Registrar<Stage?> = RegistrarManager.get(ModuleConstants.MOD_ID).builder<Stage?>(registryId)
        .syncToClients().build()
    val KEY: ResourceKey<Registry<Stage?>?>? = STAGES.key() as ResourceKey<Registry<Stage?>?>?


    fun init() {}
}
