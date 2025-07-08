package io.github.solusmods.eternalcore.impl.realm.data;

import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.registry.RealmRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;

import java.util.concurrent.CompletableFuture;

public abstract class RealmTagProvider extends IntrinsicHolderTagsProvider<AbstractRealm> {
    public RealmTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(packOutput, RealmRegistry.KEY, completableFuture, realm -> RealmRegistry.REALMS.getKey(realm).orElseThrow());
    }

    public RealmTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<AbstractRealm>> parentProvider) {
        super(output, RealmRegistry.KEY, lookupProvider, parentProvider, realm -> RealmRegistry.REALMS.getKey(realm).orElseThrow());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
