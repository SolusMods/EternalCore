package io.github.solusmods.eternalcore.storage.impl

import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.storage.api.*
import io.github.solusmods.eternalcore.storage.impl.network.s2c.StorageSyncPayload
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncChunkStoragePayload
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncEntityStoragePayload
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncWorldStoragePayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk
import java.util.function.Predicate

/**
 * Центральний менеджер сховищ для мода EternalCore.
 *
 * Цей клас відповідає за реєстрацію, ініціалізацію, управління та синхронізацію сховищ
 * для різних типів об'єктів гри (сутності, чанки, світи). Він реалізує систему збереження та
 * відновлення даних між клієнтом та сервером, а також керує життєвим циклом сховищ.
 *
 * StorageManager забезпечує наступну функціональність:
 * - Реєстрація сховищ для різних типів об'єктів
 * - Створення та приєднання сховищ до відповідних власників
 * - Синхронізація даних сховищ між клієнтом та сервером
 * - Обробка подій життєвого циклу гравця для підтримки цілісності даних
 */
object StorageManager {
    /** Реєстр сховищ для сутностей */
    private val entityStorageRegistry = StorageRegistryImpl<Entity>()

    /** Реєстр сховищ для чанків */
    private val chunkStorageRegistry = StorageRegistryImpl<LevelChunk>()

    /** Реєстр сховищ для світів */
    private val levelStorageRegistry = StorageRegistryImpl<Level>()

    /**
     * Ініціалізує систему сховищ.
     *
     * Цей метод реєструє всі події, необхідні для правильної роботи системи сховищ,
     * включаючи обробники для подій приєднання гравця, респауну та зміни вимірів.
     * Також налаштовується копіювання сховища при клонуванні гравця.
     *
     * Повинен бути викликаний один раз під час ініціалізації мода.
     */
    @JvmStatic
    fun init() {
        // Register storage registries
        StorageEvents.REGISTER_WORLD_STORAGE.invoker().register(levelStorageRegistry)
        StorageEvents.REGISTER_CHUNK_STORAGE.invoker().register(chunkStorageRegistry)
        StorageEvents.REGISTER_ENTITY_STORAGE.invoker().register(entityStorageRegistry)

        // Initial client synchronization
        PlayerEvent.PLAYER_JOIN.register { player ->
            player.syncStorage()
            player.serverLevel().syncStorage(player)
        }

        // Synchronization on respawn and dimension change
        PlayerEvent.PLAYER_RESPAWN.register { player, _, _ ->
            player.syncStorage()
            player.serverLevel().syncStorage(player)
        }

        PlayerEvent.CHANGE_DIMENSION.register { player, _, _ ->
            player.syncStorage()
            player.serverLevel().syncStorage(player)
        }

        // Copy storage from old player to new player
        PlayerEvent.PLAYER_CLONE.register { oldPlayer, newPlayer, _ ->
            val newStorage = CombinedStorage(newPlayer)
            newStorage.load(oldPlayer.getCombinedStorage().toNBT())
            newPlayer.setCombinedStorage(newStorage)
        }
    }

    /**
     * Заповнює власника сховища всіма зареєстрованими сховищами відповідного типу.
     *
     * Цей метод викликається під час ініціалізації об'єкта, що реалізує інтерфейс [StorageHolder],
     * щоб створити та приєднати всі необхідні сховища відповідно до його типу.
     *
     * @param holder Власник сховища, для якого створюються сховища
     */
    @JvmStatic
    fun initialStorageFilling(holder: StorageHolder) {
        when (holder.getStorageType()) {
            StorageType.ENTITY -> entityStorageRegistry.attach(holder as Entity)
            StorageType.CHUNK -> chunkStorageRegistry.attach(holder as LevelChunk)
            StorageType.WORLD -> levelStorageRegistry.attach(holder as Level)
        }
    }

    /**
     * Синхронізує дані сховища з усіма гравцями, що відстежують джерело.
     *
     * @param source Джерело даних для синхронізації
     * @param update Флаг, що вказує, чи надсилати лише оновлення (за замовчуванням false)
     */
    @JvmStatic
    fun syncTracking(source: StorageHolder, update: Boolean = false) {
        NetworkManager.sendToPlayers(
            source.trackingPlayers,
            createSyncPacket(source, update)
        )
    }

    /**
     * Синхронізує дані сховища з конкретним цільовим гравцем.
     *
     * @param source Джерело даних для синхронізації
     * @param target Гравець, якому надсилаються дані
     */
    @JvmStatic
    fun syncTarget(source: StorageHolder, target: ServerPlayer) {
        NetworkManager.sendToPlayer(target, createSyncPacket(source, update = false))
    }

    /**
     * Створює пакет синхронізації для відправки з сервера на клієнт.
     *
     * @param source Джерело даних для синхронізації
     * @param update Флаг, що вказує, чи надсилати лише оновлення
     * @return Пакет синхронізації відповідного типу
     */
    fun createSyncPacket(source: StorageHolder, update: Boolean): StorageSyncPayload {
        return when (source.getStorageType()) {
            StorageType.ENTITY -> {
                val sourceEntity = source as Entity
                val combinedStorage = sourceEntity.getCombinedStorage()
                SyncEntityStoragePayload(
                    entityId = sourceEntity.id,
                    isUpdate = update,
                    storageTag = if (update) combinedStorage.createUpdatePacket(true) else combinedStorage.toNBT()
                )
            }

            StorageType.CHUNK -> {
                val sourceChunk = source as LevelChunk
                val combinedStorage = sourceChunk.getCombinedStorage()
                SyncChunkStoragePayload(
                    isUpdate = update,
                    chunkPos = sourceChunk.pos,
                    storageTag = if (update) combinedStorage.createUpdatePacket(true) else combinedStorage.toNBT()
                )
            }

            StorageType.WORLD -> {
                val combinedStorage = source.getCombinedStorage()
                SyncWorldStoragePayload(
                    isUpdate = update,
                    storageTag = if (update) combinedStorage.createUpdatePacket(true) else combinedStorage.toNBT()
                )
            }
        }
    }

    /**
     * Створює нове сховище для вказаного власника та ідентифікатора.
     *
     * @param type Тип сховища (сутність, чанк або світ)
     * @param id Ідентифікатор сховища
     * @param holder Власник сховища
     * @return Створене сховище або null, якщо створення неможливе
     */
    fun constructStorageFor(type: StorageType, id: ResourceLocation, holder: StorageHolder): Storage? {
        return when (type) {
            StorageType.ENTITY -> {
                entityStorageRegistry.registry[id]?.second?.create(holder as Entity)
            }
            StorageType.CHUNK -> {
                chunkStorageRegistry.registry[id]?.second?.create(holder as LevelChunk)
            }
            StorageType.WORLD -> {
                levelStorageRegistry.registry[id]?.second?.create(holder as Level)
            }
        }
    }

    /**
     * Отримує сховище вказаного типу від власника.
     *
     * @param holder Власник сховища
     * @param storageKey Ключ сховища
     * @return Сховище вказаного типу або null, якщо сховище не знайдено
     */
    fun <T : Storage> getStorage(holder: StorageHolder, storageKey: StorageKey<T>): T? {
        return holder.getStorage(storageKey as StorageKey<T?>?)
    }

    /**
     * Extension function для синхронізації сховища сутності/гравця
     */
    private fun ServerPlayer.syncStorage() {
        sync(this)
    }

    /**
     * Extension function для синхронізації сховища рівня з гравцем
     */
    private fun Level.syncStorage(player: ServerPlayer) {
        sync(player)
    }

    /**
     * Внутрішня реалізація реєстру сховищ для певного типу власника.
     *
     * @param T Тип власника сховища
     */
    private class StorageRegistryImpl<T : StorageHolder> : StorageEvents.StorageRegistry<T> {
        /** Мапа, що зберігає зареєстровані фабрики сховищ з предикатами перевірки */
        val registry: MutableMap<ResourceLocation, Pair<Predicate<T>, StorageEvents.StorageFactory<T, *>>> =
            mutableMapOf()

        /**
         * Реєструє новий тип сховища для власників даного типу.
         *
         * @param id Ідентифікатор сховища
         * @param storageClass Клас сховища
         * @param attachCheck Предикат, що перевіряє, чи потрібно приєднувати сховище
         * @param factory Фабрика для створення екземплярів сховища
         * @return Ключ для доступу до зареєстрованого сховища
         */
        override fun <S : Storage> register(
            id: ResourceLocation,
            storageClass: Class<S>,
            attachCheck: Predicate<T>,
            factory: StorageEvents.StorageFactory<T, S>
        ): StorageKey<S> {
            registry[id] = Pair(attachCheck, factory)
            return StorageKey(id, storageClass)
        }

        /**
         * Приєднує всі необхідні сховища до цільового власника.
         *
         * @param target Власник, до якого приєднуються сховища
         */
        fun attach(target: T) {
            registry.forEach { (id, checkAndFactory) ->
                val (predicate, factory) = checkAndFactory
                if (predicate.test(target)) {
                    val storage = factory.create(target)
                    target.attachStorage(id, storage)
                }
            }
        }
    }
}