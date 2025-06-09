package io.github.solusmods.eternalcore.storage.api

import net.minecraft.nbt.CompoundTag

fun interface SimpleNBTDeserializer<T> {
    fun fromNBT(tag: CompoundTag?, key: String?): T?
}