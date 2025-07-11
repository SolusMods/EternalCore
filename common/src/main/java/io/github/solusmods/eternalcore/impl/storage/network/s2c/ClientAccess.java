package io.github.solusmods.eternalcore.impl.storage.network.s2c;


import io.github.solusmods.eternalcore.api.storage.StorageHolder;
import io.github.solusmods.eternalcore.impl.storage.CombinedStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {


    static void handle(SyncEntityStoragePayload packet) {
        Entity entity = getEntityFromId(packet.entityId());
        if (entity == null) return;
        handleUpdatePacket(entity, packet);
    }

    @Nullable
    static Entity getEntityFromId(int id) {
        // Early return if this is a self-update packet
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getId() == id)
            return Minecraft.getInstance().player;
        // Get entity from level
        Level level = Minecraft.getInstance().level;
        if (level == null) return null;
        return level.getEntity(id);
    }

    static void handle(SyncChunkStoragePayload packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        LevelChunk chunk = level.getChunk(packet.chunkPos().x, packet.chunkPos().z);
        handleUpdatePacket(chunk, packet);
    }

    static void handle(SyncWorldStoragePayload packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        handleUpdatePacket(level, packet);
    }


    static void handleUpdatePacket(StorageHolder holder, StorageSyncPayload packet) {
        if (packet.isUpdate()) {
            holder.eternalCore$getCombinedStorage().handleUpdatePacket(packet.storageTag());
        } else {
            CombinedStorage updatedStorage = new CombinedStorage(holder);
            updatedStorage.load(packet.storageTag());
            holder.eternalCore$setCombinedStorage(updatedStorage);
        }
    }
}
