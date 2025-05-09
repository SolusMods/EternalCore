package io.github.solusmods.eternalcore.element.api;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface Elements {

    Optional<ElementInstance> getElement();
    Collection<ElementInstance> getElements();

    default boolean setElement(@NotNull ResourceLocation elementId, boolean notify) {
        return setElement(elementId, notify, null);
    }

    default boolean setElement(@NotNull ResourceLocation elementId, boolean notify, @Nullable MutableComponent component) {
        Element element = ElementAPI.getElementRegistry().get(elementId);
        if (element == null) return false;
        return setElement(element.createDefaultInstance(), false, notify, component);
    }

    default boolean setElement(@NonNull Element element, boolean notify) {
        return setElement(element, notify, null);
    }

    default boolean setElement(@NonNull Element element, boolean notify, @Nullable MutableComponent component) {
        return setElement(element.createDefaultInstance(), false, notify, component);
    }

    default boolean setElement(ElementInstance instance, boolean breakthrough, boolean notify) {
        return setElement(instance, breakthrough, notify, null);
    }

    boolean setElement(ElementInstance instance, boolean breakthrough, boolean notify, @Nullable MutableComponent component);

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
