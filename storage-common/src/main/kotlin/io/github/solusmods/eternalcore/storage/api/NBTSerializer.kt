package io.github.solusmods.eternalcore.storage.api

import net.minecraft.nbt.CompoundTag

fun interface NBTSerializer<T> {
    fun toNBT(instance: T?): CompoundTag?
}
