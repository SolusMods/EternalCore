package io.github.solusmods.eternalcore.testing.registry

import io.github.solusmods.eternalcore.stage.api.Stage
import io.github.solusmods.eternalcore.stage.api.StageInstance
import io.github.solusmods.eternalcore.testing.EternalCoreTesting
import net.minecraft.world.entity.LivingEntity

class TestStage(override val baseQiRange: Pair<Float?, Float?>? = Pair(0.0F, 0.0F)) : Stage(Type.EARLY) {
    override fun getNextBreakthroughs(
        instance: StageInstance?,
        living: LivingEntity?
    ): MutableList<Stage?>? {
        TODO("Not yet implemented")
    }

    override fun getPreviousBreakthroughs(
        instance: StageInstance?,
        living: LivingEntity?
    ): MutableList<Stage?>? {
        TODO("Not yet implemented")
    }

    override fun getDefaultBreakthrough(
        instance: StageInstance?,
        living: LivingEntity?
    ): Stage? {
        TODO("Not yet implemented")
    }

    override fun onTick(instance: StageInstance, living: LivingEntity) {
        EternalCoreTesting.LOG.debug("Stage Tick")
    }
}