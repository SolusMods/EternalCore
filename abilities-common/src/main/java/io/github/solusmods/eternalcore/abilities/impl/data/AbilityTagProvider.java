package io.github.solusmods.eternalcore.abilities.impl.data;

import io.github.solusmods.eternalcore.abilities.api.Ability;
import io.github.solusmods.eternalcore.abilities.impl.AbilityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Provider for ability tags that allows registration of tags for {@link Ability} entities.
 * This class handles both synchronous and parent-dependent tag generation.
 */
public abstract class AbilityTagProvider extends IntrinsicHolderTagsProvider<Ability> {
    public AbilityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, AbilityRegistry.KEY, lookupProvider, ability -> AbilityRegistry.ABILITIES.getKey(ability).orElseThrow());
    }

    public AbilityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Ability>> parentProvider) {
        super(output, AbilityRegistry.KEY, lookupProvider, parentProvider, ability -> AbilityRegistry.ABILITIES.getKey(ability).orElseThrow());
    }
}
