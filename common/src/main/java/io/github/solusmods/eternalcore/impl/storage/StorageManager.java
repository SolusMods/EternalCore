package io.github.solusmods.eternalcore.impl.storage;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.api.storage.*;
import io.github.solusmods.eternalcore.impl.storage.network.s2c.StorageSyncPayload;
import io.github.solusmods.eternalcore.impl.storage.network.s2c.SyncChunkStoragePayload;
import io.github.solusmods.eternalcore.impl.storage.network.s2c.SyncEntityStoragePayload;
import io.github.solusmods.eternalcore.impl.storage.network.s2c.SyncWorldStoragePayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

/**
 * Центральний менеджер сховищ для мода EternalCore.
 * <p>
 * Цей клас відповідає за реєстрацію, ініціалізацію, управління та синхронізацію сховищ
 * для різних типів об'єктів гри (сутності, чанки, світи). Він реалізує систему збереження та
 * відновлення даних між клієнтом та сервером, а також керує життєвим циклом сховищ.
 * </p>
 * <p>
 * StorageManager забезпечує наступну функціональність:
 * <ul>
 *   <li>Реєстрація сховищ для різних типів об'єктів</li>
 *   <li>Створення та приєднання сховищ до відповідних власників</li>
 *   <li>Синхронізація даних сховищ між клієнтом та сервером</li>
 *   <li>Обробка подій життєвого циклу гравця для підтримки цілісності даних</li>
 * </ul>
 * </p>
 */
public final class StorageManager {

    /**
     * Реєстр сховищ для сутностей
     */
    private static final StorageRegistryImpl<Entity> ENTITY_STORAGE_REGISTRY = new StorageRegistryImpl<>();

    /**
     * Реєстр сховищ для чанків
     */
    private static final StorageRegistryImpl<LevelChunk> CHUNK_STORAGE_REGISTRY = new StorageRegistryImpl<>();

    /**
     * Реєстр сховищ для світів
     */
    private static final StorageRegistryImpl<Level> LEVEL_STORAGE_REGISTRY = new StorageRegistryImpl<>();

    /**
     * Приватний конструктор для запобігання створенню екземплярів.
     * Цей клас призначений для використання лише через статичні методи.
     */
    private StorageManager() {
    }

    /**
     * Ініціалізує систему сховищ.
     * <p>
     * Цей метод реєструє всі події, необхідні для правильної роботи системи сховищ,
     * включаючи обробники для подій приєднання гравця, респауну та зміни вимірів.
     * Також налаштовується копіювання сховища при клонуванні гравця.
     * </p>
     * <p>
     * Повинен бути викликаний один раз під час ініціалізації мода.
     * </p>
     */
    public static void init() {
        StorageEvents.REGISTER_WORLD_STORAGE.invoker().register(LEVEL_STORAGE_REGISTRY);
        StorageEvents.REGISTER_CHUNK_STORAGE.invoker().register(CHUNK_STORAGE_REGISTRY);
        StorageEvents.REGISTER_ENTITY_STORAGE.invoker().register(ENTITY_STORAGE_REGISTRY);
        // Initial client synchronization
        PlayerEvent.PLAYER_JOIN.register(player -> {
            player.eternalCore$sync(player);
            ServerLevel level = player.serverLevel();
            level.eternalCore$sync(player);
        });
        // Synchronization on respawn and dimension change
        PlayerEvent.PLAYER_RESPAWN.register((player, b, removalReason) -> {
            player.eternalCore$sync(player);
            ServerLevel level = player.serverLevel();
            level.eternalCore$sync(player);
        });
        PlayerEvent.CHANGE_DIMENSION.register((player, resourceKey, resourceKey1) -> {
            player.eternalCore$sync(player);
            ServerLevel level = player.serverLevel();
            level.eternalCore$sync(player);
        });

        // Copy storage from old player to new player
        PlayerEvent.PLAYER_CLONE.register((oldPlayer, newPlayer, wonGame) -> {
            CombinedStorage newStorage = new CombinedStorage(newPlayer);
            newStorage.load(oldPlayer.eternalCore$getCombinedStorage().toNBT());
            newPlayer.eternalCore$setCombinedStorage(newStorage);
        });
    }

    /**
     * Заповнює власника сховища всіма зареєстрованими сховищами відповідного типу.
     * <p>
     * Цей метод викликається під час ініціалізації об'єкта, що реалізує інтерфейс {@link StorageHolder},
     * щоб створити та приєднати всі необхідні сховища відповідно до його типу.
     * </p>
     *
     * @param holder Власник сховища, для якого створюються сховища
     */
    public static void initialStorageFilling(StorageHolder holder) {
        switch (holder.eternalCore$getStorageType()) {
            case ENTITY -> ENTITY_STORAGE_REGISTRY.attach((Entity) holder);
            case CHUNK -> CHUNK_STORAGE_REGISTRY.attach((LevelChunk) holder);
            case WORLD -> LEVEL_STORAGE_REGISTRY.attach((Level) holder);
        }
    }

    /**
     * Синхронізує дані сховища з усіма гравцями, що відстежують джерело.
     * <p>
     * Використовує значення false для параметра update за замовчуванням.
     * </p>
     *
     * @param source Джерело даних для синхронізації
     */
    public static void syncTracking(StorageHolder source) {
        syncTracking(source, false);
    }

    /**
     * Синхронізує дані сховища з усіма гравцями, що відстежують джерело.
     * <p>
     * Якщо параметр update встановлено в true, буде відправлено тільки оновлені дані,
     * в іншому випадку - весь стан сховища.
     * </p>
     *
     * @param source Джерело даних для синхронізації
     * @param update Флаг, що вказує, чи надсилати лише оновлення
     */
    public static void syncTracking(StorageHolder source, boolean update) {
        NetworkManager.sendToPlayers(source.eternalCore$getTrackingPlayers(), createSyncPacket(source, update));
    }

    /**
     * Синхронізує дані сховища з конкретним цільовим гравцем.
     * <p>
     * Використовується для відправки повного стану сховища одному гравцю.
     * </p>
     *
     * @param source Джерело даних для синхронізації
     * @param target Гравець, якому надсилаються дані
     */
    public static void syncTarget(StorageHolder source, ServerPlayer target) {
        NetworkManager.sendToPlayer(target, createSyncPacket(source, false));
    }

    /**
     * Створює пакет синхронізації для відправки з сервера на клієнт.
     * <p>
     * Пакет залежить від типу власника сховища (сутність, чанк або світ).
     * </p>
     *
     * @param source Джерело даних для синхронізації
     * @param update Флаг, що вказує, чи надсилати лише оновлення
     * @return Пакет синхронізації відповідного типу
     */
    public static StorageSyncPayload createSyncPacket(StorageHolder source, boolean update) {
        return switch (source.eternalCore$getStorageType()) {
            case ENTITY -> {
                Entity sourceEntity = (Entity) source;
                yield new SyncEntityStoragePayload(
                        update,
                        sourceEntity.getId(),
                        update ? sourceEntity.eternalCore$getCombinedStorage().createUpdatePacket(true)
                                : sourceEntity.eternalCore$getCombinedStorage().toNBT()
                );
            }
            case CHUNK -> {
                LevelChunk sourceChunk = (LevelChunk) source;
                yield new SyncChunkStoragePayload(
                        update,
                        sourceChunk.getPos(),
                        update ? sourceChunk.eternalCore$getCombinedStorage().createUpdatePacket(true)
                                : sourceChunk.eternalCore$getCombinedStorage().toNBT()
                );
            }
            case WORLD -> new SyncWorldStoragePayload(
                    update,
                    update ? source.eternalCore$getCombinedStorage().createUpdatePacket(true)
                            : source.eternalCore$getCombinedStorage().toNBT()
            );
        };
    }

    /**
     * Створює нове сховище для вказаного власника та ідентифікатора.
     * <p>
     * Цей метод використовується для динамічного створення сховищ за потреби.
     * </p>
     *
     * @param type   Тип сховища (сутність, чанк або світ)
     * @param id     Ідентифікатор сховища
     * @param holder Власник сховища
     * @return Створене сховище або null, якщо створення неможливе
     */
    @Nullable
    public static AbstractStorage constructStorageFor(StorageType type, ResourceLocation id, StorageHolder holder) {
        return switch (type) {
            case ENTITY -> ENTITY_STORAGE_REGISTRY.registry.get(id).getSecond().create((Entity) holder);
            case CHUNK -> CHUNK_STORAGE_REGISTRY.registry.get(id).getSecond().create((LevelChunk) holder);
            case WORLD -> LEVEL_STORAGE_REGISTRY.registry.get(id).getSecond().create((Level) holder);
        };
    }

    /**
     * Отримує сховище вказаного типу від власника.
     * <p>
     * Це зручний метод для доступу до сховища через ключ.
     * </p>
     *
     * @param holder     Власник сховища
     * @param storageKey Ключ сховища
     * @param <T>        Тип сховища
     * @return Сховище вказаного типу або null, якщо сховище не знайдено
     */
    @Nullable
    public static <T extends AbstractStorage> T getStorage(StorageHolder holder, StorageKey<T> storageKey) {
        return holder.eternalCore$getStorage(storageKey);
    }


}
