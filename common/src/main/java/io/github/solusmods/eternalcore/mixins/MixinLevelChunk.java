package io.github.solusmods.eternalcore.mixins;

import io.github.solusmods.eternalcore.api.network.util.PlayerLookup;
import io.github.solusmods.eternalcore.api.storage.AbstractStorage;
import io.github.solusmods.eternalcore.api.storage.StorageHolder;
import io.github.solusmods.eternalcore.api.storage.StorageKey;
import io.github.solusmods.eternalcore.api.storage.StorageType;
import io.github.solusmods.eternalcore.impl.storage.CombinedStorage;
import io.github.solusmods.eternalcore.impl.storage.StorageManager;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class MixinLevelChunk extends ChunkAccess implements StorageHolder {
    @Unique
    private CombinedStorage storage;

    public MixinLevelChunk(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable LevelChunkSection[] sections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, biomeRegistry, inhabitedTime, sections, blendingData);
    }

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
        return StorageType.CHUNK;
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
        return PlayerLookup.tracking((LevelChunk) (Object) this);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/UpgradeData;Lnet/minecraft/world/ticks/LevelChunkTicks;Lnet/minecraft/world/ticks/LevelChunkTicks;J[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V", at = @At("RETURN"))
    void initStorage(Level level, ChunkPos pos, UpgradeData data, LevelChunkTicks blockTicks, LevelChunkTicks fluidTicks, long inhabitedTime, LevelChunkSection[] sections, LevelChunk.PostLoadProcessor postLoad, BlendingData blendingData, CallbackInfo ci) {
        if (this.storage == null) {
            this.storage = new CombinedStorage(this);
            StorageManager.initialStorageFilling(this);
        }
    }
}
