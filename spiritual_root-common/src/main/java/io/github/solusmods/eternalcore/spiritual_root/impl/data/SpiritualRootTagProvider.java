package io.github.solusmods.eternalcore.spiritual_root.impl.data;

import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRoot;
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;

import java.util.concurrent.CompletableFuture;

public abstract class SpiritualRootTagProvider extends IntrinsicHolderTagsProvider<SpiritualRoot> {
    public SpiritualRootTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, SpiritualRootRegistry.KEY, lookupProvider, spiritualRoot -> SpiritualRootRegistry.SPIRITUAL_ROOTS.getKey(spiritualRoot).orElseThrow());
    }

    public SpiritualRootTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<SpiritualRoot>> parentProvider) {
        super(output, SpiritualRootRegistry.KEY, lookupProvider, parentProvider, spiritualRoot -> SpiritualRootRegistry.SPIRITUAL_ROOTS.getKey(spiritualRoot).orElseThrow());
    }
}
