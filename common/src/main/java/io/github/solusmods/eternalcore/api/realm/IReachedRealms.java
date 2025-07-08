package io.github.solusmods.eternalcore.api.realm;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IReachedRealms {

    Collection<AbstractRealm> getReachedRealms();

    default boolean addRealm(@NotNull ResourceLocation realmId, boolean teleportToSpawn) {
        return addRealm(realmId, teleportToSpawn, null);
    }

    default boolean addRealm(@NotNull ResourceLocation realmId, boolean teleportToSpawn, @Nullable MutableComponent component) {
        AbstractRealm abstractRealm = RealmAPI.getRealmRegistry().get(realmId);
        if (abstractRealm == null) return false;
        return addRealm(abstractRealm, false, teleportToSpawn, component);
    }

    default boolean addRealm(@NonNull AbstractRealm abstractRealm, boolean teleportToSpawn) {
        return addRealm(abstractRealm, teleportToSpawn, null);
    }

    default boolean addRealm(@NonNull AbstractRealm abstractRealm, boolean teleportToSpawn, @Nullable MutableComponent component) {
        return addRealm(abstractRealm, false, teleportToSpawn, component);
    }

    boolean addRealm(AbstractRealm abstractRealm, boolean breakthrough, boolean teleportToSpawn, @Nullable MutableComponent component);

    void markDirty();
}
