package io.github.solusmods.eternalcore.storage.api;

import io.github.solusmods.eternalcore.storage.impl.CombinedStorage;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface StorageHolder {
    @NotNull
    default CompoundTag eternalCore$getStorage() {
        throw new AssertionError();
    }

    @Nullable
    default <T extends Storage> T eternalCore$getStorage(StorageKey<T> storageKey) {
        throw new AssertionError();
    }

    @NotNull
    default <T extends Storage> Optional<T> eternalCraft$getStorageOptional(StorageKey<T> storageKey) {
        return Optional.ofNullable(this.eternalCore$getStorage(storageKey));
    }

    default void eternalCraft$sync(boolean update) {
        StorageManager.syncTracking(this, update);
    }

    default void eternalCraft$sync() {
        this.eternalCraft$sync(false);
    }

    default void eternalCraft$sync(@NotNull ServerPlayer target) {
        StorageManager.syncTarget(this, target);
    }

    default void eternalCore$attachStorage(@NotNull ResourceLocation id, @NotNull Storage storage) {
        throw new AssertionError();
    }

    @NotNull
    default StorageType eternalCore$getStorageType() {
        throw new AssertionError();
    }

    @NotNull
    default CombinedStorage eternalCore$getCombinedStorage() {
        throw new AssertionError();
    }

    default void eternalCore$setCombinedStorage(@NotNull CombinedStorage storage) {
        throw new AssertionError();
    }

    default Iterable<ServerPlayer> eternalCore$getTrackingPlayers() {
        throw new AssertionError();
    }
}
