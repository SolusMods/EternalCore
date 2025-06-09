package io.github.solusmods.eternalcore.storage.api

import net.minecraft.resources.ResourceLocation

@JvmRecord
data class StorageKey<T : Storage?>(@JvmField val id: ResourceLocation, val type: Class<T>)