package io.github.solusmods.eternalcore.abilities.impl.data

import io.github.solusmods.eternalcore.abilities.api.Ability
import io.github.solusmods.eternalcore.abilities.impl.AbilityRegistry
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * Provider for ability tags that allows registration of tags for [Ability] entities.
 * This class handles both synchronous and parent-dependent tag generation.
 */
abstract class AbilityTagProvider : IntrinsicHolderTagsProvider<Ability?> {
    constructor(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider?>?) : super(
        output,
        AbilityRegistry.KEY,
        lookupProvider,
        Function { ability: Ability? -> AbilityRegistry.ABILITIES.getKey(ability).orElseThrow() })

    constructor(
        output: PackOutput,
        lookupProvider: CompletableFuture<HolderLookup.Provider?>?,
        parentProvider: CompletableFuture<TagLookup<Ability?>?>?
    ) : super(
        output,
        AbilityRegistry.KEY,
        lookupProvider,
        parentProvider,
        Function { ability: Ability? -> AbilityRegistry.ABILITIES.getKey(ability).orElseThrow() })
}
