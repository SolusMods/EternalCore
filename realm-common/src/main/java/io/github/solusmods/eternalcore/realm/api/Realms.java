package io.github.solusmods.eternalcore.realm.api;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Realms {

    Optional<RealmInstance> getRealm();

    default boolean setRealm(@NotNull ResourceLocation realmId, boolean notify) {
        return setRealm(realmId, notify, null);
    }

    default boolean setRealm(@NotNull ResourceLocation realmId, boolean notify, @Nullable MutableComponent component) {
        Realm realm = RealmAPI.getRealmRegistry().get(realmId);
        if (realm == null) return false;
        return setRealm(realm.createDefaultInstance(), false, notify, component);
    }

    default boolean setRealm(@NonNull Realm realm, boolean notify) {
        return setRealm(realm, notify, null);
    }

    default boolean setRealm(@NonNull Realm realm, boolean notify, @Nullable MutableComponent component) {
        return setRealm(realm.createDefaultInstance(), false, notify, component);
    }

    default boolean setRealm(RealmInstance instance, boolean breakthrough, boolean notify) {
        return setRealm(instance, breakthrough, notify, null);
    }

    boolean setRealm(RealmInstance instance, boolean breakthrough, boolean notify, @Nullable MutableComponent component);

    void markDirty();

    void sync();
}
