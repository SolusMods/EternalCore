package io.github.solusmods.eternalcore.realm.api;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IReachedRealms {

    Collection<RealmInstance> getReachedRealms();

    default boolean addRealm(@NotNull ResourceLocation realmId, boolean teleportToSpawn) {
        return addRealm(realmId, teleportToSpawn, null);
    }

    default boolean addRealm(@NotNull ResourceLocation realmId, boolean teleportToSpawn, @Nullable MutableComponent component) {
        Realm realm = RealmAPI.getRealmRegistry().get(realmId);
        if (realm == null) return false;
        return addRealm(realm.createDefaultInstance(), false, teleportToSpawn, component);
    }

    default boolean addRealm(@NonNull Realm realm, boolean teleportToSpawn) {
        return addRealm(realm, teleportToSpawn, null);
    }

    default boolean addRealm(@NonNull Realm realm, boolean teleportToSpawn, @Nullable MutableComponent component) {
        return addRealm(realm.createDefaultInstance(), false, teleportToSpawn, component);
    }

    default boolean addRealm(RealmInstance instance, boolean breakthrough, boolean teleportToSpawn) {
        return addRealm(instance, breakthrough, teleportToSpawn, null);
    }

    boolean addRealm(RealmInstance instance, boolean breakthrough, boolean teleportToSpawn, @Nullable MutableComponent component);

    void markDirty();

    void sync();
}
