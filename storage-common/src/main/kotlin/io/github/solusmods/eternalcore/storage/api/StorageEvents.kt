package io.github.solusmods.eternalcore.storage.api

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk
import java.util.function.Predicate

interface StorageEvents {

    fun interface RegisterStorage<T : StorageHolder> {
        fun register(registry: StorageRegistry<T>)
    }

    fun interface StorageFactory<T : StorageHolder, S : Storage> {
        fun create(target: T): S
    }

    interface StorageRegistry<T : StorageHolder> {
        fun <S : Storage> register(
            id: ResourceLocation,
            storageClass: Class<S>,
            attachCheck: Predicate<T>,
            factory: StorageFactory<T, S>
        ): StorageKey<S>
    }

    companion object {
        val REGISTER_ENTITY_STORAGE: Event<RegisterStorage<Entity>> =
            EventFactory.createLoop()

        val REGISTER_CHUNK_STORAGE: Event<RegisterStorage<LevelChunk>> =
            EventFactory.createLoop()

        val REGISTER_WORLD_STORAGE: Event<RegisterStorage<Level>> =
            EventFactory.createLoop()
    }
}