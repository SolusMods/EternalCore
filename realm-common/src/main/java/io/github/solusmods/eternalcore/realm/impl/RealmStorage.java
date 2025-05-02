package io.github.solusmods.eternalcore.realm.impl;

import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.realm.EternalCoreRealm;
import io.github.solusmods.eternalcore.realm.api.*;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageEvents;
import io.github.solusmods.eternalcore.storage.api.StorageHolder;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


public class RealmStorage extends Storage implements Realms, IReachedRealms {
    private static final String REALM_KEY = "realm_key";
    private static final String REACHED_REALMS_KEY = "reached_realms_key";
    public static final ResourceLocation ID = EternalCoreRealm.create("realm_storage");
    @Getter
    private static StorageKey<RealmStorage> key = null;
    private final Collection<RealmInstance> reachedRealms = new ArrayList<>();
    private RealmInstance realm = null;

    protected RealmStorage(StorageHolder holder) {
        super(holder);
    }

    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        RealmStorage.class, Player.class::isInstance,
                        RealmStorage::new));
    }

    @Override
    public Collection<RealmInstance> getReachedRealms() {
        return this.reachedRealms;
    }

    @Override
    public Optional<RealmInstance> getRealm() {
        return Optional.ofNullable(realm);
    }

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

    @Override
    public void save(CompoundTag data) {
        saveInstance(data, REALM_KEY, realm, RealmInstance::toNBT);
        saveInstanceCollection(data, REACHED_REALMS_KEY, reachedRealms, RealmInstance::toNBT, RealmInstance::getRealmId);
    }

    @Override
    public void load(CompoundTag data) {
        loadSingleInstances(data);
        loadCollections(data);
    }

    private void loadSingleInstances(CompoundTag data) {
        loadSingleInstance(data, REALM_KEY, realm, RealmInstance::fromNBT);
    }

    private void loadCollections(CompoundTag data) {
        loadInstanceCollection(data, REACHED_REALMS_KEY, reachedRealms, RealmInstance::fromNBT);
    }

    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }
}
