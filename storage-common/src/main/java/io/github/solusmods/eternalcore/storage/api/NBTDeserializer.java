package io.github.solusmods.eternalcore.storage.api;

import net.minecraft.nbt.CompoundTag;

@FunctionalInterface
public interface NBTDeserializer<T>{
    T fromNBT(CompoundTag tag);
}