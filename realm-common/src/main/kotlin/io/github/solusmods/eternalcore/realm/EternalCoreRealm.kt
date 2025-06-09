package io.github.solusmods.eternalcore.realm

import io.github.solusmods.eternalcore.realm.impl.RealmRegistry
import io.github.solusmods.eternalcore.realm.impl.RealmStorage
import io.github.solusmods.eternalcore.realm.impl.network.RealmNetwork
import net.minecraft.resources.ResourceLocation

object EternalCoreRealm {
    fun create(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, path)
    }

    @JvmStatic
    fun init() {
        RealmStorage.Companion.init()
        RealmRegistry.init()
        RealmNetwork.init()
    }
}
