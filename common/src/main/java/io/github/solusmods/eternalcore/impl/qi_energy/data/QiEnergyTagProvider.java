package io.github.solusmods.eternalcore.impl.qi_energy.data;

import io.github.solusmods.eternalcore.api.qi_energy.ElementType;
import io.github.solusmods.eternalcore.api.registry.ElementTypeRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Базовий провайдер тегів для елементів EternalCore.
 */
public abstract class QiEnergyTagProvider extends IntrinsicHolderTagsProvider<ElementType> {

    /**
     * Конструктор без parentProvider.
     *
     * @param output         Вихідний каталог для ресурсів
     * @param lookupProvider Асинхронний постачальник HolderLookup
     */
    public QiEnergyTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, ElementTypeRegistry.KEY, lookupProvider,
                (qiEnergy) -> ElementTypeRegistry.ELEMENT_TYPES.getKey(qiEnergy).orElseThrow());
    }

    /**
     * Конструктор із parentProvider.
     *
     * @param output         Вихідний каталог для ресурсів
     * @param lookupProvider Асинхронний постачальник HolderLookup
     * @param parentProvider Опціональний батьківський провайдер тегів
     */
    public QiEnergyTagProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagLookup<ElementType>> parentProvider) {
        super(output, ElementTypeRegistry.KEY, lookupProvider, parentProvider,
                (qiEnergy) -> ElementTypeRegistry.ELEMENT_TYPES.getKey(qiEnergy).orElseThrow());
    }
}
