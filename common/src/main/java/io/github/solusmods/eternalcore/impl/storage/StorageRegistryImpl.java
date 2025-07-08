package io.github.solusmods.eternalcore.impl.storage;

import com.mojang.datafixers.util.Pair;
import io.github.solusmods.eternalcore.api.storage.AbstractStorage;
import io.github.solusmods.eternalcore.api.storage.StorageEvents;
import io.github.solusmods.eternalcore.api.storage.StorageHolder;
import io.github.solusmods.eternalcore.api.storage.StorageKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Внутрішня реалізація реєстру сховищ для певного типу власника.
 * <p>
 * Ця приватна внутрішня реалізація керує реєстрацією та приєднанням сховищ
 * для конкретного типу власника (сутність, чанк або світ).
 * </p>
 *
 * @param <T> Тип власника сховища
 */
public class StorageRegistryImpl<T extends StorageHolder> implements StorageEvents.StorageRegistry<T> {
    /**
     * Мапа, що зберігає зареєстровані фабрики сховищ з предикатами перевірки
     */
    public final Map<ResourceLocation, Pair<Predicate<T>, StorageEvents.StorageFactory<T, ?>>> registry = new HashMap<>();

    /**
     * Реєструє новий тип сховища для власників даного типу.
     *
     * @param id           Ідентифікатор сховища
     * @param storageClass Клас сховища
     * @param attachCheck  Предикат, що перевіряє, чи потрібно приєднувати сховище
     * @param factory      Фабрика для створення екземплярів сховища
     * @param <S>          Тип сховища
     * @return Ключ для доступу до зареєстрованого сховища
     */
    @Override
    public <S extends AbstractStorage> StorageKey<S> register(ResourceLocation id, Class<S> storageClass, Predicate<T> attachCheck, StorageEvents.StorageFactory<T, S> factory) {
        this.registry.put(id, Pair.of(attachCheck, factory));
        return new StorageKey<>(id, storageClass);
    }

    /**
     * Приєднує всі необхідні сховища до цільового власника.
     * <p>
     * Перевіряє кожне зареєстроване сховище і, якщо предикат повертає true,
     * створює та приєднує відповідне сховище до власника.
     * </p>
     *
     * @param target Власник, до якого приєднуються сховища
     */
    public void attach(T target) {
        this.registry.forEach((id, checkAndFactory) -> {
            if (!checkAndFactory.getFirst().test(target)) return;
            AbstractStorage storage = checkAndFactory.getSecond().create(target);
            target.eternalCore$attachStorage(id, storage);
        });
    }
}
