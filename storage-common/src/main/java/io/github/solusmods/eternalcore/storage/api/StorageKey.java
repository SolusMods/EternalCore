package io.github.solusmods.eternalcore.storage.api;

import net.minecraft.resources.ResourceLocation;

public record StorageKey<T extends Storage>(ResourceLocation id, Class<T> type) {
}
