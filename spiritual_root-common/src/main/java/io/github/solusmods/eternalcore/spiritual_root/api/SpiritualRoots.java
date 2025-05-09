package io.github.solusmods.eternalcore.spiritual_root.api;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

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
     * Отримує колекцію всіх духовних коренів, якими володіє сутність.
     *
     * @return Колекція екземплярів духовних коренів
     */
    Collection<SpiritualRootInstance> getSpiritualRoots();

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
     * Генерує випадкову кількість духовних коренів для сутності.
     * <p>
     * Розподіл ймовірностей:
     * <ul>
     *   <li>1 корінь - 10% (найрідкісніший, найсильніший)</li>
     *   <li>2 корені - 20%</li>
     *   <li>3 корені - 40% (найпоширеніший)</li>
     *   <li>4 корені - 20%</li>
     *   <li>5 коренів - 10% (найслабший, але найбільш гнучкий)</li>
     * </ul>
     * </p>
     *
     * @return Випадкова кількість духовних коренів
     */
    default int getRandomRootCount() {
        // Зважений випадковий вибір:
        // 1 корінь - 10% (найрідкісніший, найсильніший)
        // 2 корені - 20%
        // 3 корені - 40% (найпоширеніший)
        // 4 корені - 20%
        // 5 коренів - 10% (найслабший, але найбільш гнучкий)

        double random = Math.random();
        if (random < 0.1) return 1;
        if (random < 0.3) return 2;
        if (random < 0.7) return 3;
        if (random < 0.9) return 4;
        return 5;
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
     * Отримує ефективність культивації для конкретного духовного кореня.
     * <p>
     * Ефективність культивації визначає, наскільки добре сутність може використовувати
     * даний духовний корінь для культивації енергії.
     * </p>
     *
     * @param instance Екземпляр духовного кореня
     * @return Коефіцієнт ефективності культивації
     */
    float getCultivationEfficiency(SpiritualRootInstance instance);

    /**
     * Отримує загальний множник швидкості культивації для сутності.
     * <p>
     * Цей множник впливає на швидкість, з якою сутність може культивувати енергію,
     * незалежно від конкретного духовного кореня.
     * </p>
     *
     * @return Множник швидкості культивації
     */
    float getCultivationSpeedMultiplier();

    /**
     * Генерує випадковий набір духовних коренів для сутності з доступного списку.
     * <p>
     * Кількість генерованих коренів визначається методом {@link #getRandomRootCount()}.
     * </p>
     *
     * @param roots Список доступних духовних коренів для вибору
     */
    void generateRandomRoots(List<SpiritualRoot> roots);

    /**
     * Отримує домінуючий духовний корінь сутності.
     * <p>
     * Домінуючий корінь має найбільший вплив на здібності та культивацію сутності.
     * </p>
     *
     * @return Домінуючий духовний корінь або null, якщо сутність не має духовних коренів
     */
    @Nullable SpiritualRootInstance getDominantRoot();

    /**
     * Позначає стан духовних коренів як змінений.
     * <p>
     * Це використовується для синхронізації даних між сервером і клієнтом.
     * </p>
     */
    void markDirty();

    void sync();
}