package io.github.solusmods.eternalcore.attributes.neoforge

import io.github.solusmods.eternalcore.attributes.EternalCoreAttribute.init
import io.github.solusmods.eternalcore.attributes.ModuleConstants
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.attributes.Attribute
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(ModuleConstants.MOD_ID)
object EternalCoreAttributeNeoForge {

    val attributes: DeferredRegister<Attribute> =
        DeferredRegister.create(Registries.ATTRIBUTE, ModuleConstants.MOD_ID)

    init {
        init()
        attributes.register(MOD_BUS)
    }
}