package io.github.solusmods.eternalcore.impl.stage.data;

import io.github.solusmods.eternalcore.api.registry.StageRegistry;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;

import java.util.concurrent.CompletableFuture;

public abstract class StageTagProvider extends IntrinsicHolderTagsProvider<AbstractStage> {

    public StageTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, StageRegistry.KEY, lookupProvider, stage -> StageRegistry.STAGES.getKey(stage).orElseThrow());
    }

    public StageTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<AbstractStage>> parentProvider) {
        super(output, StageRegistry.KEY, lookupProvider, parentProvider, stage -> StageRegistry.STAGES.getKey(stage).orElseThrow());
    }
}
