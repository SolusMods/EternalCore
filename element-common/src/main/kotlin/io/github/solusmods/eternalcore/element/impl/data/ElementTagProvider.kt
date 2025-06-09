package io.github.solusmods.eternalcore.element.impl.data

import io.github.solusmods.eternalcore.element.api.Element
import io.github.solusmods.eternalcore.element.impl.ElementRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import java.util.concurrent.CompletableFuture
import java.util.function.Function

abstract class ElementTagProvider : IntrinsicHolderTagsProvider<Element?> {
    constructor(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider?>?) : super(
        output,
        ElementRegistry.key,
        lookupProvider,
        Function { element: Element? -> ElementRegistry.elements.getKey(element).orElseThrow() })

    constructor(
        output: PackOutput,
        lookupProvider: CompletableFuture<HolderLookup.Provider?>?,
        parentProvider: CompletableFuture<TagLookup<Element?>?>?
    ) : super(
        output,
        ElementRegistry.key,
        lookupProvider,
        parentProvider,
        Function { element: Element? -> ElementRegistry.elements.getKey(element).orElseThrow() })
}
