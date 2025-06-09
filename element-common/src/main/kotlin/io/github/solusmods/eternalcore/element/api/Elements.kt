package io.github.solusmods.eternalcore.element.api

import io.github.solusmods.eternalcore.element.impl.ElementsStorage
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

interface Elements {
    val elements: MutableMap<ResourceLocation, ElementInstance>
    val obtainedElements: MutableCollection<ElementInstance>

    /**
     * Updates an element instance and optionally synchronizes the change across the network.
     *
     * @param updatedInstance The instance to update
     * @param sync If true, synchronizes the change to all clients/server
     */
    fun updateElement(updatedInstance: ElementInstance, sync: Boolean)

    fun forEachElement(action: (ElementsStorage, ElementInstance) -> Unit)

    fun forgetElement(elementId: ResourceLocation, component: MutableComponent? = null)

    fun forgetElement(element: Element, component: MutableComponent? = null) {
        forgetElement(element.registryName!!, component)
    }

    fun forgetElement(instance: ElementInstance, component: MutableComponent? = null) {
        forgetElement(instance.elementId, component)
    }

    fun addElement(
        elementId: ResourceLocation,
        teleportToSpawn: Boolean,
        component: MutableComponent? = null
    ): Boolean {
        val element = ElementAPI.elementRegistry.get(elementId) ?: return false
        return addElement(element.createDefaultInstance(), breakthrough = false, teleportToSpawn, component)
    }

    fun addElement(
        element: Element,
        teleportToSpawn: Boolean,
        component: MutableComponent? = null
    ): Boolean = addElement(element.createDefaultInstance(), breakthrough = false, teleportToSpawn, component)

    fun addElement(
        instance: ElementInstance,
        breakthrough: Boolean,
        teleportToSpawn: Boolean
    ): Boolean = addElement(instance, breakthrough, teleportToSpawn, component = null)

    fun addElement(
        instance: ElementInstance,
        breakthrough: Boolean,
        teleportToSpawn: Boolean,
        component: MutableComponent? = null
    ): Boolean

    fun markDirty()
    fun sync()
}