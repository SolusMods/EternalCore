package io.github.solusmods.eternalcore.attributes

import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributes
import io.github.solusmods.eternalcore.attributes.impl.network.EternalCoreAttributeNetwork

object EternalCoreAttribute {
    @JvmStatic
    fun init() {
        EternalCoreAttributeNetwork.init()
        EternalCoreAttributes.init()
        EternalCoreAttributeRegister.init()
    }
}