package io.github.solusmods.eternalcore.element.impl

import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import io.github.solusmods.eternalcore.element.EternalCoreElements
import io.github.solusmods.eternalcore.element.ModuleConstants
import io.github.solusmods.eternalcore.element.api.Element
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

object ElementRegistry {
    private val registryId: ResourceLocation = EternalCoreElements.create("elements")

    val elements: Registrar<Element> = RegistrarManager.get(ModuleConstants.MOD_ID)
        .builder<Element>(registryId)
        .syncToClients()
        .build()

    val key: ResourceKey<Registry<Element>> = elements.key() as ResourceKey<Registry<Element>>

    fun init() {
        // Initialization logic if needed
    }
}
