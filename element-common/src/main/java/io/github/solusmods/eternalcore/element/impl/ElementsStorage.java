package io.github.solusmods.eternalcore.element.impl;

import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.element.EternalCoreElements;
import io.github.solusmods.eternalcore.element.api.ElementEvents;
import io.github.solusmods.eternalcore.element.api.ElementInstance;
import io.github.solusmods.eternalcore.element.api.Elements;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.storage.api.*;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ElementsStorage extends Storage implements Elements {
    private static final String ELEMENTS_KEY = "elements_key";
    private static final String DOMINANT_ELEMENT_KEY = "dominant_element_key";
    public static final ResourceLocation ID = EternalCoreElements.create("elements_storage");
    @Getter
    private static StorageKey<ElementsStorage> key = null;
    private Collection<ElementInstance> elements = new ArrayList<>();
    private ElementInstance element;
    protected ElementsStorage(StorageHolder holder) {
        super(holder);
    }

    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        ElementsStorage.class, Player.class::isInstance,
                        ElementsStorage::new));
    }
    

    @Override
    public void save(CompoundTag data) {
        if (element != null)
            data.put(DOMINANT_ELEMENT_KEY, this.element.toNBT());
        saveInstanceCollection(data, ELEMENTS_KEY, elements, ElementInstance::toNBT, ElementInstance::getElementId);
    }

    @Override
    public void load(CompoundTag data) {
        loadSingleInstances(data);
        loadCollections(data);
    }

    private void loadSingleInstances(CompoundTag data) {
        if (data.contains(DOMINANT_ELEMENT_KEY))
            element = ElementInstance.fromNBT(data.getCompound(DOMINANT_ELEMENT_KEY));
    }

    private void loadCollections(CompoundTag data) {
        loadInstanceCollection(data, ELEMENTS_KEY, elements, ElementInstance::fromNBT);
    }

    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }

    @Override
    public Collection<ElementInstance> getElements() {
        return elements;
    }

    @Override
    public Optional<ElementInstance> getElement() {
        return Optional.ofNullable(element);
    }

    @Override
    public boolean addElement(ElementInstance elementInstance, boolean breakthrough, boolean notifyPlayer, @Nullable MutableComponent component) {
        Changeable<MutableComponent> realmMessage = Changeable.of(component);
        Changeable<Boolean> notify = Changeable.of(notifyPlayer);
        EventResult result = ElementEvents.ADD_ELEMENT.invoker().add(elementInstance, getOwner(), breakthrough, notify, realmMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();
        if (realmMessage.isPresent()) getOwner().sendSystemMessage(realmMessage.get());
        elementInstance.markDirty();
//        elementInstance.onReach(owner);
        elements.add(elementInstance);
        markDirty();
        return true;
    }

    @Override
    public boolean setElement(ElementInstance elementInstance, boolean breakthrough, boolean notifyPlayer, @Nullable MutableComponent component) {
        ElementInstance instance = this.element;
        Changeable<MutableComponent> realmMessage = Changeable.of(component);
        Changeable<Boolean> notify = Changeable.of(notifyPlayer);
        EventResult result = ElementEvents.SET_ELEMENT.invoker().set(instance, getOwner(), elementInstance, breakthrough, notify, realmMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();


        if (realmMessage.isPresent()) getOwner().sendSystemMessage(realmMessage.get());
        elementInstance.markDirty();
//        elementInstance.onSet(owner);
        this.element = elementInstance;
        markDirty();
        return true;
    }

//    @Override
//    public void markDirty() {
//        super.markDirty();
//        sync();
//    }
//
//    public void sync() {
//        CompoundTag tag = new CompoundTag();
//        this.save(tag);
//        InternalPlayerStorageActions.sendPlayerUpdatePacket(tag);
//    }
}
