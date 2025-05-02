package io.github.solusmods.eternalcore.storage.mixin;


import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageHolder;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import io.github.solusmods.eternalcore.storage.api.StorageType;
import io.github.solusmods.eternalcore.storage.impl.CombinedStorage;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(Level.class)
public abstract class MixinLevel implements StorageHolder, LevelAccessor {
    @Unique
    private CombinedStorage storage;


    @Override
    public @NotNull CompoundTag eternalCraft$getStorage() {
        return this.storage.toNBT();
    }

    @Nullable
    @Override
    public <T extends Storage> T eternalCraft$getStorage(StorageKey<T> storageKey) {
        return (T) this.storage.get(storageKey.id());
    }

    @Override
    public void eternalCraft$attachStorage(@NotNull ResourceLocation id, @NotNull Storage storage) {
        this.storage.add(id, storage);
    }

    @Override
    public @NotNull StorageType eternalCraft$getStorageType() {
        return StorageType.WORLD;
    }

    @Override
    public @NotNull CombinedStorage eternalCraft$getCombinedStorage() {
        return this.storage;
    }

    @Override
    public void eternalCraft$setCombinedStorage(@NotNull CombinedStorage storage) {
        this.storage = storage;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    void initStorage(WritableLevelData levelData, ResourceKey dimension, RegistryAccess registryAccess, Holder dimensionTypeRegistration, Supplier profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates, CallbackInfo ci) {
        this.storage = new CombinedStorage(this);
        StorageManager.initialStorageFilling(this);
    }
}
