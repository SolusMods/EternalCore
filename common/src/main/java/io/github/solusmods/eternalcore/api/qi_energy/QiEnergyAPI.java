package io.github.solusmods.eternalcore.api.qi_energy;

import dev.architectury.registry.registries.Registrar;
import io.github.solusmods.eternalcore.api.registry.ElementTypeRegistry;
import io.github.solusmods.eternalcore.impl.qi_energy.QiEnergyStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

/**
 * API для роботи з Елементами у моді EternalCore.
 * Надає доступ до реєстру елементів та утиліти для взаємодії з живими сутностями.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QiEnergyAPI {

    public static final Registrar<ElementType> ELEMENT_REGISTRY = ElementTypeRegistry.ELEMENT_TYPES;
    public static final ResourceKey<Registry<ElementType>> ELEMENT_REGISTRY_KEY = ElementTypeRegistry.KEY;

    public static Registrar<ElementType> getElementRegistry() {
        return ELEMENT_REGISTRY;
    }

    public static ResourceKey<Registry<ElementType>> getElementRegistryKey() {
        return ELEMENT_REGISTRY_KEY;
    }

    /**
     * Повертає домінуючий елемент у сутності.
     *
     * @param entity Сутність
     * @return Домінуючий елемент або null
     */
    public static QiEnergies getDominantElementFrom(LivingEntity entity) {
        return entity.eternalCore$getStorage(QiEnergyStorage.getKey());
    }

    /**
     * Повертає всі елементи, закріплені за сутністю.
     *
     * @param entity Сутність
     * @return Об'єкт Elements або null
     */
    public static QiEnergies getElementsFrom(LivingEntity entity) {
        return entity.eternalCore$getStorage(QiEnergyStorage.getKey());
    }

    /**
     * Повертає контейнер з елементами для сутності (опціонально).
     *
     * @param entity Сутність
     * @return Optional з контейнером ElementsStorage
     */
    public static Optional<QiEnergyStorage> getStorageOptional(LivingEntity entity) {
        return entity.eternalCore$getStorageOptional(QiEnergyStorage.getKey());
    }
}