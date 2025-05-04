package io.github.solusmods.eternalcore.storage.mixin;

import io.github.solusmods.eternalcore.network.api.util.PlayerLookup;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageHolder;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import io.github.solusmods.eternalcore.storage.api.StorageType;
import io.github.solusmods.eternalcore.storage.impl.CombinedStorage;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity implements StorageHolder {
    private static final String STORAGE_TAG_KEY = "eternalCoreStorage";
    @Shadow
    private Level level;
    @Unique
    private CombinedStorage storage;

    @Override
    public @NotNull CompoundTag eternalCore$getStorage() {
        return this.storage.toNBT();
    }

    @Nullable
    @Override
    public <T extends Storage> T eternalCore$getStorage(StorageKey<T> storageKey) {
        return (T) this.storage.get(storageKey.id());
    }

    @Override
    public void eternalCore$attachStorage(@NotNull ResourceLocation id, @NotNull Storage storage) {
        this.storage.add(id, storage);
    }

    @Override
    public @NotNull StorageType eternalCore$getStorageType() {
        return StorageType.ENTITY;
    }

    @Override
    public @NotNull CombinedStorage eternalCore$getCombinedStorage() {
        return this.storage;
    }

    @Override
    public void eternalCore$setCombinedStorage(@NotNull CombinedStorage storage) {
        this.storage = storage;
    }

    @Override
    public Iterable<ServerPlayer> eternalCore$getTrackingPlayers() {
        return PlayerLookup.trackingAndSelf((Entity) (Object) this);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    void initStorage(EntityType entityType, Level level, CallbackInfo ci) {
        // Create empty storage
        this.storage = new CombinedStorage(this);
        // Fill storage with data
        StorageManager.initialStorageFilling(this);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER), cancellable = true)
    void saveStorage(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        compound.put(STORAGE_TAG_KEY, this.storage.toNBT());
        cir.setReturnValue(compound);
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    void loadStorage(CompoundTag compound, CallbackInfo ci) {
        this.storage.load(compound.getCompound(STORAGE_TAG_KEY));
    }

    @Inject(method = "tick", at = @At("RETURN"))
    void onTickSyncCheck(CallbackInfo ci) {
        if (this.level.isClientSide()) return;
        this.level.getProfiler().push("eternalCoreSyncCheck");
        if (this.storage.isDirty()) StorageManager.syncTracking((Entity) (Object) this, true);
        this.level.getProfiler().pop();
    }
}
