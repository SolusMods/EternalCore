package io.github.solusmods.eternalcore.abilities

import dev.architectury.platform.Platform
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.abilities.impl.AbilityRegistry
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage
import io.github.solusmods.eternalcore.abilities.impl.network.EternalCoreAbilityNetwork
import net.minecraft.resources.ResourceLocation

object EternalCoreAbilities {
    fun create(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, path)
    }

    @JvmStatic
    fun init() {
        AbilityRegistry.init()
        AbilityStorage.Companion.init()
        EternalCoreAbilityNetwork.init()
        if (Platform.getEnvironment() == Env.CLIENT) {
            EternalCoreAbilitiesClient.init()
        }
    }
}
