package io.github.solusmods.eternalcore.storage.api

import io.github.solusmods.eternalcore.storage.impl.CombinedStorage
import io.github.solusmods.eternalcore.storage.impl.StorageManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.util.*

/**
 * Інтерфейс для об'єктів, що можуть зберігати та управляти даними сховищ EternalCore.
 */
interface StorageHolder {

    /**
     * Отримує дані сховища у форматі NBT.
     */
    fun getStorageData(): CompoundTag {
        throw AssertionError()
    }

    /**
     * Отримує конкретне сховище за його ключем.
     */
    fun <T : Storage?> getStorage(storageKey: StorageKey<T?>?): T? {
        throw AssertionError()
    }

    /**
     * Отримує конкретне сховище як Optional за його ключем.
     */
    fun <T : Storage?> getStorageOptional(storageKey: StorageKey<T?>?): Optional<T?> {
        return Optional.ofNullable(getStorage(storageKey)) as Optional<T?>
    }

    /**
     * Синхронізує стан сховища з усіма гравцями, що відстежують цей об'єкт.
     *
     * @param update Якщо true, надсилає лише зміни; якщо false — повний стан
     */
    fun sync(update: Boolean = false) {
        StorageManager.syncTracking(this, update)
    }

    /**
     * Синхронізує повний стан сховища з конкретним гравцем.
     */
    fun sync(target: ServerPlayer) {
        StorageManager.syncTarget(this, target)
    }

    /**
     * Приєднує сховище до цього власника за вказаним ідентифікатором.
     */
    fun attachStorage(id: ResourceLocation, storage: Storage) {
        throw AssertionError()
    }

    /**
     * Отримує тип цього власника сховища.
     */
    fun getStorageType(): StorageType {
        throw AssertionError()
    }

    /**
     * Отримує комбіноване сховище, пов'язане з цим власником.
     */
    fun getCombinedStorage(): CombinedStorage {
        throw AssertionError()
    }

    /**
     * Встановлює комбіноване сховище для цього власника.
     */
    fun setCombinedStorage(storage: CombinedStorage) {
        throw AssertionError()
    }

    /**
     * Отримує ітератор гравців, які відстежують цей об'єкт.
     */
    fun getTrackingPlayers(): Iterable<ServerPlayer?>? {
        throw AssertionError()
    }

    companion object {

    }
}
