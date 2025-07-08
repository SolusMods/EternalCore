package io.github.solusmods.eternalcore.impl.spiritual_root.data;

import io.github.solusmods.eternalcore.api.registry.SpiritualRootRegistry;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;

import java.util.concurrent.CompletableFuture;

public abstract class SpiritualRootTagProvider extends IntrinsicHolderTagsProvider<AbstractSpiritualRoot> {
    public SpiritualRootTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, SpiritualRootRegistry.KEY, lookupProvider, spiritualRoot -> SpiritualRootRegistry.SPIRITUAL_ROOTS.getKey(spiritualRoot).orElseThrow());
    }

    public SpiritualRootTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<AbstractSpiritualRoot>> parentProvider) {
        super(output, SpiritualRootRegistry.KEY, lookupProvider, parentProvider, spiritualRoot -> SpiritualRootRegistry.SPIRITUAL_ROOTS.getKey(spiritualRoot).orElseThrow());
    }
}
