package io.github.solusmods.eternalcore.realm.impl.data;

import io.github.solusmods.eternalcore.realm.api.Realm;
import io.github.solusmods.eternalcore.realm.impl.RealmRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;

import java.util.concurrent.CompletableFuture;

public abstract class RealmTagProvider extends IntrinsicHolderTagsProvider<Realm> {
    public RealmTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, RealmRegistry.KEY, lookupProvider, realm -> RealmRegistry.REALMS.getKey(realm).orElseThrow());
    }

    public RealmTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Realm>> parentProvider) {
        super(output, RealmRegistry.KEY, lookupProvider, parentProvider, realm -> RealmRegistry.REALMS.getKey(realm).orElseThrow());
    }
}
