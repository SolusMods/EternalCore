package io.github.solusmods.eternalcore.storage.api;

import net.minecraft.nbt.CompoundTag;

@FunctionalInterface
public interface NBTSerializer<T>{
    CompoundTag toNBT(T instance);
}
