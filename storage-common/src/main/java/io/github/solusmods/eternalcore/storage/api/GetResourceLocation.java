package io.github.solusmods.eternalcore.storage.api;

import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface GetResourceLocation<T> {
    ResourceLocation getId(T instance);
}
