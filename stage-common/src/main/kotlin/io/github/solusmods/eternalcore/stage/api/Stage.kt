package io.github.solusmods.eternalcore.stage.api

import io.github.solusmods.eternalcore.network.api.util.Changeable
import io.github.solusmods.eternalcore.stage.ModuleConstants
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.RequiredArgsConstructor
import lombok.Setter
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import java.awt.Color

abstract class Stage(val type: Type?) {

    /**
     * Return [Pair] of min and max Qi for this [Stage]
     *
     * @see StageInstance.getBaseQiRange
     */
    abstract val baseQiRange: Pair<Float?, Float?>?

    val minBaseQi: Float
        /**
         * Return min Qi from [Stage.getBaseQiRange] for this [Stage]
         *
         * @see StageInstance.getMinBaseQi
         */
        get() = this.baseQiRange!!.first!!

    val maxBaseQi: Float
        /**
         * Return max Qi from [Stage.getBaseQiRange] for this [Stage]
         *
         * @see StageInstance.getMaxBaseQi
         */
        get() = this.baseQiRange!!.second!!

    open fun getEffect(instance: StageInstance?, living: LivingEntity?): Changeable<MobEffectInstance?> {
        return Changeable.of(null)
    }

    /**
     * Returns a list of all [Stage] that this Stage can break through into.
     *
     * @see StageInstance.getNextBreakthroughs
     */
    abstract fun getNextBreakthroughs(instance: StageInstance?, living: LivingEntity?): MutableList<Stage?>?

    /**
     * Returns a list of all [Stage] that break through into this Stage.
     *
     * @see StageInstance.getPreviousBreakthroughs
     */
    abstract fun getPreviousBreakthroughs(instance: StageInstance?, living: LivingEntity?): MutableList<Stage?>?

    open fun getInfo(instance: StageInstance?, entity: LivingEntity?): MutableList<MutableComponent?> {
        val info: MutableList<MutableComponent?> = ArrayList<MutableComponent?>()
        return info
    }

    /**
     * Returns the default [Stage] that this Stage break through into.
     *
     * @see StageInstance.getDefaultBreakthrough
     */
    abstract fun getDefaultBreakthrough(instance: StageInstance?, living: LivingEntity?): Stage?

    val registryName: ResourceLocation?
        /**
         * Used to get the [ResourceLocation] id of this stage.
         */
        get() = StageAPI.stageRegistry!!.getId(this)

    val name: MutableComponent?
        /**
         * Used to get the [MutableComponent] name of this stage for translation.
         */
        get() {
            val id = this.registryName
            return if (id == null) null else Component.translatable(
                String.format(
                    "%s.realm.stage.%s",
                    id.namespace,
                    id.path.replace('/', '.')
                )
            )
        }

    val nameTranslationKey: String
        get() = (this.name!!.contents as TranslatableContents).key

    /**
     * Called when the [LivingEntity] sets to this Stage.
     *
     * @see StageInstance.onSet
     */
    open fun onSet(instance: StageInstance?, living: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] reach to this Stage.
     *
     * @see StageInstance.onReach
     */
    open fun onReach(instance: StageInstance?, living: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] track to this Stage.
     *
     * @see StageInstance.onTrack
     */
    open fun onTrack(instance: StageInstance?, living: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Called every tick for this [Stage].
     *
     * @see StageInstance.onTick
     */
    open fun onTick(instance: StageInstance, living: LivingEntity) {
        StageEvents.Companion.STAGE_POST_TICK.invoker().tick(instance, living)
    }

    /**
     * Called when the [LivingEntity]
     * <br></br>
     * Breakthrough to next [Stage.getDefaultBreakthrough].
     *
     * @see StageInstance.onBreakthrough
     */
    open fun onBreakthrough(instance: StageInstance?, living: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Used to create a [StageInstance] of this Stage.
     *
     *
     * Override this Method to use your extended version of [StageInstance]
     */
    open fun createDefaultInstance(): StageInstance {
        return StageInstance(this)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        } else if (o != null && this.javaClass == o.javaClass) {
            val stage = o as Stage
            return this.registryName == stage.registryName
        } else {
            return false
        }
    }

    enum class Type(val id: Int, val min: Int, val max: Int, val component: MutableComponent, val color: Color) {
        EARLY(1, 0, 1, Component.translatable("%s.stage.type.early".format(ModuleConstants.MOD_ID)), Color.GREEN),
        MIDDLE(2, 1, 2, Component.translatable("%s.stage.type.middle".format(ModuleConstants.MOD_ID)), Color.YELLOW),
        LATE(3, 2, 3, Component.translatable("%s.stage.type.late".format(ModuleConstants.MOD_ID)), Color.ORANGE),
        PEAK(4, 3, 4, Component.translatable("%s.stage.type.peak".format(ModuleConstants.MOD_ID)), Color.RED);
    }
}