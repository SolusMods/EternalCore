package io.github.solusmods.eternalcore.api.storage;

import net.minecraft.resources.ResourceLocation;

public record StorageKey<T extends AbstractStorage>(ResourceLocation id, Class<T> type) {
}
