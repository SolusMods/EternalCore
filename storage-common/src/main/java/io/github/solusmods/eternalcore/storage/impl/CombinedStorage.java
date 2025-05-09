package io.github.solusmods.eternalcore.storage.impl;

import io.github.solusmods.eternalcore.storage.EternalCoreStorage;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Комбіноване сховище, яке містить і керує кількома окремими сховищами ({@link Storage}).
 * <p>
 * Цей клас відповідає за обробку серіалізації, десеріалізації та синхронізації
 * кількох сховищ даних. Він агрегує різні сховища, ідентифіковані за їхніми
 * {@link ResourceLocation} ідентифікаторами, і забезпечує єдиний інтерфейс
 * для збереження/завантаження всіх сховищ одночасно.
 * </p>
 */
public class CombinedStorage {
    /** Ключ для списку сховищ у NBT даних */
    private static final String STORAGE_LIST_KEY = "eternalCore_registry_storage";

    /** Ключ для ідентифікатора сховища у NBT даних */
    private static final String STORAGE_ID_KEY = "eternalCore_registry_storage_id";

    /** Карта, що містить усі сховища, індексовані за їхніми ідентифікаторами */
    private final Map<ResourceLocation, Storage> storages = new HashMap<>();

    /** Власник цього комбінованого сховища */
    private final StorageHolder holder;

    /**
     * Створює нове комбіноване сховище для вказаного власника.
     *
     * @param holder Власник сховища
     */
    public CombinedStorage(StorageHolder holder) {
        this.holder = holder;
    }

    /**
     * Серіалізує всі сховища у NBT формат.
     *
     * @return CompoundTag, що містить серіалізовані дані всіх сховищ
     */
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag entriesTag = new ListTag();
        this.storages.forEach((id, storage) -> {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString(STORAGE_ID_KEY, id.toString());
            storage.save(entryTag);
            entriesTag.add(entryTag);
        });

        tag.put(STORAGE_LIST_KEY, entriesTag);
        return tag;
    }

    /**
     * Завантажує всі сховища з NBT даних.
     * <p>
     * Цей метод створює та ініціалізує сховища на основі даних у вказаному тезі.
     * Якщо сховище не може бути створене, воно пропускається з попередженням у логах.
     * </p>
     *
     * @param tag CompoundTag, що містить серіалізовані дані сховищ
     */
    public void load(CompoundTag tag) {
        ListTag entriesTag = tag.getList(STORAGE_LIST_KEY, Tag.TAG_COMPOUND);

        entriesTag.forEach(t -> {
            // Get serialized storage data
            CompoundTag entryTag = (CompoundTag) t;
            // Get storage id
            ResourceLocation id = ResourceLocation.parse(entryTag.getString(STORAGE_ID_KEY));
            // Construct storage
            Storage storage = StorageManager.constructStorageFor(this.holder.eternalCore$getStorageType(), id, holder);
            if (storage == null) {
                EternalCoreStorage.LOG.warn("Failed to construct storage for id {}. All information about this storage will be dropped!", id);
                return;
            }
            // Load storage data
            storage.load(entryTag);
            // Put storage into map
            this.storages.put(id, storage);
        });
    }

    /**
     * Обробляє пакет оновлення для синхронізації сховищ.
     * <p>
     * Отримує дані оновлення з пакету та оновлює відповідні сховища.
     * Якщо сховище з вказаним ідентифікатором не знайдено, воно пропускається
     * з попередженням у логах.
     * </p>
     *
     * @param tag CompoundTag, що містить дані оновлення для сховищ
     */
    public void handleUpdatePacket(CompoundTag tag) {
        ListTag entriesTag = tag.getList(STORAGE_LIST_KEY, Tag.TAG_COMPOUND);

        for (Tag e : entriesTag) {
            CompoundTag entryTag = (CompoundTag) e;
            ResourceLocation id = ResourceLocation.tryParse(entryTag.getString(STORAGE_ID_KEY));
            Storage storage = this.storages.get(id);
            if (storage == null) {
                EternalCoreStorage.LOG.warn("Failed to find storage for id {}. All information about this storage will be dropped!", id);
                continue;
            }

            storage.loadUpdate(entryTag);
        }
    }

    /**
     * Додає нове сховище до цього комбінованого сховища.
     *
     * @param id Ідентифікатор сховища
     * @param storage Екземпляр сховища для додавання
     */
    public void add(ResourceLocation id, Storage storage) {
        this.storages.put(id, storage);
    }

    /**
     * Отримує сховище за його ідентифікатором.
     *
     * @param id Ідентифікатор сховища
     * @return Екземпляр сховища або null, якщо сховище з таким ідентифікатором не знайдено
     */
    @Nullable
    public Storage get(ResourceLocation id) {
        return this.storages.get(id);
    }

    /**
     * Створює пакет оновлення, що містить дані змінених сховищ.
     * <p>
     * Цей метод збирає дані лише з тих сховищ, які були позначені як "брудні" (dirty),
     * тобто їхні дані змінилися з моменту останньої синхронізації.
     * </p>
     *
     * @param clean Якщо true, очищає статус "брудності" сховищ після створення пакету
     * @return CompoundTag, що містить дані оновлення для змінених сховищ
     */
    public CompoundTag createUpdatePacket(boolean clean) {
        CompoundTag tag = new CompoundTag();

        ListTag entriesTag = new ListTag();
        this.storages.forEach((id, storage) -> {
            if (!storage.isDirty()) return;
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString(STORAGE_ID_KEY, id.toString());
            storage.saveOutdated(entryTag);
            entriesTag.add(entryTag);
            if (clean) storage.clearDirty();
        });

        tag.put(STORAGE_LIST_KEY, entriesTag);
        return tag;
    }

    /**
     * Перевіряє, чи було змінено будь-яке зі сховищ.
     *
     * @return true, якщо будь-яке зі сховищ позначено як "брудне" (dirty)
     */
    public boolean isDirty() {
        for (Storage storage : this.storages.values()) {
            if (storage.isDirty()) return true;
        }
        return false;
    }
}