package io.github.solusmods.eternalcore.api.storage;

import io.github.solusmods.eternalcore.impl.storage.CombinedStorage;
import io.github.solusmods.eternalcore.impl.storage.StorageManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Інтерфейс для об'єктів, що можуть зберігати та управляти даними сховищ EternalCore.
 * <p>
 * Цей інтерфейс визначає методи для взаємодії з системою сховищ, включаючи
 * доступ до даних сховища, синхронізацію між клієнтом та сервером, і управління
 * комбінованими сховищами. Він є основним компонентом для інтеграції об'єктів гри
 * з системою зберігання даних EternalCore.
 * </p>
 * <p>
 * Реалізації цього інтерфейсу зазвичай надаються через Mixin для стандартних
 * об'єктів Minecraft, таких як Entity, LevelChunk та Level.
 * </p>
 */
public interface StorageHolder {

    /**
     * Отримує дані сховища у форматі NBT.
     * <p>
     * Цей метод повертає комбіноване представлення NBT всіх сховищ,
     * приєднаних до цього власника.
     * </p>
     *
     * @return CompoundTag, що містить всі дані сховища
     * @throws AssertionError якщо метод не реалізовано
     */
    @NotNull
    default CompoundTag eternalCore$getStorage() {
        throw new AssertionError();
    }

    /**
     * Отримує конкретне сховище за його ключем.
     * <p>
     * Використовуйте цей метод, щоб отримати доступ до конкретного типу сховища,
     * приєднаного до цього власника.
     * </p>
     *
     * @param <T>        Тип сховища, що запитується
     * @param storageKey Ключ для доступу до сховища
     * @return Сховище вказаного типу або null, якщо сховище не знайдено
     * @throws AssertionError якщо метод не реалізовано
     */
    @Nullable
    default <T extends AbstractStorage> T eternalCore$getStorage(StorageKey<T> storageKey) {
        throw new AssertionError();
    }

    /**
     * Отримує конкретне сховище як Optional за його ключем.
     * <p>
     * Цей метод надає безпечний спосіб доступу до сховища, повертаючи
     * порожній Optional, якщо сховище не існує.
     * </p>
     *
     * @param <T>        Тип сховища, що запитується
     * @param storageKey Ключ для доступу до сховища
     * @return Optional, що містить сховище вказаного типу, або порожній Optional
     */
    @NotNull
    default <T extends AbstractStorage> Optional<T> eternalCore$getStorageOptional(StorageKey<T> storageKey) {
        return Optional.ofNullable(this.eternalCore$getStorage(storageKey));
    }

    /**
     * Синхронізує стан сховища з усіма гравцями, що відстежують цей об'єкт.
     * <p>
     * Використовуйте цей метод, щоб оновити стан сховища на клієнтах гравців,
     * які відстежують цей об'єкт.
     * </p>
     *
     * @param update Якщо true, надсилає лише зміни; якщо false - повний стан сховища
     */
    default void eternalCore$sync(boolean update) {
        StorageManager.syncTracking(this, update);
    }

    /**
     * Синхронізує повний стан сховища з усіма гравцями, що відстежують цей об'єкт.
     * <p>
     * Це зручний метод, що викликає {@link #eternalCore$sync(boolean)} з параметром false.
     * </p>
     */
    default void eternalCore$sync() {
        this.eternalCore$sync(false);
    }

    /**
     * Синхронізує повний стан сховища з конкретним гравцем.
     * <p>
     * Використовуйте цей метод для надсилання стану сховища одному гравцю,
     * наприклад, при першому приєднанні до сервера або зміні виміру.
     * </p>
     *
     * @param target Цільовий гравець для синхронізації
     */
    default void eternalCore$sync(@NotNull ServerPlayer target) {
        StorageManager.syncTarget(this, target);
    }

    /**
     * Приєднує сховище до цього власника за вказаним ідентифікатором.
     * <p>
     * Цей метод зазвичай викликається під час ініціалізації об'єкта
     * або при динамічному створенні нових сховищ.
     * </p>
     *
     * @param id      Ідентифікатор сховища
     * @param storage Сховище для приєднання
     * @throws AssertionError якщо метод не реалізовано
     */
    default void eternalCore$attachStorage(@NotNull ResourceLocation id, @NotNull AbstractStorage storage) {
        throw new AssertionError();
    }

    /**
     * Отримує тип цього власника сховища.
     * <p>
     * Тип власника визначає, як обробляється та синхронізується сховище,
     * і які реєстри сховищ застосовуються.
     * </p>
     *
     * @return Тип власника сховища (ENTITY, CHUNK або WORLD)
     * @throws AssertionError якщо метод не реалізовано
     */
    @NotNull
    default StorageType eternalCore$getStorageType() {
        throw new AssertionError();
    }

    /**
     * Отримує комбіноване сховище, пов'язане з цим власником.
     * <p>
     * Комбіноване сховище містить усі окремі сховища, приєднані до цього власника,
     * і забезпечує централізоване управління їхніми даними.
     * </p>
     *
     * @return Комбіноване сховище цього власника
     * @throws AssertionError якщо метод не реалізовано
     */
    @NotNull
    default CombinedStorage eternalCore$getCombinedStorage() {
        throw new AssertionError();
    }

    /**
     * Встановлює комбіноване сховище для цього власника.
     * <p>
     * Цей метод зазвичай викликається під час ініціалізації об'єкта
     * або при клонуванні сховищ між об'єктами (наприклад, при відродженні гравця).
     * </p>
     *
     * @param storage Комбіноване сховище для встановлення
     * @throws AssertionError якщо метод не реалізовано
     */
    default void eternalCore$setCombinedStorage(@NotNull CombinedStorage storage) {
        throw new AssertionError();
    }

    /**
     * Отримує ітератор гравців, які відстежують цей об'єкт.
     * <p>
     * Використовується для синхронізації даних сховища з відповідними клієнтами.
     * </p>
     *
     * @return Ітерабельна колекція серверних гравців, що відстежують цей об'єкт
     * @throws AssertionError якщо метод не реалізовано
     */
    default Iterable<ServerPlayer> eternalCore$getTrackingPlayers() {
        throw new AssertionError();
    }
}
