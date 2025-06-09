package io.github.solusmods.eternalcore.stage.api

import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

interface IReachedStages {
    val reachedStages: MutableMap<ResourceLocation?, StageInstance>


    fun addStage(stageId: ResourceLocation, notify: Boolean?, component: MutableComponent? = null): Boolean {
        val stage = StageAPI.stageRegistry!!.get(stageId)
        if (stage == null) return false
        return addStage(stage.createDefaultInstance(), false, notify!!, component)
    }


    fun addStage(stage: Stage, notify: Boolean?, component: MutableComponent? = null): Boolean {
        return addStage(stage.createDefaultInstance(), false, notify!!, component)
    }

    fun addStage(instance: StageInstance, breakthrough: Boolean?, notify: Boolean): Boolean {
        return addStage(instance, breakthrough, notify, null)
    }

    fun addStage(
        instance: StageInstance,
        breakthrough: Boolean?,
        notify: Boolean?,
        component: MutableComponent?
    ): Boolean

    fun markDirty()

    fun sync()
}
