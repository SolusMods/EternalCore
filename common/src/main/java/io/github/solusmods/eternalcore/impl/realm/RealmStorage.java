package io.github.solusmods.eternalcore.impl.realm;

import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.realm.IReachedRealms;
import io.github.solusmods.eternalcore.api.realm.RealEvents;
import io.github.solusmods.eternalcore.api.realm.Realms;
import io.github.solusmods.eternalcore.api.storage.AbstractStorage;
import io.github.solusmods.eternalcore.api.storage.StorageEvents;
import io.github.solusmods.eternalcore.api.storage.StorageHolder;
import io.github.solusmods.eternalcore.api.storage.StorageKey;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Сховище, що керує світами (realms) для гравців.
 * <p>
 * Цей клас реалізує інтерфейси {@link Realms} та {@link IReachedRealms}, дозволяючи
 * зберігати, керувати та синхронізувати інформацію про досягнуті світи гравця
 * та його поточний активний світ. Сховище зберігає дані у NBT форматі та може
 * бути серіалізовано для збереження або мережевої синхронізації.
 * </p>
 * <p>
 * Клас також обробляє події зміни та досягнення світів через систему подій EternalCore.
 * </p>
 */
public class RealmStorage extends AbstractStorage implements Realms, IReachedRealms {

    // region Constants
    /**
     * Унікальний ідентифікатор цього типу сховища
     */
    public static final ResourceLocation ID = EternalCore.create("realm_storage");
    /**
     * Ключ для поточного активного світу в NBT
     */
    private static final String REALM_KEY = "realm_key";
    /**
     * Ключ для колекції досягнутих світів у NBT
     */
    private static final String REACHED_REALMS_KEY = "reached_realms_key";
    // endregion

    // region Static Fields
    /**
     * Ключ для доступу до цього сховища
     */
    @Getter
    private static StorageKey<RealmStorage> key = null;
    // endregion

    // region Instance Fields
    /**
     * Колекція шляхів культивації, які досягнув гравець
     */
    @Getter
    private final Collection<AbstractRealm> reachedRealms = new ArrayDeque<>();

    /**
     * Набір, що відслідковує синхронізовані ідентифікатори шляхів для визначення видалень.
     */
    private final Set<ResourceLocation> lastSyncedRealmIds = new HashSet<>();

    /**
     * Поточний активний шлях культивації гравця
     */
    private AbstractRealm realm = null;
    // endregion

    // region Constructor

    /**
     * Створює нове сховище шляхів культивації для вказаного власника.
     *
     * @param holder Власник цього сховища
     */
    protected RealmStorage(StorageHolder holder) {
        super(holder);
    }
    // endregion

    // region Static Initialization

    /**
     * Ініціалізує систему сховища шляхів культивації, реєструючи його в системі сховищ EternalCore.
     * <p>
     * Цей метод повинен бути викликаний один раз під час ініціалізації мода.
     * </p>
     */
    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        RealmStorage.class,
                        Entity.class::isInstance,
                        RealmStorage::new));
    }
    // endregion

    // region Getters

    /**
     * Отримує поточний активний шлях культивації гравця.
     *
     * @return Optional, що містить поточний шлях культивації, або пустий Optional, якщо шлях культивації не встановлено
     */
    @Override
    public Optional<AbstractRealm> getRealm() {
        return Optional.ofNullable(realm);
    }

    /**
     * Отримує власника сховища як живу сутність.
     *
     * @return Власник сховища як LivingEntity
     */
    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }
    // endregion

    // region Realm Management

    /**
     * Додає новий досягнутий шлях культивації до колекції гравця.
     * <p>
     * Цей метод викликає подію {@link RealEvents#REACH_REALM}, яка може бути скасована
     * обробниками подій. Якщо шлях успішно пройдено, викликається {@link AbstractRealm#onReach(LivingEntity)}
     * для ініціалізації ефектів шляху культивації.
     * </p>
     *
     * @param abstractRealm Екземпляр шляху культивації для додавання
     * @param breakthrough  Чи є це проривом (breakthrough)
     * @param notifyPlayer  Чи повідомляти гравця про досягнення
     * @param component     Компонент повідомлення (може бути null)
     * @return true, якщо шлях культивації було успішно додано, false в іншому випадку
     */
    @Override
    public boolean addRealm(AbstractRealm abstractRealm, boolean breakthrough, boolean notifyPlayer, @Nullable MutableComponent component) {
        // Prepare event parameters
        Changeable<MutableComponent> realmMessage = Changeable.of(component);
        Changeable<Boolean> notify = Changeable.of(notifyPlayer);

        // Fire event and check if cancelled
        EventResult result = RealEvents.REACH_REALM.invoker().reach(abstractRealm, getOwner(), breakthrough, notify, realmMessage);
        if (result.isFalse()) {
            return false;
        }

        // Process realm addition
        LivingEntity owner = getOwner();
        if (realmMessage.isPresent()) {
            owner.sendSystemMessage(realmMessage.get());
        }

        // Update realm state
        abstractRealm.onReach(owner);
        reachedRealms.add(abstractRealm);
        markDirty();

        return true;
    }

    /**
     * Встановлює активний шлях для культивації гравця.
     * <p>
     * Цей метод викликає подію {@link RealEvents#SET_REALM}, яка може бути скасована
     * обробниками подій. Якщо шлях культивації змінюється, попередній шлях культивації видаляє свої
     * модифікатори атрибутів, а новий шлях культивації ініціалізується через {@link io.github.solusmods.eternalcore.api.realm.AbstractRealm#onSet(LivingEntity)}.
     * </p>
     *
     * @param realm        Екземпляр шляху культивації для встановлення
     * @param breakthrough Чи є це проривом (breakthrough)
     * @param notifyPlayer Чи повідомляти гравця про зміну
     * @param component    Компонент повідомлення (може бути null)
     * @return true, якщо шлях культивації було успішно встановлено, false в іншому випадку
     */
    @Override
    public boolean setRealm(AbstractRealm realm, boolean breakthrough, boolean notifyPlayer, @Nullable MutableComponent component) {
        AbstractRealm previousRealm = this.realm;

        // Prepare event parameters
        Changeable<MutableComponent> realmMessage = Changeable.of(component);
        Changeable<Boolean> notify = Changeable.of(notifyPlayer);

        // Fire event and check if cancelled
        EventResult result = RealEvents.SET_REALM.invoker().set(previousRealm, getOwner(), realm, breakthrough, notify, realmMessage);
        if (result.isFalse()) {
            return false;
        }

        // Process realm change
        LivingEntity owner = getOwner();

        // Remove previous realm's effects
        if (previousRealm != null && previousRealm != realm) {
            previousRealm.removeAttributeModifiers(owner);
        }

        // Send notification if needed
        if (realmMessage.isPresent()) {
            owner.sendSystemMessage(realmMessage.get());
        }

        // Update realm state
        realm.onSet(owner);
        this.realm = realm;
        markDirty();

        return true;
    }
    // endregion

    // region Serialization

    /**
     * Зберігає стан сховища в NBT формат.
     *
     * @param data Тег, в який буде збережено дані
     */
    @Override
    public void save(CompoundTag data) {
        // Save current realm
        if (realm != null) {
            data.put(REALM_KEY, realm.toNBT());
        }

        // Save reached realms
        ListTag reachedRealmsTag = new ListTag();
        reachedRealms.forEach(abstractRealm -> reachedRealmsTag.add(abstractRealm.toNBT()));
        data.put(REACHED_REALMS_KEY, reachedRealmsTag);
    }

    /**
     * Завантажує стан сховища з NBT формату.
     *
     * @param data Тег, з якого будуть завантажені дані
     */
    @Override
    public void load(CompoundTag data) {
        this.reachedRealms.clear();
        this.realm = null;

        // Load current realm
        if (data.contains(REALM_KEY, Tag.TAG_COMPOUND)) {
            CompoundTag realmTag = data.getCompound(REALM_KEY);
            AbstractRealm loadedRealm = AbstractRealm.fromNBT(realmTag);
            if (loadedRealm != null) {
                this.realm = loadedRealm;
            } else {
                logMissingRealmRegistration(realmTag, "current realm");
            }
        }

        // Load reached realms
        ListTag reachedRealmsTag = data.getList(REACHED_REALMS_KEY, Tag.TAG_COMPOUND);
        for (int index = 0; index < reachedRealmsTag.size(); index++) {
            Tag tag = reachedRealmsTag.get(index);
            if (!(tag instanceof CompoundTag realmTag)) {
                EternalCore.LOG.error("Failed to load reached realm entry at index {}: expected CompoundTag but found {}", index,
                        tag.getClass().getSimpleName());
                continue;
            }

            try {
                AbstractRealm abstractRealm = AbstractRealm.fromNBT(realmTag);
                if (abstractRealm != null) {
                    this.reachedRealms.add(abstractRealm);
                } else {
                    logMissingRealmRegistration(realmTag, "reached realm entry");
                }
            } catch (Exception e) {
                EternalCore.LOG.error("Failed to load realm instance from NBT", e);
            }
        }

        this.lastSyncedRealmIds.clear();
        for (AbstractRealm abstractRealm : this.reachedRealms) {
            this.lastSyncedRealmIds.add(abstractRealm.getResource());
        }
    }
    // endregion


    @Override
    public void saveOutdated(CompoundTag data) {
        Set<ResourceLocation> currentRealmIds = new HashSet<>();
        for (AbstractRealm abstractRealm : this.reachedRealms) {
            currentRealmIds.add(abstractRealm.getResource());
        }

        boolean shouldResetExistingData = false;
        if (!lastSyncedRealmIds.isEmpty()) {
            for (ResourceLocation previousId : lastSyncedRealmIds) {
                if (!currentRealmIds.contains(previousId)) {
                    shouldResetExistingData = true;
                    break;
                }
            }
        }

        if (shouldResetExistingData) {
            data.putBoolean("resetExistingData", true);
        }

        super.saveOutdated(data);

        this.lastSyncedRealmIds.clear();
        this.lastSyncedRealmIds.addAll(currentRealmIds);
    }


    @Override
    public String toString() {
        return String.format("%s{currentRealm={%s}, reachedRealmsCount={%s}}", getClass().getSimpleName(), getRealm().toString(), getReachedRealms().size());
    }

    private void logMissingRealmRegistration(CompoundTag tag, String context) {
        if (tag.contains(AbstractRealm.REALM_ID_KEY)) {
            EternalCore.LOG.error("Failed to load {}: realm '{}' is not registered", context, tag.getString(AbstractRealm.REALM_ID_KEY));
        } else {
            EternalCore.LOG.error("Failed to load {}: missing realm id in tag {}", context, tag);
        }
    }
}
