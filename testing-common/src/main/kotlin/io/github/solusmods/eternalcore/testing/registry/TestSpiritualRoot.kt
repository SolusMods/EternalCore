package io.github.solusmods.eternalcore.testing.registry

import io.github.solusmods.eternalcore.element.api.Element
import io.github.solusmods.eternalcore.spiritual_root.api.RootType
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRoot
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootInstance
import net.minecraft.world.entity.LivingEntity

class TestSpiritualRoot: SpiritualRoot(type = RootType.DARKNESS) {
    override fun getElement(
        instance: SpiritualRootInstance,
        entity: LivingEntity
    ): Element? {
        TODO("Not yet implemented")
    }

    override fun getFirstDegree(
        instance: SpiritualRootInstance,
        living: LivingEntity
    ): SpiritualRoot? {
        TODO("Not yet implemented")
    }

    override fun getPreviousDegree(
        spiritualRootInstance: SpiritualRootInstance,
        living: LivingEntity
    ): SpiritualRoot? {
        TODO("Not yet implemented")
    }
}