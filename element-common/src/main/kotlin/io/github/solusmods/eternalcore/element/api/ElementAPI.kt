package io.github.solusmods.eternalcore.element.api

import dev.architectury.registry.registries.Registrar
import io.github.solusmods.eternalcore.element.impl.ElementRegistry
import io.github.solusmods.eternalcore.element.impl.ElementsStorage
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.LivingEntity
import java.util.Optional

/**
 * API object for working with Elements in the EternalCore mod.
 * Provides access to element registry and utilities for working with living entities.
 */
@Suppress("UNCHECKED_CAST")
object ElementAPI {

    /**
     * Returns the Element Registry.
     * Can be used to load Elements from the Registry.
     */
    @JvmField
    val elementRegistry: Registrar<Element> = ElementRegistry.elements

    /**
     * Returns the Registry Key of the ElementRegistry.
     * Can be used to create DeferredRegister instances.
     */
    @JvmField
    val elementRegistryKey: ResourceKey<Registry<Element>> = ElementRegistry.key

    /**
     * Loads the dominant element from a LivingEntity.
     *
     * @param entity The living entity to get the dominant element from
     * @return The dominant Elements instance, or null if not found
     */
    @JvmStatic
    fun getDominantElementFrom(entity: LivingEntity): Elements? {
        return entity.`eternalCore$getStorage`(ElementsStorage.key)
    }

    /**
     * Loads all elements from a LivingEntity.
     *
     * @param entity The living entity to get elements from
     * @return The Elements instance, or null if not found
     */
    @JvmStatic
    fun getElementsFrom(entity: LivingEntity): Elements? {
        return entity.`eternalCore$getStorage`(ElementsStorage.key)
    }

    @JvmStatic
    fun getStorageOptional(entity: LivingEntity): Optional<ElementsStorage> {
        return entity.`eternalCore$getStorageOptional`(ElementsStorage.key!!)
    }
}