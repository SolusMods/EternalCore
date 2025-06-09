package io.github.solusmods.eternalcore.element.api

import dev.architectury.registry.registries.Registrar
import io.github.solusmods.eternalcore.element.impl.ElementRegistry
import io.github.solusmods.eternalcore.element.impl.ElementsStorage
import io.github.solusmods.eternalcore.storage.api.Storage
import io.github.solusmods.eternalcore.storage.api.StorageKey
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.LivingEntity

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
    val elementRegistry: Registrar<Element>
        get() = ElementRegistry.elements

    /**
     * Returns the Registry Key of the ElementRegistry.
     * Can be used to create DeferredRegister instances.
     */
    val elementRegistryKey: ResourceKey<Registry<Element>>
        get() = ElementRegistry.key

    /**
     * Loads the dominant element from a LivingEntity.
     *
     * @param entity The living entity to get the dominant element from
     * @return The dominant Elements instance, or null if not found
     */
    fun getDominantElementFrom(entity: LivingEntity): Elements? {
        return entity.getStorage<ElementsStorage>(ElementsStorage.key)
    }

    /**
     * Loads all elements from a LivingEntity.
     *
     * @param entity The living entity to get elements from
     * @return The Elements instance, or null if not found
     */
    fun getElementsFrom(entity: LivingEntity): Elements? {
        return entity.getStorage<ElementsStorage>(ElementsStorage.key)
    }
}

/**
 * Extension function to safely get storage from a LivingEntity.
 * Provides a cleaner API for accessing entity storage.
 */
@Suppress("UNCHECKED_CAST")
private inline fun <reified T> LivingEntity.getStorage(key: StorageKey<ElementsStorage>?): T? {
    return this.getStorage(key as StorageKey<Storage?>?) as T?
}