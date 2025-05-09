package io.github.solusmods.eternalcore.realm.impl;

import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.realm.EternalCoreRealm;
import io.github.solusmods.eternalcore.realm.api.*;
import io.github.solusmods.eternalcore.realm.impl.network.InternalRealmPacketActions;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageEvents;
import io.github.solusmods.eternalcore.storage.api.StorageHolder;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

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
public class RealmStorage extends Storage implements Realms, IReachedRealms {
    /** Ключ для поточного активного світу в NBT */
    private static final String REALM_KEY = "realm_key";
    
    /** Ключ для колекції досягнутих світів у NBT */
    private static final String REACHED_REALMS_KEY = "reached_realms_key";
    
    /** Унікальний ідентифікатор цього типу сховища */
    public static final ResourceLocation ID = EternalCoreRealm.create("realm_storage");
    
    /** Ключ для доступу до цього сховища */
    @Getter
    private static StorageKey<RealmStorage> key = null;
    
    /** Колекція світів, які досягнув гравець */
    private final Collection<RealmInstance> reachedRealms = new ArrayList<>();
    
    /** Поточний активний світ гравця */
    private RealmInstance realm = null;

    /**
     * Створює нове сховище світів для вказаного власника.
     *
     * @param holder Власник цього сховища
     */
    protected RealmStorage(StorageHolder holder) {
        super(holder);
    }

    /**
     * Ініціалізує систему сховища світів, реєструючи його в системі сховищ EternalCore.
     * <p>
     * Цей метод повинен бути викликаний один раз під час ініціалізації мода.
     * </p>
     */
    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        RealmStorage.class, Entity.class::isInstance,
                        RealmStorage::new));
    }

    /**
     * Отримує колекцію всіх світів, досягнутих гравцем.
     *
     * @return Колекція екземплярів досягнутих світів
     */
    @Override
    public Collection<RealmInstance> getReachedRealms() {
        return this.reachedRealms;
    }

    /**
     * Отримує поточний активний світ гравця.
     *
     * @return Optional, що містить поточний світ, або пустий Optional, якщо світ не встановлено
     */
    @Override
    public Optional<RealmInstance> getRealm() {
        return Optional.ofNullable(realm);
    }

    /**
     * Додає новий досягнутий світ до колекції гравця.
     * <p>
     * Цей метод викликає подію {@link RealEvents#REACH_REALM}, яка може бути скасована
     * обробниками подій. Якщо світ успішно додано, викликається {@link RealmInstance#onReach(LivingEntity)}
     * для ініціалізації ефектів світу.
     * </p>
     *
     * @param realm Екземпляр світу для додавання
     * @param breakthrough Чи є це проривом (breakthrough)
     * @param notifyPlayer Чи повідомляти гравця про досягнення
     * @param component Компонент повідомлення (може бути null)
     * @return true, якщо світ було успішно додано, false в іншому випадку
     */
    @Override
    public boolean addRealm(RealmInstance realm, boolean breakthrough, boolean notifyPlayer, @Nullable MutableComponent component) {
        Changeable<MutableComponent> realmMessage = Changeable.of(component);
        Changeable<Boolean> notify = Changeable.of(notifyPlayer);
        EventResult result = RealEvents.REACH_REALM.invoker().reach(realm, getOwner(), breakthrough, notify, realmMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();
        if (realmMessage.isPresent()) getOwner().sendSystemMessage(realmMessage.get());
        realm.markDirty();
        realm.onReach(owner);
        reachedRealms.add(realm);
        markDirty();
        return true;
    }

    /**
     * Встановлює активний світ для гравця.
     * <p>
     * Цей метод викликає подію {@link RealEvents#SET_REALM}, яка може бути скасована
     * обробниками подій. Якщо світ змінюється, попередній світ видаляє свої
     * модифікатори атрибутів, а новий світ ініціалізується через {@link RealmInstance#onSet(LivingEntity)}.
     * </p>
     *
     * @param realm Екземпляр світу для встановлення
     * @param breakthrough Чи є це проривом (breakthrough)
     * @param notifyPlayer Чи повідомляти гравця про зміну
     * @param component Компонент повідомлення (може бути null)
     * @return true, якщо світ було успішно встановлено, false в іншому випадку
     */
    @Override
    public boolean setRealm(RealmInstance realm, boolean breakthrough, boolean notifyPlayer, @Nullable MutableComponent component) {
        RealmInstance instance = this.realm;
        Changeable<MutableComponent> realmMessage = Changeable.of(component);
        Changeable<Boolean> notify = Changeable.of(notifyPlayer);
        EventResult result = RealEvents.SET_REALM.invoker().set(instance, getOwner(), realm, breakthrough, notify, realmMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();
        if (instance != null && instance != realm) {
            instance.removeAttributeModifiers(owner);
        }


        if (realmMessage.isPresent()) getOwner().sendSystemMessage(realmMessage.get());
        realm.markDirty();
        realm.onSet(owner);
        this.realm = realm;
        markDirty();
        return true;
    }

    /**
     * Зберігає стан сховища в NBT формат.
     *
     * @param data Тег, в який буде збережено дані
     */
    @Override
    public void save(CompoundTag data) {
        saveInstance(data, REALM_KEY, realm, RealmInstance::toNBT);
        saveInstanceCollection(data, REACHED_REALMS_KEY, reachedRealms, RealmInstance::toNBT, RealmInstance::getRealmId);
    }

    /**
     * Завантажує стан сховища з NBT формату.
     *
     * @param data Тег, з якого будуть завантажені дані
     */
    @Override
    public void load(CompoundTag data) {
        if (data.contains(REALM_KEY)) {
            realm = RealmInstance.fromNBT(data.getCompound(REALM_KEY));
        }
        loadCollections(data);
    }

    /**
     * Завантажує колекції з NBT даних.
     *
     * @param data Тег, з якого будуть завантажені колекції
     */
    private void loadCollections(CompoundTag data) {
        loadInstanceCollection(data, REACHED_REALMS_KEY, reachedRealms, RealmInstance::fromNBT);
    }

    /**
     * Отримує власника сховища як живу сутність.
     *
     * @return Власник сховища як LivingEntity
     */
    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }

    public void sync(){
        CompoundTag data = new CompoundTag();
        saveOutdated(data);
        InternalRealmPacketActions.sendSyncStoragePayload(data);
    }
}