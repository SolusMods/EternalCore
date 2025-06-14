package io.github.solusmods.eternalcore.stage.api

import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import java.util.*

interface Stages {
    val stage: StageInstance?

    fun getStageOptional(): Optional<StageInstance>

    fun setStage(stageId: ResourceLocation, notify: Boolean): Boolean {
        return setStage(stageId, notify, null)
    }

    fun setStage(stageId: ResourceLocation, notify: Boolean, component: MutableComponent?): Boolean {
        val stage = StageAPI.stageRegistry.get(stageId)
        if (stage == null) return false
        return setStage(stage.createDefaultInstance(), false, notify, component)
    }

    fun setStage(stage: Stage, notify: Boolean?): Boolean {
        return setStage(stage, notify, null)
    }

    fun setStage(stage: Stage, notify: Boolean?, component: MutableComponent?): Boolean {
        return setStage(stage.createDefaultInstance(), false, notify, component)
    }

    fun setStage(stageInstance: StageInstance, advancement: Boolean?, notify: Boolean?): Boolean {
        return setStage(stageInstance, advancement, notify, null)
    }

    fun setStage(stageInstance: StageInstance, advancement: Boolean?, notify: Boolean?, component: MutableComponent?): Boolean

    fun breakthroughStage(stageId: ResourceLocation): Boolean {
        return breakthroughStage(stageId, null)
    }

    fun breakthroughStage(stageId: ResourceLocation, component: MutableComponent?): Boolean {
        val stage = StageAPI.stageRegistry!!.get(stageId)
        if (stage == null) return false
        return setStage(stage.createDefaultInstance(), advancement = true, notify = false)
    }

    fun breakthroughStage(stage: Stage, component: MutableComponent? = null): Boolean {
        return setStage(stage.createDefaultInstance(), advancement = true, notify = false, component = component)
    }

    fun breakthroughStage(stageInstance: StageInstance, component: MutableComponent? = null): Boolean {
        return setStage(stageInstance, advancement = true, notify = false, component = component)
    }

    fun markDirty()

    fun sync()
}
