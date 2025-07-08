package io.github.solusmods.eternalcore.api.storage;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.Predicate;

public interface StorageEvents {
    Event<RegisterStorage<Entity>> REGISTER_ENTITY_STORAGE = EventFactory.createLoop();
    Event<RegisterStorage<LevelChunk>> REGISTER_CHUNK_STORAGE = EventFactory.createLoop();
    Event<RegisterStorage<Level>> REGISTER_WORLD_STORAGE = EventFactory.createLoop();

    @FunctionalInterface
    interface RegisterStorage<T extends StorageHolder> {
        void register(StorageRegistry<T> registry);
    }

    @FunctionalInterface
    interface StorageFactory<T extends StorageHolder, S extends AbstractStorage> {
        S create(T target);
    }

    interface StorageRegistry<T extends StorageHolder> {
        <S extends AbstractStorage> StorageKey<S> register(ResourceLocation id, Class<S> storageClass, Predicate<T> attachCheck, StorageFactory<T, S> factory);
    }
}
