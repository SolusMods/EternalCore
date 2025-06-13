package io.github.solusmods.eternalcore.testing.registry

import io.github.solusmods.eternalcore.attributes.EternalCoreAttributeRegister
import io.github.solusmods.eternalcore.testing.ModuleConstants
import net.minecraft.world.entity.ai.attributes.Attribute

object TestAttributeRegistry {
    @JvmStatic
    val TEST_ATTRIBUTE_PLAYER = EternalCoreAttributeRegister.registerPlayerAttribute(ModuleConstants.MOD_ID,
        "test_attribute_player", "eternalcore.attribute.test_attribute_player",69.0, 0.0, 420.0, true, Attribute.Sentiment.NEUTRAL)
    @JvmStatic
    fun init(){}
}