package io.github.solusmods.eternalcore.element.impl;

import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.element.EternalCoreElements;
import io.github.solusmods.eternalcore.element.api.ElementEvents;
import io.github.solusmods.eternalcore.element.api.ElementInstance;
import io.github.solusmods.eternalcore.element.api.Elements;
import io.github.solusmods.eternalcore.element.impl.network.InternalStorageActions;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.network.api.util.StorageType;
import io.github.solusmods.eternalcore.storage.EternalCoreStorage;
import io.github.solusmods.eternalcore.storage.api.*;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Сховище для управління елементами гравця в системі EternalCore.
 * <p>
 * Цей клас відповідає за зберігання, управління та синхронізацію елементів,
 * які гравець здобуває протягом гри. Елементи є важливою частиною системи культивації
 * та визначають спеціальні здібності та атрибути гравця.
 * </p>
 * <p>
 * ElementsStorage реалізує інтерфейс {@link Elements} та розширює базовий клас {@link Storage},
 * успадковуючи функціональність збереження та завантаження даних у форматі NBT.
 * </p>
 */
public class ElementsStorage extends Storage implements Elements {
    /** Ключ для зберігання колекції елементів у NBT */
    private static final String ELEMENTS_KEY = "elements_key";
    
    /** Унікальний ідентифікатор цього типу сховища */
    public static final ResourceLocation ID = EternalCoreElements.create("elements_storage");
    
    /** Ключ для доступу до цього сховища */
    @Getter
    private static StorageKey<ElementsStorage> key = null;
    
    /** Колекція елементів, якими володіє гравець */
    @Getter
    private final Map<ResourceLocation, ElementInstance> elements = new HashMap<>();
    
    /**
     * Створює нове сховище елементів для вказаного власника.
     *
     * @param holder Власник цього сховища
     */
    protected ElementsStorage(StorageHolder holder) {
        super(holder);
    }

    public Collection<ElementInstance> getObtainedElements() {
        return this.elements.values();
    }

    private boolean hasRemovedElements = false;

    /**
     * Ініціалізує систему сховища елементів, реєструючи його в системі сховищ EternalCore.
     * <p>
     * Цей метод повинен бути викликаний один раз під час ініціалізації мода.
     * </p>
     */
    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        ElementsStorage.class, Entity.class::isInstance,
                        ElementsStorage::new));
    }
    
    /**
     * Зберігає стан сховища в NBT формат.
     * <p>
     * Зберігає домінуючий елемент та колекцію всіх елементів гравця.
     * </p>
     *
     * @param data Тег, в який буде збережено дані
     */
    @Override
    public void save(CompoundTag data) {
        ListTag elementsTag = new ListTag();
        elements.values().forEach(instance -> {
            elementsTag.add(instance.toNBT());
            instance.resetDirty();
        });
        data.put(ELEMENTS_KEY, elementsTag);
    }

    /**
     * Завантажує стан сховища з NBT формату.
     * <p>
     * Відновлює колекцію елементів гравця.
     * </p>
     *
     * @param data Тег, з якого будуть завантажені дані
     */
    @Override
    public void load(CompoundTag data) {
        if (data.contains("resetExistingData")) {
            this.elements.clear();
        }
        for (Tag tag : data.getList(ELEMENTS_KEY, Tag.TAG_COMPOUND)) {
            try {
                ElementInstance instance = ElementInstance.fromNBT((CompoundTag) tag);
                this.elements.put(instance.getElementId(), instance);
            } catch (Exception e) {
                EternalCoreStorage.LOG.error("Failed to load element instance from NBT", e);
            }
        }
    }

    /**
     * Отримує власника сховища як живу сутність.
     *
     * @return Власник сховища як LivingEntity
     */
    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }

    /**
     * Updates a element instance and optionally synchronizes the change across the network.
     * <p>
     *
     * @param updatedInstance The instance to update
     * @param sync            If true, synchronizes the change to all clients/server
     */
    @Override
    public void updateElement(ElementInstance updatedInstance, boolean sync) {
        updatedInstance.markDirty();
        elements.put(updatedInstance.getElementId(), updatedInstance);
        if (sync) markDirty();
    }
    @Override
    public void forEachElement(BiConsumer<ElementsStorage, ElementInstance> elementInstanceBiConsumer) {
        List.copyOf(this.elements.values()).forEach(elementInstance -> elementInstanceBiConsumer.accept(this, elementInstance));
        markDirty();
    }

    @Override
    public void forgetElement(@NotNull ResourceLocation resourceLocation, @Nullable MutableComponent component) {
        if (!this.elements.containsKey(resourceLocation)) return;
        ElementInstance instance = this.elements.get(resourceLocation);

        Changeable<MutableComponent> forgetMessage = Changeable.of(component);
        EventResult result = ElementEvents.FORGET_ELEMENT.invoker().forget(instance, getOwner(), forgetMessage);
        if (result.isFalse()) return;

        if (forgetMessage.isPresent()) getOwner().sendSystemMessage(forgetMessage.get());
        instance.markDirty();

        this.getObtainedElements().remove(instance);
        this.hasRemovedElements = true;
        markDirty();
    }

    /**
     * Додає новий елемент до колекції гравця.
     * <p>
     * Цей метод викликає подію {@link ElementEvents#ADD_ELEMENT}, яка може бути скасована
     * обробниками подій. Якщо елемент успішно додано, він позначається як змінений
     * і додається до колекції елементів.
     * </p>
     *
     * @param elementInstance Екземпляр елемента для додавання
     * @param breakthrough Чи є це проривом (breakthrough)
     * @param notifyPlayer Чи повідомляти гравця про додавання
     * @param component Компонент повідомлення (може бути null)
     * @return true, якщо елемент було успішно додано, false в іншому випадку
     */
    @Override
    public boolean addElement(ElementInstance elementInstance, boolean breakthrough, boolean notifyPlayer, @Nullable MutableComponent component) {
        if (elements.containsKey(elementInstance.getElementId())){
            EternalCoreStorage.LOG.debug("Tried to register a deduplicate of {}.", elementInstance.getElementId());
            return false;
        }

        Changeable<MutableComponent> addMessage = Changeable.of(component);
        Changeable<Boolean> notify = Changeable.of(notifyPlayer);
        EventResult result = ElementEvents.ADD_ELEMENT.invoker().add(elementInstance, getOwner(), breakthrough, notify, addMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();
        elementInstance.markDirty();
        elements.put(elementInstance.getElementId(), elementInstance);
        markDirty();
        if (addMessage.isPresent()) getOwner().sendSystemMessage(addMessage.get());
        return true;
    }

    @Override
    public void saveOutdated(CompoundTag data) {
        if (this.hasRemovedElements) {
            this.hasRemovedElements = false;
            data.putBoolean("resetExistingData", true);
            super.saveOutdated(data);
        } else {
            ListTag elementsTag = new ListTag();
            for (ElementInstance instance : this.elements.values()) {
                if (!instance.isDirty()) continue;
                elementsTag.add(instance.toNBT());
                instance.resetDirty();
            }
            data.put(ELEMENTS_KEY, elementsTag);
        }
    }

    public void sync(){
        CompoundTag data = new CompoundTag();
        saveOutdated(data);
        InternalStorageActions.sendSyncStoragePayload(StorageType.ELEMENTS, data);
    }
}