package io.github.solusmods.eternalcore.api.realm;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Realms {

    Optional<AbstractRealm> getRealm();

    default boolean setRealm(@NotNull ResourceLocation realmId, boolean notify) {
        return setRealm(realmId, notify, null);
    }

    default boolean setRealm(@NotNull ResourceLocation realmId, boolean notify, @Nullable MutableComponent component) {
        AbstractRealm abstractRealm = RealmAPI.getRealmRegistry().get(realmId);
        if (abstractRealm == null) return false;
        return setRealm(abstractRealm, false, notify, component);
    }

    default boolean setRealm(@NonNull AbstractRealm abstractRealm, boolean notify) {
        return setRealm(abstractRealm, notify, null);
    }

    default boolean setRealm(@NonNull AbstractRealm abstractRealm, boolean notify, @Nullable MutableComponent component) {
        return setRealm(abstractRealm, false, notify, component);
    }

    boolean setRealm(AbstractRealm instance, boolean breakthrough, boolean notify, @Nullable MutableComponent component);


    default boolean breakthroughRealm(@NotNull ResourceLocation realmId) {
        return breakthroughRealm(realmId, null);
    }

    default boolean breakthroughRealm(@NotNull ResourceLocation realmId, @Nullable MutableComponent component) {
        AbstractRealm abstractRealm = RealmAPI.getRealmRegistry().get(realmId);
        if (abstractRealm == null) return false;
        return setRealm(abstractRealm, true, false, null);
    }

    default void breakthroughRealm(@NonNull AbstractRealm abstractRealm) {
        this.breakthroughRealm(abstractRealm, null);
    }

    default void breakthroughRealm(@NonNull AbstractRealm abstractRealm, @Nullable MutableComponent component) {
        setRealm(abstractRealm, true, false, component);
    }

    void markDirty();
}
