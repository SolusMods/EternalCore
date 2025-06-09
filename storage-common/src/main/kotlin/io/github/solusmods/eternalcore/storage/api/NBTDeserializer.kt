package io.github.solusmods.eternalcore.storage.api

import net.minecraft.nbt.CompoundTag

fun interface NBTDeserializer<T> {
    fun fromNBT(tag: CompoundTag?): T?
}