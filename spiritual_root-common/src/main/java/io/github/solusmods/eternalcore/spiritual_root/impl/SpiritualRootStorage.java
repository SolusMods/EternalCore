package io.github.solusmods.eternalcore.spiritual_root.impl;

import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot;
import io.github.solusmods.eternalcore.spiritual_root.api.*;
import io.github.solusmods.eternalcore.spiritual_root.impl.network.InternalSpiritualRootPacketActions;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageEvents;
import io.github.solusmods.eternalcore.storage.api.StorageHolder;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Клас для зберігання та управління духовними коренями сутності.
 * Реалізує інтерфейс {@link SpiritualRoots} та розширює {@link Storage}.
 */
public class SpiritualRootStorage extends Storage implements SpiritualRoots {
    private static final String SPIRITUAL_ROOTS_KEY = "spiritual_roots_key";
    public static final ResourceLocation ID = EternalCoreSpiritualRoot.create("spiritual_root_storage");
    @Getter
    private static StorageKey<SpiritualRootStorage> key = null;
    private Collection<SpiritualRootInstance> spiritualRoots = new ArrayList<>();
    protected SpiritualRootStorage(StorageHolder holder) {
        super(holder);
    }

    /**
     * Ініціалізує систему зберігання духовних коренів.
     * Реєструє сховище для гравців.
     */
    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        SpiritualRootStorage.class, Entity.class::isInstance,
                        SpiritualRootStorage::new));
    }

    /**
     * Зберігає стан духовних коренів у NBT.
     *
     * @param data CompoundTag для збереження даних
     */
    @Override
    public void save(CompoundTag data) {
        saveInstanceCollection(data, SPIRITUAL_ROOTS_KEY, spiritualRoots, SpiritualRootInstance::toNBT, SpiritualRootInstance::getSpiritualRootId);
    }

    /**
     * Завантажує стан духовних коренів з NBT.
     *
     * @param data CompoundTag з даними для завантаження
     */
    @Override
    public void load(CompoundTag data) {
        loadCollections(data);
    }

    private void loadCollections(CompoundTag data) {
        loadInstanceCollection(data, SPIRITUAL_ROOTS_KEY, spiritualRoots, SpiritualRootInstance::fromNBT);
    }

    /**
     * Отримує власника цього сховища.
     *
     * @return Жива сутність-власник сховища
     */
    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }

    /**
     * Повертає колекцію всіх духовних коренів сутності.
     *
     * @return Незмінна колекція екземплярів духовних коренів
     */
    @Override
    public Collection<SpiritualRootInstance> getSpiritualRoots() {
        return spiritualRoots;
    }

    /**
     * Встановлює нову колекцію духовних коренів.
     *
     * @param roots Нова колекція духовних коренів
     */
    public void setSpiritualRoots(Collection<SpiritualRootInstance> roots) {
        this.spiritualRoots = roots;
        markDirty();
    }

    /**
     * Додає новий духовний корінь до сутності.
     *
     * @param instance Екземпляр духовного кореня для додавання
     * @param advance Чи потрібно застосовувати просунуті ефекти
     * @param notify Чи потрібно сповіщати гравця
     * @param component Компонент повідомлення для сповіщення (може бути null)
     * @return true якщо корінь успішно додано, false якщо додавання скасовано
     */
    @Override
    public boolean addSpiritualRoot(SpiritualRootInstance instance, boolean advance, boolean notify, @Nullable MutableComponent component) {
        Changeable<MutableComponent> rootMessage = Changeable.of(component);
        Changeable<Boolean> notifyPlayer = Changeable.of(notify);
        EventResult result = SpiritualRootEvents.ADD.invoker().add(instance, getOwner(), advance, notifyPlayer, rootMessage);
        if (result.isFalse()) return false;
        LivingEntity owner = getOwner();
        if (rootMessage.isPresent()) getOwner().sendSystemMessage(rootMessage.get());
        instance.markDirty();
        instance.onAdd(owner);
        spiritualRoots.add(instance);
        markDirty();
        return true;
    }

    /**
     * Розраховує ефективність культивації для конкретного духовного кореня.
     *
     * @param rootInstance Екземпляр духовного кореня для розрахунку
     * @return Значення ефективності від 0.1 до 1.0
     */
    @Override
    public float getCultivationEfficiency(SpiritualRootInstance rootInstance) {
        float efficiency = 0.2f; // Базова ефективність навіть без відповідного кореня

        for (SpiritualRootInstance instance : getSpiritualRoots()) {
            if (instance.equals(rootInstance)) {
                // Знайдено відповідний корінь, додаємо його вплив
                efficiency += 0.8f * instance.getStrength();
            } else if (instance.getOpposite(getOwner()).equals(rootInstance)) {
                // Знайдено протилежний корінь, зменшуємо ефективність
                efficiency -= 0.3f * instance.getStrength();
            }
        }
        // Обмеження мінімальної ефективності
        return Math.max(0.1f, efficiency);
    }

    /**
     * Розраховує множник швидкості культивації на основі кількості коренів.
     *
     * @return Множник швидкості культивації
     */
    public float getCultivationSpeedMultiplier() {
        // Чим менше коренів, тим більший загальний множник швидкості
        return switch (spiritualRoots.size()) {
            case 1 -> 1.5f; // Найвища концентрація, найшвидша культивація
            case 2 -> 1.3f;
            case 3 -> 1.0f; // Стандартна швидкість
            case 4 -> 0.8f;
            case 5 -> 0.6f; // Найнижча концентрація, найповільніша культивація
            default -> 0.5f;       // Якщо немає коренів або інші особливі випадки
        };
    }

    /**
     * Генерує випадкові духовні корені для сутності.
     * Корені генеруються тільки якщо сутність ще не має коренів.
     *
     * @param roots Список доступних типів духовних коренів
     */
    public void generateRandomRoots(List<SpiritualRoot> roots) {
        if (!getSpiritualRoots().isEmpty()) return;

        // Визначаємо кількість коренів (1-5)
        int rootCount = getRandomRootCount();

        // Вибираємо випадкові типи без повторень

        Collections.shuffle(roots);

        for (int i = 0; i < rootCount; i++) {
            if (i < roots.size()) {
                SpiritualRoot root = roots.get(i);
                // Чим менше коренів, тим більша їх сила
                float baseStrength = 1.0f / rootCount;
                // Додаємо випадковість до сили кожного кореня
                float randomFactor = 0.8f + (float) (Math.random() * 0.4f);
                float strength = baseStrength * randomFactor;
                SpiritualRootInstance rootInstance = root.createDefaultInstance();
                rootInstance.setStrength(rootInstance.getStrength()+strength);
                rootInstance.markDirty();
                addSpiritualRoot(rootInstance, false, false);
                markDirty();
            }
        }
    }

    /**
     * Визначає домінуючий тип духовного кореня на основі сили.
     *
     * @return Тип найсильнішого кореня або null якщо коренів немає
     */
    public @Nullable SpiritualRootInstance getDominantRoot() {
        if (spiritualRoots.isEmpty()) {
            return null;
        }

        return Collections.max(spiritualRoots,
                Comparator.comparing(SpiritualRootInstance::getStrength));
    }

    public void sync(){
        CompoundTag data = new CompoundTag();
        saveOutdated(data);
        InternalSpiritualRootPacketActions.sendSyncStoragePayload(data);
    }
}
