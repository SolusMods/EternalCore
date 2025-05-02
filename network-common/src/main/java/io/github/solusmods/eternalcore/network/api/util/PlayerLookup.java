package io.github.solusmods.eternalcore.network.api.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.stream.Collectors;

@UtilityClass
public final class PlayerLookup {
    /**
     * Returns a collection of players tracking the entity.
     */
    public static Collection<ServerPlayer> tracking(@NonNull Entity entity) {
        ChunkSource manager = entity.level().getChunkSource();
        if (!(manager instanceof ServerChunkCache cache))
            throw new IllegalArgumentException("Only supported on server worlds!");
        ChunkMap.TrackedEntity trackedEntity = cache.chunkMap.entityMap.get(entity.getId());
        if (trackedEntity == null) return Collections.emptySet();
        return trackedEntity.seenBy.stream().map(ServerPlayerConnection::getPlayer).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns a collection of players tracking the entity and the entity itself (only if the entity is a {@link ServerPlayer}).
     */
    public static Collection<ServerPlayer> trackingAndSelf(@NonNull Entity entity) {
        Deque<ServerPlayer> watchers = new ArrayDeque<>(tracking(entity));
        if (entity instanceof ServerPlayer player) watchers.addFirst(player);
        return watchers;
    }

    public static Collection<ServerPlayer> tracking(@NonNull LevelChunk chunk) {
        if (!(chunk.getLevel() instanceof ServerLevel level))
            throw new IllegalArgumentException("Only supported on server worlds!");
        return level.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false);
    }
}
