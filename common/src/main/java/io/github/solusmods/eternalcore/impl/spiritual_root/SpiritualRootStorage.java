package io.github.solusmods.eternalcore.impl.spiritual_root;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.spiritual_root.SpiritualRootEvents;
import io.github.solusmods.eternalcore.api.spiritual_root.SpiritualRoots;
import io.github.solusmods.eternalcore.api.storage.AbstractStorage;
import io.github.solusmods.eternalcore.api.storage.StorageEvents;
import io.github.solusmods.eternalcore.api.storage.StorageHolder;
import io.github.solusmods.eternalcore.api.storage.StorageKey;
import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Сховище, що керує духовними коренями сутності.
 * <p>
 * {@link SpiritualRootStorage} реєструється через {@link #init()} та гарантує, що всі зміни
 * синхронізуються з клієнтом викликом {@link #markDirty()}. Події {@link SpiritualRootEvents}
 * дозволяють відміняти, модифікувати або супроводжувати додавання та видалення духовних коренів.
 * Під час часткових оновлень використовується прапорець <code>resetExistingData</code>, щоб
 * клієнт скинув кешовані корені, коли їх було видалено на сервері.
 * </p>
 */
public class SpiritualRootStorage extends AbstractStorage implements SpiritualRoots {

    /**
     * Унікальний ідентифікатор цього типу сховища.
     */
    public static final ResourceLocation ID = EternalCore.create("spiritual_root_storage");
    /**
     * Ключ для серіалізації набору духовних коренів у NBT.
     */
    private static final String SPIRITUAL_ROOTS_KEY = "spiritual_roots_key";
    /**
     * Ключ доступу до цього сховища у реєстрі.
     */
    private static StorageKey<SpiritualRootStorage> key = null;

    /**
     * Отримані духовні корені, відображені за ідентифікатором ресурсу.
     */
    private final Map<ResourceLocation, AbstractSpiritualRoot> spiritualRoots = new HashMap<>();

    private boolean hasRemovedRoots = false;

    /**
     * Створює нове сховище духовних коренів для вказаного власника.
     *
     * @param holder Власник цього сховища
     */
    protected SpiritualRootStorage(StorageHolder holder) {
        super(holder);
    }

    /**
     * Реєструє тип сховища у системі EternalCore.
     * <p>
     * Викликається під час ініціалізації мода та визначає, для яких сутностей буде створено сховище.
     * </p>
     */
    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID, SpiritualRootStorage.class, Entity.class::isInstance, SpiritualRootStorage::new));
    }

    /**
     * Повертає ключ доступу до сховища духовних коренів.
     *
     * @return Ключ сховища, або {@code null}, якщо {@link #init()} ще не викликано
     */
    public static StorageKey<SpiritualRootStorage> getKey() {
        return key;
    }

    /**
     * Серіалізує всі наявні духовні корені у NBT.
     *
     * @param data Тег, у який записуються дані
     */
    @Override
    public void save(CompoundTag data) {
        ListTag rootsTag = new ListTag();
        spiritualRoots.values().forEach(root -> rootsTag.add(root.toNBT()));
        data.put(SPIRITUAL_ROOTS_KEY, rootsTag);
    }

    /**
     * Відновлює стан сховища з NBT.
     * <p>
     * Підтримує скидання стану за допомогою прапорця <code>resetExistingData</code> для видалених коренів.
     * </p>
     *
     * @param data Тег, з якого зчитуються дані
     */
    @Override
    public void load(CompoundTag data) {
        if (data.contains("resetExistingData")) {
            this.spiritualRoots.clear();
        }
        for (Tag tag : data.getList(SPIRITUAL_ROOTS_KEY, Tag.TAG_COMPOUND)) {
            try {
                var root = AbstractSpiritualRoot.fromNBT((CompoundTag) tag);
                this.spiritualRoots.put(root.getResource(), root);
            } catch (Exception e) {
                EternalCore.LOG.error("Failed to load spiritual root from NBT", e);
            }
        }
    }

    /**
     * Повертає усі отримані духовні корені.
     *
     * @return Набір духовних коренів
     */
    @Override
    public Collection<AbstractSpiritualRoot> getGainedRoots() {
        return this.spiritualRoots.values();
    }

    /**
     * Серіалізує лише змінені дані для синхронізації з клієнтом.
     * <p>
     * Якщо корінь було видалено, встановлює <code>resetExistingData</code>, щоби клієнт повністю
     * оновив локальний стан.
     * </p>
     *
     * @param data Тег, у який записуються дані
     */
    @Override
    public void saveOutdated(CompoundTag data) {
        if (this.hasRemovedRoots) {
            this.hasRemovedRoots = false;
            data.putBoolean("resetExistingData", true);
            super.saveOutdated(data);
        } else {
            ListTag rootList = new ListTag();
            for (AbstractSpiritualRoot root : this.spiritualRoots.values()) {
                rootList.add(root.toNBT());
            }
            data.put(SPIRITUAL_ROOTS_KEY, rootList);
        }
    }

    /**
     * Отримує власника сховища як живу сутність.
     *
     * @return Власник сховища
     */
    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }

    /**
     * Повертає карту духовних коренів за ідентифікатором ресурсу.
     *
     * @return Відображення ідентифікатора на корінь
     */
    @Override
    public Map<ResourceLocation, AbstractSpiritualRoot> getSpiritualRoots() {
        return spiritualRoots;
    }

    /**
     * Додає новий духовний корінь.
     * <p>
     * Викликає {@link SpiritualRootEvents#ADD} для можливості відміни або модифікації кореня перед
     * додаванням. Успішний виклик відправляє повідомлення гравцю (якщо вказано) та позначає сховище як змінене.
     * </p>
     *
     * @param root        Корінь для додавання
     * @param advancement Чи пов'язано додавання з просуванням
     * @param notify      Чи слід сповіщати гравця
     * @param component   Повідомлення для відображення (може бути {@code null})
     * @return {@code true}, якщо корінь додано
     */
    @Override
    public boolean addSpiritualRoot(@NotNull AbstractSpiritualRoot root, boolean advancement, boolean notify, @Nullable MutableComponent component) {
        if (this.spiritualRoots.containsKey(root.getResource())) {
            EternalCore.LOG.debug("Tried to register duplicate spiritual root: {}", root.getResource());
            return false;
        }

        var rootMessage = Changeable.of(component);
        var notifyPlayer = Changeable.of(notify);

        var result = SpiritualRootEvents.ADD.invoker().add(root, getOwner(), advancement, notifyPlayer, rootMessage);
        if (result.isFalse()) return false;

        var newRoot = result.object();
        this.spiritualRoots.put(newRoot.getResource(), newRoot);

        if (rootMessage.isPresent()) getOwner().sendSystemMessage(rootMessage.get());
        markDirty();
        return true;
    }

    /**
     * Виконує дію для кожного духовного кореня.
     *
     * @param consumer Обробник пари «ідентифікатор - корінь»
     */
    @Override
    public void forEachRoot(BiConsumer<ResourceLocation, AbstractSpiritualRoot> consumer) {
        spiritualRoots.forEach(consumer);
    }

    /**
     * Забуває (видаляє) духовний корінь.
     * <p>
     * Подія {@link SpiritualRootEvents#FORGET_SPIRITUAL_ROOT} може змінити повідомлення або скасувати
     * видалення. Після успіху сховище позначається як змінене та використовує часткову синхронізацію.
     * </p>
     *
     * @param rootId    Ідентифікатор кореня
     * @param component Повідомлення для гравця (може бути {@code null})
     */
    @Override
    public void forgetRoot(@NotNull ResourceLocation rootId, @Nullable MutableComponent component) {
        if (!this.spiritualRoots.containsKey(rootId)) return;

        var root = this.spiritualRoots.get(rootId);
        var forgetMessage = Changeable.of(component);

        var result = SpiritualRootEvents.FORGET_SPIRITUAL_ROOT.invoker().forget(root, getOwner(), forgetMessage);
        if (result.isFalse()) return;

        if (forgetMessage.isPresent()) getOwner().sendSystemMessage(forgetMessage.get());
        this.spiritualRoots.remove(rootId);
        this.hasRemovedRoots = true;
        markDirty();
    }

    /**
     * Оновлює інформацію про існуючий духовний корінь.
     * <p>
     * Подія {@link SpiritualRootEvents#UPDATE} може змінити повідомлення або скасувати дію.
     * При успіху дані оновлюються та синхронізуються з клієнтом.
     * </p>
     *
     * @param root        Оновлений корінь
     * @param advancement Чи пов'язано оновлення з просуванням
     * @param notify      Чи слід сповіщати гравця
     * @param message     Повідомлення для гравця (може бути {@code null})
     * @return {@code true}, якщо корінь оновлено
     */
    @Override
    public boolean updateSpiritualRoot(@NonNull AbstractSpiritualRoot root, boolean advancement, boolean notify, @Nullable MutableComponent message) {
        if (!this.spiritualRoots.containsKey(root.getResource())) return false;

        var rootMessage = Changeable.of(message);
        var notifyPlayer = Changeable.of(notify);

        var result = SpiritualRootEvents.UPDATE.invoker().update(root, getOwner(), rootMessage);
        if (result.isFalse()) return false;
        if (rootMessage.isPresent()) getOwner().sendSystemMessage(rootMessage.get());
        this.spiritualRoots.put(root.getResource(), root);
        markDirty();
        return true;
    }

    /**
     * Повертає зручне для дебагу представлення сховища.
     *
     * @return Рядковий опис стану
     */
    @Override
    public String toString() {
        return String.format("%s{roots=[%s], owner={%s}}", this.getClass().getSimpleName(), this.spiritualRoots.values(), getOwner().toString());
    }
}
