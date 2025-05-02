package io.github.solusmods.eternalcore.storage.api;

import net.minecraft.nbt.CompoundTag;

@FunctionalInterface
public interface SimpleNBTDeserializer<T>{
    T fromNBT(CompoundTag tag, String key);
}