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
import lombok.Getter;
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
 * Сховище для управління елементами гравця в системі EternalCore.
 * <p>
 * Цей клас відповідає за зберігання, управління та синхронізацію елементів,
 * які гравець здобуває протягом гри. Елементи є важливою частиною системи культивації
 * та визначають спеціальні здібності та атрибути гравця.
 * </p>
 * <p>
 * ElementsStorage реалізує інтерфейс {@link AbstractQiEnergy} та розширює базовий клас {@link AbstractStorage},
 * успадковуючи функціональність збереження та завантаження даних у форматі NBT.
 * </p>
 */
public class QiEnergyStorage extends AbstractStorage implements QiEnergies {
    /**
     * Унікальний ідентифікатор цього типу сховища
     */
    public static final ResourceLocation ID = EternalCore.create("qi_energies_storage");
    /**
     * Ключ для зберігання колекції елементів у NBT
     */
    private static final String QI_ENERGIES_KEY = "qi_energies_key";
    /**
     * Ключ для доступу до цього сховища
     */
    @Getter
    private static StorageKey<QiEnergyStorage> key = null;

    /**
     * Колекція елементів, якими володіє гравець
     */
    @Getter
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

    public Collection<ElementalQiEnergy> getObtainedQiEnergies() {
        return this.ElementalQiEnergies.values();
    }

    @Override
    public void addQi(ResourceLocation qiEnergyId, double amount) {
        ElementalQiEnergy qiEnergy = getOrCreateQiEnergy(qiEnergyId);
        double before = qiEnergy.getAmount();
        qiEnergy.add(amount);
        if (Double.compare(before, qiEnergy.getAmount()) != 0) {
            markDirty();
        }
    }

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

    @Override
    public void consumeQi(ResourceLocation qiEnergyId, double amount) {
        ElementalQiEnergy qiEnergy = getExistingQiEnergy(qiEnergyId, "consume");
        double before = qiEnergy.getAmount();
        qiEnergy.subtract(amount);
        if (Double.compare(before, qiEnergy.getAmount()) != 0) {
            markDirty();
        }
    }

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
     * Зберігає стан сховища в NBT формат.
     * <p>
     * Зберігає домінуючий елемент та колекцію всіх елементів гравця.
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

    @Override
    public String toString() {
        return String.format("%s{qiEnergies=[%s], owner={%s}}", this.getClass().getSimpleName(),  getElementalQiEnergies().toString(), getOwner().toString());
    }
}