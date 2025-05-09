package io.github.solusmods.eternalcore.element.impl;

import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.element.EternalCoreElements;
import io.github.solusmods.eternalcore.element.api.ElementEvents;
import io.github.solusmods.eternalcore.element.api.ElementInstance;
import io.github.solusmods.eternalcore.element.api.Elements;
import io.github.solusmods.eternalcore.element.impl.network.InternalStorageActions;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.network.api.util.StorageType;
import io.github.solusmods.eternalcore.storage.api.*;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    
    /** Ключ для зберігання домінуючого елемента у NBT */
    private static final String DOMINANT_ELEMENT_KEY = "dominant_element_key";
    
    /** Унікальний ідентифікатор цього типу сховища */
    public static final ResourceLocation ID = EternalCoreElements.create("elements_storage");
    
    /** Ключ для доступу до цього сховища */
    @Getter
    private static StorageKey<ElementsStorage> key = null;
    
    /** Колекція елементів, якими володіє гравець */
    private Collection<ElementInstance> elements = new ArrayList<>();
    
    /** Поточний домінуючий елемент гравця */
    private ElementInstance element;
    
    /**
     * Створює нове сховище елементів для вказаного власника.
     *
     * @param holder Власник цього сховища
     */
    protected ElementsStorage(StorageHolder holder) {
        super(holder);
    }

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
        if (element != null)
            data.put(DOMINANT_ELEMENT_KEY, this.element.toNBT());
        saveInstanceCollection(data, ELEMENTS_KEY, elements, ElementInstance::toNBT, ElementInstance::getElementId);
    }

    /**
     * Завантажує стан сховища з NBT формату.
     * <p>
     * Відновлює домінуючий елемент та колекцію елементів гравця.
     * </p>
     *
     * @param data Тег, з якого будуть завантажені дані
     */
    @Override
    public void load(CompoundTag data) {
        if (data.contains(DOMINANT_ELEMENT_KEY, 10)) {
            element = ElementInstance.fromNBT(data.getCompound(DOMINANT_ELEMENT_KEY));
        }
        loadCollections(data);
    }

    /**
     * Завантажує колекції з NBT даних.
     * <p>
     * Допоміжний метод для завантаження колекції елементів.
     * </p>
     *
     * @param data Тег, з якого будуть завантажені колекції
     */
    private void loadCollections(CompoundTag data) {
        elements.clear();
        loadInstanceCollection(data, ELEMENTS_KEY, elements, ElementInstance::fromNBT);
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
     * Отримує колекцію всіх елементів, якими володіє гравець.
     *
     * @return Колекція екземплярів елементів
     */
    @Override
    public Collection<ElementInstance> getElements() {
        return elements;
    }

    /**
     * Отримує поточний домінуючий елемент гравця.
     *
     * @return Optional, що містить домінуючий елемент, або пустий Optional, якщо елемент не встановлено
     */
    @Override
    public Optional<ElementInstance> getElement() {
        return Optional.ofNullable(element);
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
        Changeable<MutableComponent> realmMessage = Changeable.of(component);
        Changeable<Boolean> notify = Changeable.of(notifyPlayer);
        EventResult result = ElementEvents.ADD_ELEMENT.invoker().add(elementInstance, getOwner(), breakthrough, notify, realmMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();
        if (realmMessage.isPresent()) getOwner().sendSystemMessage(realmMessage.get());
        elementInstance.markDirty();
        elements.add(elementInstance);
        markDirty();
        return true;
    }

    /**
     * Встановлює домінуючий елемент для гравця.
     * <p>
     * Цей метод викликає подію {@link ElementEvents#SET_ELEMENT}, яка може бути скасована
     * обробниками подій. Якщо елемент успішно встановлено, він позначається як змінений
     * і стає новим домінуючим елементом гравця.
     * </p>
     *
     * @param elementInstance Екземпляр елемента для встановлення як домінуючий
     * @param breakthrough Чи є це проривом (breakthrough)
     * @param notifyPlayer Чи повідомляти гравця про зміну
     * @param component Компонент повідомлення (може бути null)
     * @return true, якщо елемент було успішно встановлено, false в іншому випадку
     */
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

    public void sync(){
        CompoundTag data = new CompoundTag();
        saveOutdated(data);
        InternalStorageActions.sendSyncStoragePayload(StorageType.ELEMENTS, data);
    }
}