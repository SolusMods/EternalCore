package io.github.solusmods.eternalcore.impl.qi_energy;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.qi_energy.AbstractQiEnergy;
import io.github.solusmods.eternalcore.api.qi_energy.ElementalQiEnergy;
import io.github.solusmods.eternalcore.api.qi_energy.QiEnergies;
import io.github.solusmods.eternalcore.api.qi_energy.QiEnergyAPI;
import io.github.solusmods.eternalcore.api.storage.AbstractStorage;
import io.github.solusmods.eternalcore.api.storage.StorageEvents;
import io.github.solusmods.eternalcore.api.storage.StorageHolder;
import io.github.solusmods.eternalcore.api.storage.StorageKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Сховище для управління запасами Ці (Qi) сутності.
 * <p>
 * {@link QiEnergyStorage} реєструється через {@link #init()} та після цього надає публічний API
 * для додавання, вилучення та читання {@link ElementalQiEnergy}. Контракт сховища гарантує, що
 * всі операції викликають {@link #markDirty()} лише після фактичної зміни стану, а також що будь-які
 * спроби взаємодії з невідомою енергією завершуються виключенням і повідомленням у лог.
 * </p>
 * <p>
 * Метод {@link #saveOutdated(CompoundTag)} застосовується під час часткової синхронізації з клієнтами:
 * якщо енергію видалено, сховище надсилає прапорець <code>resetExistingData</code>, і клієнт повинен
 * очистити локальний стан перед застосуванням нових значень.
 * </p>
 */
public class QiEnergyStorage extends AbstractStorage implements QiEnergies {
    /**
     * Унікальний ідентифікатор цього типу сховища.
     */
    public static final ResourceLocation ID = EternalCore.create("qi_energies_storage");
    /**
     * Ключ для зберігання колекції елементів у NBT.
     */
    private static final String QI_ENERGIES_KEY = "qi_energies_key";
    /**
     * Ключ для доступу до цього сховища.
     */
    private static StorageKey<QiEnergyStorage> key = null;

    /**
     * Колекція енергій, якими володіє сутність.
     */
    private final Map<ResourceLocation, ElementalQiEnergy> ElementalQiEnergies = new HashMap<>();
    private boolean hasRemovedElements = false;

    /**
     * Створює нове сховище елементів для вказаного власника.
     *
     * @param holder Власник цього сховища
     */
    protected QiEnergyStorage(StorageHolder holder) {
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
                        QiEnergyStorage.class, Entity.class::isInstance,
                        QiEnergyStorage::new));
    }

    /**
     * Повертає ключ доступу до сховища у системі зберігання.
     *
     * @return Ключ сховища, або {@code null}, якщо {@link #init()} ще не викликано
     */
    public static StorageKey<QiEnergyStorage> getKey() {
        return key;
    }

    /**
     * Повертає всі відкриті енергії Ці.
     *
     * @return Набір енергій, відсортований за ідентифікаторами реєстру
     */
    public Collection<ElementalQiEnergy> getObtainedQiEnergies() {
        return this.ElementalQiEnergies.values();
    }

    /**
     * Повертає доступ до внутрішньої мапи енергій.
     * <p>
     * Повертається жива структура даних; виклики повинні гарантувати послідовність змін та виклик
     * {@link #markDirty()} в разі модифікацій.
     * </p>
     *
     * @return Мапа енергій за ідентифікатором
     */
    @Override
    public Map<ResourceLocation, ElementalQiEnergy> getElementalQiEnergies() {
        return this.ElementalQiEnergies;
    }

    /**
     * Додає певну кількість енергії Ці до сховища.
     * <p>
     * Якщо енергія відсутня, вона буде створена на основі зареєстрованого типу.
     * </p>
     *
     * @param qiEnergyId Ідентифікатор енергії Ці
     * @param amount     Кількість, яку необхідно додати (може бути від'ємною для корекції)
     * @throws IllegalArgumentException Якщо ідентифікатор дорівнює {@code null}
     * @throws IllegalStateException    Якщо енергію не зареєстровано в {@link QiEnergyAPI}
     */
    @Override
    public void addQi(ResourceLocation qiEnergyId, double amount) {
        ElementalQiEnergy qiEnergy = getOrCreateQiEnergy(qiEnergyId);
        double before = qiEnergy.getAmount();
        qiEnergy.add(amount);
        if (Double.compare(before, qiEnergy.getAmount()) != 0) {
            markDirty();
        }
    }

    /**
     * Виконує дію над кожною енергією Ці.
     * <p>
     * Метод гарантує, що під час ітерації можна безпечно модифікувати кількість енергії без
     * виключень конкурентності; після завершення викликається {@link #markDirty()} у разі змін.
     * </p>
     *
     * @param action Дія, яку потрібно виконати
     */
    @Override
    public void forEachQiEnergy(BiConsumer<QiEnergyStorage, ElementalQiEnergy> action) {
        boolean mutated = false;
        for (ElementalQiEnergy element : List.copyOf(getElementalQiEnergies().values())) {
            double before = element.getAmount();
            action.accept(this, element);
            if (!mutated && Double.compare(before, element.getAmount()) != 0) {
                mutated = true;
            }
        }
        if (mutated) {
            markDirty();
        }
    }

    /**
     * Витрачає певну кількість енергії Ці.
     *
     * @param qiEnergyId Ідентифікатор енергії Ці
     * @param amount     Кількість для списання
     * @throws IllegalArgumentException Якщо ідентифікатор дорівнює {@code null}
     * @throws IllegalStateException    Якщо енергію не зареєстровано або її не отримано
     */
    @Override
    public void consumeQi(ResourceLocation qiEnergyId, double amount) {
        ElementalQiEnergy qiEnergy = getExistingQiEnergy(qiEnergyId, "consume");
        double before = qiEnergy.getAmount();
        qiEnergy.subtract(amount);
        if (Double.compare(before, qiEnergy.getAmount()) != 0) {
            markDirty();
        }
    }

    /**
     * Повертає кількість певної енергії Ці, доступної у сховищі.
     *
     * @param qiEnergyId Ідентифікатор енергії Ці
     * @return Кількість енергії
     * @throws IllegalArgumentException Якщо ідентифікатор дорівнює {@code null}
     * @throws IllegalStateException    Якщо енергію не зареєстровано або її не отримано
     */
    @Override
    public double getQi(ResourceLocation qiEnergyId) {
        return getExistingQiEnergy(qiEnergyId, "query").getAmount();
    }

    private ElementalQiEnergy getOrCreateQiEnergy(ResourceLocation qiEnergyId) {
        if (qiEnergyId == null) {
            throw new IllegalArgumentException("Qi energy id cannot be null when attempting to add qi.");
        }

        ElementalQiEnergy qiEnergy = this.ElementalQiEnergies.get(qiEnergyId);
        if (qiEnergy != null) {
            return qiEnergy;
        }

        var elementType = QiEnergyAPI.getElementRegistry().get(qiEnergyId);
        if (elementType == null) {
            handleMissingQiEnergy(qiEnergyId, "initialize");
        }

        qiEnergy = new ElementalQiEnergy(elementType, 0);
        this.ElementalQiEnergies.put(qiEnergyId, qiEnergy);
        markDirty();
        return qiEnergy;
    }

    private ElementalQiEnergy getExistingQiEnergy(ResourceLocation qiEnergyId, String operation) {
        if (qiEnergyId == null) {
            throw new IllegalArgumentException("Qi energy id cannot be null when attempting to " + operation + " qi.");
        }

        ElementalQiEnergy qiEnergy = this.ElementalQiEnergies.get(qiEnergyId);
        if (qiEnergy == null) {
            handleMissingQiEnergy(qiEnergyId, operation);
        }
        return qiEnergy;
    }

    private void handleMissingQiEnergy(ResourceLocation qiEnergyId, String operation) {
        String ownerDescription;
        if (this.holder instanceof LivingEntity livingEntity) {
            ownerDescription = livingEntity.getName().getString();
        } else {
            ownerDescription = String.valueOf(this.holder);
        }

        String message = String.format("Qi energy '%s' is not registered for '%s' while attempting to %s qi.",
                qiEnergyId,
                ownerDescription,
                operation);
        EternalCore.LOG.error(message);
        throw new IllegalStateException(message);
    }

    /**
     * Зберігає стан сховища в NBT.
     * <p>
     * Контракт гарантує, що кожен {@link ElementalQiEnergy} буде серіалізовано через
     * {@link ElementalQiEnergy#toNBT()} без побічних ефектів.
     * </p>
     *
     * @param data Тег, в який буде збережено дані
     */
    @Override
    public void save(CompoundTag data) {
        ListTag qiEnergyTag = new ListTag();
        ElementalQiEnergies.values().forEach(instance -> {
            qiEnergyTag.add(instance.toNBT());
        });
        data.put(QI_ENERGIES_KEY, qiEnergyTag);
    }

    /**
     * Завантажує стан сховища з NBT.
     * <p>
     * Під час завантаження попередні дані можуть бути скинуті, якщо присутній ключ
     * <code>resetExistingData</code>, що використовується для синхронізації клієнтів.
     * </p>
     *
     * @param data Тег, з якого будуть завантажені дані
     */
    @Override
    public void load(CompoundTag data) {
        if (data.contains("resetExistingData")) {
            this.ElementalQiEnergies.clear();
        }
        for (Tag tag : data.getList(QI_ENERGIES_KEY, Tag.TAG_COMPOUND)) {
            try {
                ElementalQiEnergy instance = ElementalQiEnergy.fromNBT((CompoundTag) tag);
                if (instance.getElement() == null) continue;
                this.ElementalQiEnergies.put(instance.getElement().getResource(), instance);
            } catch (Exception e) {
                EternalCore.LOG.error("Failed to load qi energy instance from NBT", e);
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
     * Виконує часткову синхронізацію сховища.
     * <p>
     * Якщо будь-яку енергію було видалено, сховище ставить прапорець <code>resetExistingData</code>
     * і делегує серіалізацію батьківському класу. В іншому випадку відправляється лише оновлений список.
     * </p>
     *
     * @param data Тег для запису
     */
    @Override
    public void saveOutdated(CompoundTag data) {
        if (this.hasRemovedElements) {
            this.hasRemovedElements = false;
            data.putBoolean("resetExistingData", true);
            super.saveOutdated(data);
        } else {
            ListTag elementsTag = new ListTag();
            for (ElementalQiEnergy instance : this.ElementalQiEnergies.values()) {
                elementsTag.add(instance.toNBT());
            }
            data.put(QI_ENERGIES_KEY, elementsTag);
        }
    }

    /**
     * Повертає опис стану сховища для відлагодження.
     *
     * @return Рядковий опис
     */
    @Override
    public String toString() {
        return String.format("%s{qiEnergies=[%s], owner={%s}}", this.getClass().getSimpleName(),  getElementalQiEnergies().toString(), getOwner().toString());
    }
}