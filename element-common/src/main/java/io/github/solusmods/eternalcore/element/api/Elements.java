package io.github.solusmods.eternalcore.element.api;

import io.github.solusmods.eternalcore.element.impl.ElementsStorage;
import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public interface Elements {

    Map<ResourceLocation, ElementInstance> getElements();

    Collection<ElementInstance> getObtainedElements();

    /**
     * Updates a element instance and optionally synchronizes the change across the network.
     * <p>
     * @param updatedInstance The instance to update
     * @param sync If true, synchronizes the change to all clients/server
     */
    void updateElement(ElementInstance updatedInstance, boolean sync);

    void forEachElement(BiConsumer<ElementsStorage, ElementInstance> skillInstanceConsumer);

    void forgetElement(@NotNull ResourceLocation skillId, @Nullable MutableComponent component);

    default void forgetElement(@NotNull ResourceLocation skillId) {
        forgetElement(skillId, null);
    }

    default void forgetElement(@NonNull Element element, @Nullable MutableComponent component) {
        forgetElement(element.getRegistryName(), component);
    }

    default void forgetElement(@NonNull Element element) {
        forgetElement(element.getRegistryName());
    }

    default void forgetElement(@NonNull ElementInstance instance, @Nullable MutableComponent component) {
        forgetElement(instance.getElementId(), component);
    }

    default void forgetElement(@NonNull ElementInstance instance) {
        forgetElement(instance.getElementId());
    }


    default boolean addElement(@NotNull ResourceLocation elementId, boolean teleportToSpawn) {
        return addElement(elementId, teleportToSpawn, null);
    }

    default boolean addElement(@NotNull ResourceLocation elementId, boolean teleportToSpawn, @Nullable MutableComponent component) {
        Element element = ElementAPI.getElementRegistry().get(elementId);
        if (element == null) return false;
        return addElement(element.createDefaultInstance(), false, teleportToSpawn, component);
    }

    default boolean addElement(@NonNull Element element, boolean teleportToSpawn) {
        return addElement(element, teleportToSpawn, null);
    }

    default boolean addElement(@NonNull Element element, boolean teleportToSpawn, @Nullable MutableComponent component) {
        return addElement(element.createDefaultInstance(), false, teleportToSpawn, component);
    }

    default boolean addElement(ElementInstance instance, boolean breakthrough, boolean teleportToSpawn) {
        return addElement(instance, breakthrough, teleportToSpawn, null);
    }

    boolean addElement(ElementInstance instance, boolean breakthrough, boolean teleportToSpawn, @Nullable MutableComponent component);
    
    void markDirty();

    void sync();
}
