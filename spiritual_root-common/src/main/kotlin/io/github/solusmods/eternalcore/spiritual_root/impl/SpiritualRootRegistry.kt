package io.github.solusmods.eternalcore.spiritual_root.impl

import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot
import io.github.solusmods.eternalcore.spiritual_root.ModuleConstants
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRoot
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

object SpiritualRootRegistry {
    private val registryId: ResourceLocation = EternalCoreSpiritualRoot.create("spiritual_roots")
    @JvmField
    val SPIRITUAL_ROOTS: Registrar<SpiritualRoot> =
        RegistrarManager.get(ModuleConstants.MOD_ID).builder<SpiritualRoot>(registryId)
            .syncToClients().build()
    @JvmField
    val KEY: ResourceKey<Registry<SpiritualRoot>> = SPIRITUAL_ROOTS.key() as ResourceKey<Registry<SpiritualRoot>>


    fun init() {
    }
}
