package io.github.solusmods.eternalcore.stage.impl.data;

import io.github.solusmods.eternalcore.stage.api.Stage;
import io.github.solusmods.eternalcore.stage.impl.StageRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;

import java.util.concurrent.CompletableFuture;

public abstract class StageTagProvider extends IntrinsicHolderTagsProvider<Stage> {

    public StageTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, StageRegistry.KEY, lookupProvider, stage -> StageRegistry.STAGES.getKey(stage).orElseThrow());
    }

    public StageTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Stage>> parentProvider) {
        super(output, StageRegistry.KEY, lookupProvider, parentProvider, stage -> StageRegistry.STAGES.getKey(stage).orElseThrow());
    }
}
