package io.github.solusmods.eternalcore.element.impl.data;

import io.github.solusmods.eternalcore.element.api.Element;
import io.github.solusmods.eternalcore.element.impl.ElementRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;

import java.util.concurrent.CompletableFuture;

public abstract class ElementTagProvider extends IntrinsicHolderTagsProvider<Element> {
    public ElementTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, ElementRegistry.KEY, lookupProvider, element -> ElementRegistry.ELEMENTS.getKey(element).orElseThrow());
    }

    public ElementTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Element>> parentProvider) {
        super(output, ElementRegistry.KEY, lookupProvider, parentProvider, element -> ElementRegistry.ELEMENTS.getKey(element).orElseThrow());
    }
}
