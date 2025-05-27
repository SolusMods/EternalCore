package io.github.solusmods.eternalcore.spiritual_root.api;

import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootStorage;
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
 * Цей інтерфейс надає методи для додавання, отримання та генерації духовних коренів.
 * </p>
 */
public interface SpiritualRoots {
    /**
     * Позначає стан духовних коренів як змінений.
     * <p>
     * Це використовується для синхронізації даних між сервером і клієнтом.
     * </p>
     */
    void markDirty();

    void sync();

    Collection<SpiritualRootInstance> getGainedRoots();

    /**
     * Отримує колекцію всіх духовних коренів, якими володіє сутність.
     *
     * @return Колекція екземплярів духовних коренів
     */
    Map<ResourceLocation, SpiritualRootInstance> getSpiritualRoots();

    /**
     * Додає духовний корінь до сутності за ідентифікатором.
     *
     * @param realmId Ідентифікатор духовного кореня
     * @param notify Чи слід сповіщати про додавання
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */
    default boolean addSpiritualRoot(@NotNull ResourceLocation realmId, boolean notify) {
        return addSpiritualRoot(realmId, notify, null);
    }

    /**
     * Додає духовний корінь до сутності за ідентифікатором з можливістю вказати повідомлення.
     *
     * @param realmId Ідентифікатор духовного кореня
     * @param notify Чи слід сповіщати про додавання
     * @param component Компонент повідомлення для відображення (може бути null)
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */
    default boolean addSpiritualRoot(@NotNull ResourceLocation realmId, boolean notify, @Nullable MutableComponent component) {
        SpiritualRoot spiritualRoot = SpiritualRootAPI.getSpiritualRootRegistry().get(realmId);
        if (spiritualRoot == null) return false;
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(), false, notify, component);
    }

    /**
     * Додає духовний корінь до сутності за об'єктом духовного кореня.
     *
     * @param spiritualRoot Об'єкт духовного кореня
     * @param notify Чи слід сповіщати про додавання
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */
    default boolean addSpiritualRoot(@NonNull SpiritualRoot spiritualRoot, boolean notify) {
        return addSpiritualRoot(spiritualRoot, notify, null);
    }

    /**
     * Додає духовний корінь до сутності за об'єктом духовного кореня з можливістю вказати повідомлення.
     *
     * @param spiritualRoot Об'єкт духовного кореня
     * @param notify Чи слід сповіщати про додавання
     * @param component Компонент повідомлення для відображення (може бути null)
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */
    default boolean addSpiritualRoot(@NonNull SpiritualRoot spiritualRoot, boolean notify, @Nullable MutableComponent component) {
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(), false, notify, component);
    }

    /**
     * Додає екземпляр духовного кореня до сутності.
     *
     * @param instance Екземпляр духовного кореня
     * @param advance Чи є це просуванням (advancement)
     * @param notify Чи слід сповіщати про додавання
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */
    default boolean addSpiritualRoot(SpiritualRootInstance instance, boolean advance, boolean notify) {
        return addSpiritualRoot(instance, advance, notify, null);
    }

    /**
     * Додає екземпляр духовного кореня до сутності з повними параметрами.
     * <p>
     * Це базовий метод, який викликається всіма іншими перевантаженими методами addSpiritualRoot.
     * </p>
     *
     * @param instance Екземпляр духовного кореня
     * @param advance Чи є це просуванням (advancement)
     * @param notify Чи слід сповіщати про додавання
     * @param component Компонент повідомлення для відображення (може бути null)
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */
    boolean addSpiritualRoot(SpiritualRootInstance instance, boolean advance, boolean notify, @Nullable MutableComponent component);


    /**
     * Updates a element instance and optionally synchronizes the change across the network.
     * <p>
     * @param updatedInstance The instance to update
     * @param sync If true, synchronizes the change to all clients/server
     */
    void updateRoot(SpiritualRootInstance updatedInstance, boolean sync);

    void forEachRoot(BiConsumer<SpiritualRootStorage, SpiritualRootInstance> biConsumer);

    void forgetRoot(@NotNull ResourceLocation skillId, @Nullable MutableComponent component);

    default void forgetRoot(@NotNull ResourceLocation skillId) {
        forgetRoot(skillId, null);
    }

    default void forgetRoot(@NonNull SpiritualRoot spiritualRoot, @Nullable MutableComponent component) {
        forgetRoot(spiritualRoot.getRegistryName(), component);
    }

    default void forgetRoot(@NonNull SpiritualRoot spiritualRoot) {
        forgetRoot(spiritualRoot.getRegistryName());
    }

    default void forgetRoot(@NonNull SpiritualRootInstance spiritualRootInstance, @Nullable MutableComponent component) {
        forgetRoot(spiritualRootInstance.getSpiritualRootId(), component);
    }

    default void forgetRoot(@NonNull SpiritualRootInstance spiritualRootInstance) {
        forgetRoot(spiritualRootInstance.getSpiritualRootId());
    }

    default boolean advanceSpiritualRoot(@NotNull ResourceLocation spiritualRootId) {
        return advanceSpiritualRoot(spiritualRootId, null);
    }

    default boolean advanceSpiritualRoot(@NotNull ResourceLocation spiritualRootId, @Nullable MutableComponent component) {
        SpiritualRoot spiritualRoot = SpiritualRootAPI.getSpiritualRootRegistry().get(spiritualRootId);
        if (spiritualRoot == null) return false;
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(), true, false);
    }

    default boolean advanceSpiritualRoot(@NonNull SpiritualRoot spiritualRoot) {
        return this.advanceSpiritualRoot(spiritualRoot, null);
    }

    default boolean advanceSpiritualRoot(@NonNull SpiritualRoot spiritualRoot, @Nullable MutableComponent component) {
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(), true, false, component);
    }

    default boolean advanceSpiritualRoot(SpiritualRootInstance advance) {
        return advanceSpiritualRoot(advance, null);
    }

    default boolean advanceSpiritualRoot(SpiritualRootInstance advance, @Nullable MutableComponent component) {
        return addSpiritualRoot(advance, true, false, component);
    }

}