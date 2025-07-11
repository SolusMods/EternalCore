package io.github.solusmods.eternalcore.mixins;

import io.github.solusmods.eternalcore.api.storage.AbstractStorage;
import io.github.solusmods.eternalcore.api.storage.StorageHolder;
import io.github.solusmods.eternalcore.api.storage.StorageKey;
import io.github.solusmods.eternalcore.api.storage.StorageType;
import io.github.solusmods.eternalcore.impl.storage.CombinedStorage;
import io.github.solusmods.eternalcore.impl.storage.StorageManager;
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
    public @NotNull CompoundTag eternalCore$getStorage() {
        return this.storage.toNBT();
    }

    @Nullable
    @Override
    public <T extends AbstractStorage> T eternalCore$getStorage(StorageKey<T> storageKey) {
        return (T) this.storage.get(storageKey.id());
    }

    @Override
    public void eternalCore$attachStorage(@NotNull ResourceLocation id, @NotNull AbstractStorage storage) {
        this.storage.add(id, storage);
    }

    @Override
    public @NotNull StorageType eternalCore$getStorageType() {
        return StorageType.WORLD;
    }

    @Override
    public @NotNull CombinedStorage eternalCore$getCombinedStorage() {
        return this.storage;
    }

    @Override
    public void eternalCore$setCombinedStorage(@NotNull CombinedStorage storage) {
        this.storage = storage;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    void initStorage(WritableLevelData levelData, ResourceKey dimension, RegistryAccess registryAccess, Holder dimensionTypeRegistration, Supplier profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates, CallbackInfo ci) {
        this.storage = new CombinedStorage(this);
        StorageManager.initialStorageFilling(this);
    }
}
