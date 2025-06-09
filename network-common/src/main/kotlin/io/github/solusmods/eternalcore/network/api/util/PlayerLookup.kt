package io.github.solusmods.eternalcore.network.api.util

import lombok.experimental.UtilityClass
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerPlayerConnection
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.chunk.LevelChunk
import java.util.*
import java.util.stream.Collectors

@UtilityClass
object PlayerLookup {
    /**
     * Returns a collection of players tracking the entity.
     */
    fun tracking(entity: Entity): MutableCollection<ServerPlayer?> {
        val manager = entity.level().getChunkSource()
        require(manager is ServerChunkCache) { "Only supported on server worlds!" }
        val trackedEntity = manager.chunkMap.entityMap.get(entity.getId())
        if (trackedEntity == null) return mutableSetOf<ServerPlayer?>()
        return trackedEntity.seenBy.stream().map<ServerPlayer?> { obj: ServerPlayerConnection? -> obj!!.getPlayer() }
            .collect(Collectors.toUnmodifiableSet())
    }

    /**
     * Returns a collection of players tracking the entity and the entity itself (only if the entity is a [ServerPlayer]).
     */
    @JvmStatic
    fun trackingAndSelf(entity: Entity): MutableCollection<ServerPlayer?> {
        val watchers: Deque<ServerPlayer?> = ArrayDeque<ServerPlayer?>(tracking(entity))
        if (entity is ServerPlayer) watchers.addFirst(entity)
        return watchers
    }

    @JvmStatic
    fun tracking(chunk: LevelChunk): MutableCollection<ServerPlayer?> {
        require(chunk.level is ServerLevel) { "Only supported on server worlds!" }
        val level = chunk.level as ServerLevel
        return level.chunkSource.chunkMap.getPlayers(chunk.pos, false)
    }
}
