package io.github.solusmods.eternalcore.stage.impl.data

import io.github.solusmods.eternalcore.stage.api.Stage
import io.github.solusmods.eternalcore.stage.impl.StageRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import java.util.concurrent.CompletableFuture
import java.util.function.Function

abstract class StageTagProvider : IntrinsicHolderTagsProvider<Stage?> {
    constructor(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider?>?) : super(
        output,
        StageRegistry.KEY,
        lookupProvider,
        Function { stage: Stage? -> StageRegistry.STAGES.getKey(stage).orElseThrow() })

    constructor(
        output: PackOutput,
        lookupProvider: CompletableFuture<HolderLookup.Provider?>?,
        parentProvider: CompletableFuture<TagLookup<Stage?>?>?
    ) : super(
        output,
        StageRegistry.KEY,
        lookupProvider,
        parentProvider,
        Function { stage: Stage? -> StageRegistry.STAGES.getKey(stage).orElseThrow() })
}
