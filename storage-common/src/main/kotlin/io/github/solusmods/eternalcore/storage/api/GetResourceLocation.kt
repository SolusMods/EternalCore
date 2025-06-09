package io.github.solusmods.eternalcore.storage.api

import net.minecraft.resources.ResourceLocation

fun interface GetResourceLocation<T> {
    fun getId(instance: T?): ResourceLocation?
}
