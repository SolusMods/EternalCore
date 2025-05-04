package io.github.solusmods.eternalcore.storage.impl;

import com.mojang.datafixers.util.Pair;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.storage.api.*;
import io.github.solusmods.eternalcore.storage.impl.network.c2s.SyncChunkStorageC2SPayload;
import io.github.solusmods.eternalcore.storage.impl.network.c2s.SyncEntityStorageC2SPayload;
import io.github.solusmods.eternalcore.storage.impl.network.c2s.SyncWorldStorageCTSPayload;
import io.github.solusmods.eternalcore.storage.impl.network.s2c.StorageSyncPayload;
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncChunkStoragePayload;
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncEntityStoragePayload;
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncWorldStoragePayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class StorageManager {

    private static final StorageRegistryImpl<Entity> ENTITY_STORAGE_REGISTRY = new StorageRegistryImpl<>();
    private static final StorageRegistryImpl<LevelChunk> CHUNK_STORAGE_REGISTRY = new StorageRegistryImpl<>();
    private static final StorageRegistryImpl<Level> LEVEL_STORAGE_REGISTRY = new StorageRegistryImpl<>();

    private StorageManager() {
    }

    public static void init() {
        StorageEvents.REGISTER_WORLD_STORAGE.invoker().register(LEVEL_STORAGE_REGISTRY);
        StorageEvents.REGISTER_CHUNK_STORAGE.invoker().register(CHUNK_STORAGE_REGISTRY);
        StorageEvents.REGISTER_ENTITY_STORAGE.invoker().register(ENTITY_STORAGE_REGISTRY);
        // Initial client synchronization
        PlayerEvent.PLAYER_JOIN.register(player -> {
            player.eternalCore$sync(player);
            ServerLevel level = player.serverLevel();
            level.eternalCore$sync(player);
        });
        // Synchronization on respawn and dimension change
        PlayerEvent.PLAYER_RESPAWN.register((player, b, removalReason) -> {
            player.eternalCore$sync(player);
            ServerLevel level = player.serverLevel();
            level.eternalCore$sync(player);
        });
        PlayerEvent.CHANGE_DIMENSION.register((player, resourceKey, resourceKey1) -> {
            player.eternalCore$sync(player);
            ServerLevel level = player.serverLevel();
            level.eternalCore$sync(player);
        });

        // Copy storage from old player to new player
        PlayerEvent.PLAYER_CLONE.register((oldPlayer, newPlayer, wonGame) -> {
            CombinedStorage newStorage = new CombinedStorage(newPlayer);
            newStorage.load(oldPlayer.eternalCore$getCombinedStorage().toNBT());
            newPlayer.eternalCore$setCombinedStorage(newStorage);
        });
    }

    public static void initialStorageFilling(StorageHolder holder) {
        switch (holder.eternalCore$getStorageType()) {
            case ENTITY -> ENTITY_STORAGE_REGISTRY.attach((Entity) holder);
            case CHUNK -> CHUNK_STORAGE_REGISTRY.attach((LevelChunk) holder);
            case WORLD -> LEVEL_STORAGE_REGISTRY.attach((Level) holder);
        }
    }

    public static void syncTracking(StorageHolder source) {
        syncTracking(source, false);
    }

    public static void syncTracking(StorageHolder source, boolean update) {
        NetworkManager.sendToPlayers(source.eternalCore$getTrackingPlayers(), createSyncPacket(source, update));
    }

    public static void syncTarget(StorageHolder source, ServerPlayer target) {
        NetworkManager.sendToPlayer(target, createSyncPacket(source, false));
    }

    public static void toServer(StorageHolder storageHolder){
        NetworkManager.sendToServer(createSyncToServerPacket(storageHolder, true));
    }

    public static StorageSyncPayload createSyncPacket(StorageHolder source, boolean update) {
        return switch (source.eternalCore$getStorageType()) {
            case ENTITY -> {
                Entity sourceEntity = (Entity) source;
                yield new SyncEntityStoragePayload(
                        update,
                        sourceEntity.getId(),
                        update ? sourceEntity.eternalCore$getCombinedStorage().createUpdatePacket(true)
                                : sourceEntity.eternalCore$getCombinedStorage().toNBT()
                );
            }
            case CHUNK -> {
                LevelChunk sourceChunk = (LevelChunk) source;
                yield new SyncChunkStoragePayload(
                        update,
                        sourceChunk.getPos(),
                        update ? sourceChunk.eternalCore$getCombinedStorage().createUpdatePacket(true)
                                : sourceChunk.eternalCore$getCombinedStorage().toNBT()
                );
            }
            case WORLD -> new SyncWorldStoragePayload(
                    update,
                    update ? source.eternalCore$getCombinedStorage().createUpdatePacket(true)
                            : source.eternalCore$getCombinedStorage().toNBT()
            );
        };
    }

    public static StorageSyncPayload createSyncToServerPacket(StorageHolder source, boolean update) {
        return switch (source.eternalCore$getStorageType()) {
            case ENTITY -> {
                Entity sourceEntity = (Entity) source;
                yield new SyncEntityStorageC2SPayload(
                        update,
                        sourceEntity.getId(),
                        update ? sourceEntity.eternalCore$getCombinedStorage().createUpdatePacket(true)
                                : sourceEntity.eternalCore$getCombinedStorage().toNBT()
                );
            }
            case CHUNK -> {
                LevelChunk sourceChunk = (LevelChunk) source;
                yield new SyncChunkStorageC2SPayload(
                        update,
                        sourceChunk.getPos(),
                        update ? sourceChunk.eternalCore$getCombinedStorage().createUpdatePacket(true)
                                : sourceChunk.eternalCore$getCombinedStorage().toNBT()
                );
            }
            case WORLD -> new SyncWorldStorageCTSPayload(
                    update,
                    update ? source.eternalCore$getCombinedStorage().createUpdatePacket(true)
                            : source.eternalCore$getCombinedStorage().toNBT()
            );
        };
    }

    @Nullable
    public static Storage constructStorageFor(StorageType type, ResourceLocation id, StorageHolder holder) {
        return switch (type) {
            case ENTITY -> ENTITY_STORAGE_REGISTRY.registry.get(id).getSecond().create((Entity) holder);
            case CHUNK -> CHUNK_STORAGE_REGISTRY.registry.get(id).getSecond().create((LevelChunk) holder);
            case WORLD -> LEVEL_STORAGE_REGISTRY.registry.get(id).getSecond().create((Level) holder);
        };
    }

    @Nullable
    public static <T extends Storage> T getStorage(StorageHolder holder, StorageKey<T> storageKey) {
        return holder.eternalCore$getStorage(storageKey);
    }

    private static class StorageRegistryImpl<T extends StorageHolder> implements StorageEvents.StorageRegistry<T> {
        private final Map<ResourceLocation, Pair<Predicate<T>, StorageEvents.StorageFactory<T, ?>>> registry = new HashMap<>();

        @Override
        public <S extends Storage> StorageKey<S> register(ResourceLocation id, Class<S> storageClass, Predicate<T> attachCheck, StorageEvents.StorageFactory<T, S> factory) {
            this.registry.put(id, Pair.of(attachCheck, factory));
            return new StorageKey<>(id, storageClass);
        }

        public void attach(T target) {
            this.registry.forEach((id, checkAndFactory) -> {
                if (!checkAndFactory.getFirst().test(target)) return;
                Storage storage = checkAndFactory.getSecond().create(target);
                target.eternalCore$attachStorage(id, storage);
            });
        }
    }
}
