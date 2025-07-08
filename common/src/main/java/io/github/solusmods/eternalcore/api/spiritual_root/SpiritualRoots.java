package io.github.solusmods.eternalcore.api.spiritual_root;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Інтерфейс для управління духовними коренями (spiritual roots) сутності.
 * <p>
 * Духовні корені є основним елементом системи культивації в Eternal Core.
 * Вони визначають здатність сутності до культивації та спеціальні здібності.
 * Цей інтерфейс надає методи для додавання, отримання, просування та видалення духовних коренів.
 * </p>
 */
public interface SpiritualRoots {

    /**
     * Позначає стан духовних коренів як змінений (для синхронізації даних).
     */
    void markDirty();

    /**
     * Повертає всі духовні корені, якими володіє сутність.
     *
     * @return Мапа духовних коренів за їх ідентифікаторами
     */
    Map<ResourceLocation, AbstractSpiritualRoot> getSpiritualRoots();

    /**
     * Повертає всі духовні корені, які були вже отримані сутністю.
     *
     * @return Колекція духовних коренів
     */
    Collection<AbstractSpiritualRoot> getGainedRoots();

    /**
     * Додає духовний корінь за ідентифікатором.
     *
     * @param rootId Ідентифікатор духовного кореня
     * @param notify Чи слід показувати повідомлення
     * @return true, якщо корінь успішно додано
     */
    default boolean addSpiritualRoot(@NotNull ResourceLocation rootId, boolean notify) {
        return addSpiritualRoot(rootId, notify, null);
    }

    /**
     * Додає духовний корінь за ідентифікатором із кастомним повідомленням.
     *
     * @param rootId  Ідентифікатор духовного кореня
     * @param notify  Чи слід показувати повідомлення
     * @param message Кастомне повідомлення (або null)
     * @return true, якщо корінь успішно додано
     */
    default boolean addSpiritualRoot(@NotNull ResourceLocation rootId, boolean notify, @Nullable MutableComponent message) {
        AbstractSpiritualRoot root = SpiritualRootAPI.getSpiritualRootRegistry().get(rootId);
        if (root == null) return false;
        return addSpiritualRoot(root, false, notify, message);
    }

    /**
     * Додає духовний корінь.
     *
     * @param root   Об'єкт духовного кореня
     * @param notify Чи слід показувати повідомлення
     * @return true, якщо корінь успішно додано
     */
    default boolean addSpiritualRoot(@NonNull AbstractSpiritualRoot root, boolean notify) {
        return addSpiritualRoot(root, notify, null);
    }

    /**
     * Додає духовний корінь із можливістю вказати повідомлення.
     *
     * @param root    Об'єкт духовного кореня
     * @param notify  Чи слід показувати повідомлення
     * @param message Кастомне повідомлення (або null)
     * @return true, якщо корінь успішно додано
     */
    default boolean addSpiritualRoot(@NonNull AbstractSpiritualRoot root, boolean notify, @Nullable MutableComponent message) {
        return addSpiritualRoot(root, false, notify, message);
    }

    /**
     * Базовий метод додавання духовного кореня.
     *
     * @param root        Об'єкт духовного кореня
     * @param advancement Чи є це просуванням (advancement)
     * @param notify      Чи слід показувати повідомлення
     * @param message     Кастомне повідомлення (або null)
     * @return true, якщо корінь успішно додано
     */
    boolean addSpiritualRoot(@NonNull AbstractSpiritualRoot root, boolean advancement, boolean notify, @Nullable MutableComponent message);

    /**
     * Викликає споживача для кожного духовного кореня.
     *
     * @param consumer Функція, яка отримує ідентифікатор та сам корінь
     */
    void forEachRoot(BiConsumer<ResourceLocation, AbstractSpiritualRoot> consumer);

    /**
     * Видаляє духовний корінь за ідентифікатором.
     *
     * @param rootId  Ідентифікатор духовного кореня
     * @param message Кастомне повідомлення (або null)
     */
    void forgetRoot(@NotNull ResourceLocation rootId, @Nullable MutableComponent message);

    default void forgetRoot(@NonNull ResourceLocation rootId) {
        forgetRoot(rootId, null);
    }

    default void forgetRoot(@NonNull AbstractSpiritualRoot root, @Nullable MutableComponent message) {
        forgetRoot(root.getResource(), message);
    }

    default void forgetRoot(@NonNull AbstractSpiritualRoot root) {
        forgetRoot(root.getResource(), null);
    }

    /**
     * Просуває духовний корінь (символізує прорив чи підвищення рівня).
     *
     * @param rootId  Ідентифікатор духовного кореня
     * @param message Повідомлення (може бути null)
     * @return true, якщо просування відбулося
     */
    default boolean advanceSpiritualRoot(@NotNull ResourceLocation rootId, @Nullable MutableComponent message) {
        AbstractSpiritualRoot root = SpiritualRootAPI.getSpiritualRootRegistry().get(rootId);
        if (root == null) return false;
        return addSpiritualRoot(root, true, false, message);
    }

    default boolean advanceSpiritualRoot(@NonNull AbstractSpiritualRoot root, @Nullable MutableComponent message) {
        return addSpiritualRoot(root, true, false, message);
    }

    default boolean advanceSpiritualRoot(@NonNull AbstractSpiritualRoot root) {
        return advanceSpiritualRoot(root, null);
    }

    default boolean updateSpiritualRoot(@NonNull AbstractSpiritualRoot root, boolean notify) {
        return updateSpiritualRoot(root, false, notify, null);
    }

    default boolean updateSpiritualRoot(@NonNull ResourceLocation rootId, boolean notify,  @Nullable MutableComponent message) {
        AbstractSpiritualRoot root = SpiritualRootAPI.getSpiritualRootRegistry().get(rootId);
        if (root == null) return false;
        return updateSpiritualRoot(root, false, notify, message);
    }

    boolean updateSpiritualRoot(@NonNull AbstractSpiritualRoot root, boolean advancement, boolean notify, @Nullable MutableComponent message);
}
