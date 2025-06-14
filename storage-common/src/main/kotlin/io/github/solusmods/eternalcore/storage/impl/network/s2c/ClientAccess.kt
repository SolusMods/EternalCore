package io.github.solusmods.eternalcore.storage.impl.network.s2c

import io.github.solusmods.eternalcore.storage.api.StorageHolder
import io.github.solusmods.eternalcore.storage.impl.CombinedStorage
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity

object ClientAccess {

    fun handle(packet: SyncEntityStoragePayload) {
        getEntityFromId(packet.entityId)?.let { entity ->
            handleUpdatePacket(entity, packet)
        }
    }

    fun getEntityFromId(id: Int): Entity? {
        val mc = Minecraft.getInstance()
        val player = mc.player
        if (player?.id == id) return player
        return mc.level?.getEntity(id)
    }

    fun handle(packet: SyncChunkStoragePayload) {
        Minecraft.getInstance().level?.let { level ->
            val chunk = level.getChunk(packet.chunkPos.x, packet.chunkPos.z)
            handleUpdatePacket(chunk, packet)
        }
    }

    fun handle(packet: SyncWorldStoragePayload) {
        Minecraft.getInstance().level?.let { level ->
            handleUpdatePacket(level, packet)
        }
    }

    fun handleUpdatePacket(holder: StorageHolder, packet: StorageSyncPayload) {
        val storage = if (packet.isUpdate) {
            holder.`eternalCore$getCombinedStorage`().apply {
                handleUpdatePacket(packet.storageTag)
            }
        } else {
            CombinedStorage(holder).apply {
                load(packet.storageTag)
                holder.`eternalCore$setCombinedStorage`(this)
            }
        }
    }
}
