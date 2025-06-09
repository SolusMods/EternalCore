package io.github.solusmods.eternalcore.spiritual_root.impl.data

import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRoot
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import java.util.concurrent.CompletableFuture
import java.util.function.Function

abstract class SpiritualRootTagProvider : IntrinsicHolderTagsProvider<SpiritualRoot?> {
    constructor(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider?>?) : super(
        output,
        SpiritualRootRegistry.KEY,
        lookupProvider,
        Function { spiritualRoot: SpiritualRoot? ->
            SpiritualRootRegistry.SPIRITUAL_ROOTS.getKey(spiritualRoot).orElseThrow()
        })

    constructor(
        output: PackOutput,
        lookupProvider: CompletableFuture<HolderLookup.Provider?>?,
        parentProvider: CompletableFuture<TagLookup<SpiritualRoot?>?>?
    ) : super(
        output,
        SpiritualRootRegistry.KEY,
        lookupProvider,
        parentProvider,
        Function { spiritualRoot: SpiritualRoot? ->
            SpiritualRootRegistry.SPIRITUAL_ROOTS.getKey(spiritualRoot).orElseThrow()
        })
}
