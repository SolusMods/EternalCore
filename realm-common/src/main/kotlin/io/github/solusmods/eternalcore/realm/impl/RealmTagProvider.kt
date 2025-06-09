package io.github.solusmods.eternalcore.realm.impl

import io.github.solusmods.eternalcore.realm.api.Realm
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.IntrinsicHolderTagsProvider
import java.util.concurrent.CompletableFuture
import java.util.function.Function

abstract class RealmTagProvider : IntrinsicHolderTagsProvider<Realm?> {
    constructor(packOutput: PackOutput, completableFuture: CompletableFuture<HolderLookup.Provider?>?) : super(
        packOutput,
        RealmRegistry.KEY,
        completableFuture,
        Function { realm: Realm? -> RealmRegistry.REALMS.getKey(realm).orElseThrow() })

    constructor(
        output: PackOutput,
        lookupProvider: CompletableFuture<HolderLookup.Provider?>?,
        parentProvider: CompletableFuture<TagLookup<Realm?>?>?
    ) : super(
        output,
        RealmRegistry.KEY,
        lookupProvider,
        parentProvider,
        Function { realm: Realm? -> RealmRegistry.REALMS.getKey(realm).orElseThrow() })
}
