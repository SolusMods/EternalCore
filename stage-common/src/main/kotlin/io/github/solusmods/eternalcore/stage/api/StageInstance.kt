package io.github.solusmods.eternalcore.stage.api

import dev.architectury.registry.registries.RegistrySupplier
import io.github.solusmods.eternalcore.network.api.util.Changeable
import lombok.Getter
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import org.jetbrains.annotations.ApiStatus
import java.util.*

open class StageInstance(stage: Stage?): Cloneable {
    protected val stageRegistrySupplier: RegistrySupplier<Stage?> = StageAPI.stageRegistry!!.delegate(StageAPI.stageRegistry!!.getId(stage))
    private var tag: CompoundTag? = null

    @Getter
    private var dirty = false

    val stage: Stage?
        /**
         * Used to get the [Stage] type of this Instance.
         */
        get() = stageRegistrySupplier.get()

    val stageId: ResourceLocation?
        get() = this.stageRegistrySupplier.id

    val type: Stage.Type?
        /**
         * Used to get the type of this [Stage].
         */
        get() = this.stage!!.type

    /**
     * Used to create an exact copy of the current instance.
     */
    open fun copy(): StageInstance {
        val clone = StageInstance(this.stage)
        clone.dirty = this.dirty
        if (this.tag != null) clone.tag = this.tag!!.copy()
        return clone
    }

    /**
     * This method is used to ensure that all required information are stored.
     *
     *
     * Override [StageInstance.serialize] to store your custom Data.
     */
    open fun toNBT(): CompoundTag {
        val nbt = CompoundTag()
        nbt.putString(STAGE_KEY, this.stageId.toString())
        serialize(nbt)
        return nbt
    }

    /**
     * Can be used to save custom data.
     *
     * @param nbt Tag with data from [StageInstance.fromNBT]
     */
    open fun serialize(nbt: CompoundTag): CompoundTag {
        if (this.tag != null) nbt.put("tag", this.tag!!.copy())
        return nbt
    }

    /**
     * Can be used to load custom data.
     */
    open fun deserialize(tag: CompoundTag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag")
    }

    /**
     * Marks the current instance as dirty.
     */
    fun markDirty() {
        this.dirty = true
    }

    /**
     * This Method is invoked to indicate that a [StageInstance] has been synced with the clients.
     *
     *
     * Do **NOT** use that method on your own!
     */
    @ApiStatus.Internal
    fun resetDirty() {
        this.dirty = false
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val instance = o as StageInstance
        return this.stageId == instance.stageId &&
                stageRegistrySupplier.getRegistryKey() == instance.stageRegistrySupplier.getRegistryKey()
    }

    override fun hashCode(): Int {
        return Objects.hash(this.stageId, stageRegistrySupplier.getRegistryKey())
    }

    fun `is`(tag: TagKey<Stage?>?): Boolean {
        return this.stageRegistrySupplier.`is`(tag)
    }

    val displayName: MutableComponent?
        /**
         * Used to get the [MutableComponent] name of this stage for translation.
         */
        get() = this.stage!!.name

    fun getChatDisplayName(withDescription: Boolean): MutableComponent {
        var style = Style.EMPTY.withColor(ChatFormatting.GRAY)
        if (withDescription) {
            val hoverMessage = this.displayName!!.append("\n")
            hoverMessage.append(this.stage!!.name!!.withStyle(ChatFormatting.GRAY))
            style = style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage))
        }

        val component = Component.literal("[").append(
            this.displayName
        ).append("]")
        return component.withStyle(style)
    }

    val baseQiRange: Pair<Float?, Float?>?
        /**
         * Return [Pair] of min and max Qi for this [StageInstance]
         */
        get() = this.stage!!.baseQiRange

    val minBaseQi: Float
        /**
         * Return min Qi from [.getBaseQiRange] for this [StageInstance]
         */
        get() = this.stage!!.minBaseQi

    val maxBaseQi: Float
        /**
         * Return max Qi from [.getBaseQiRange] for this [StageInstance]
         */
        get() = this.stage!!.maxBaseQi

    /**
     * Return stage effect
     *
     * @param entity Affected [LivingEntity] for this stage
     */
    fun getEffect(entity: LivingEntity?): Changeable<MobEffectInstance?>? {
        return this.stage!!.getEffect(this, entity)
    }

    /**
     * Returns a list of all [Stage] that this Stage can break through into.
     *
     *
     * @param entity Affected [LivingEntity] breakthrough this stage.
     */
    fun getNextBreakthroughs(entity: LivingEntity?): MutableList<Stage?>? {
        return this.stage!!.getNextBreakthroughs(this, entity)
    }

    /**
     * Returns a list of all [Stage] that breakthrough into this Stage.
     *
     *
     * @param living Affected [LivingEntity] being this stage.
     */
    fun getPreviousBreakthroughs(living: LivingEntity?): MutableList<Stage?>? {
        return this.stage!!.getPreviousBreakthroughs(this, living)
    }

    /**
     * Returns the default [Stage] that this Stage breakthrough into.
     *
     *
     * @param living Affected [LivingEntity] evolving this stage.
     */
    fun getDefaultBreakthrough(living: LivingEntity?): Stage? {
        return this.stage!!.getDefaultBreakthrough(this, living)
    }

    fun getInfo(living: LivingEntity?): MutableList<MutableComponent?>? {
        return this.stage!!.getInfo(this, living)
    }

    /**
     * Called when the [LivingEntity] sets to this Stage.
     *
     * @param living Affected [LivingEntity] sets to this Stage.
     */
    fun onSet(living: LivingEntity?) {
        this.stage!!.onSet(this, living)
    }

    /**
     * Called when the [LivingEntity] reach this Stage.
     *
     * @param living Affected [LivingEntity] reach this Stage.
     */
    fun onReach(living: LivingEntity?) {
        this.stage!!.onReach(this, living)
    }

    /**
     * Called when the [LivingEntity] track on GUI this Stage.
     *
     * @param living Affected [LivingEntity] track on GUI this Stage.
     */
    fun onTrack(living: LivingEntity?) {
        this.stage!!.onTrack(this, living)
    }

    /**
     * Called every tick for this Stage.
     *
     * @param living Affected [LivingEntity] track on GUI this Stage.
     */
    fun onTick(living: LivingEntity) {
        this.stage!!.onTick(this, living)
    }

    /**
     * Called when the [LivingEntity] breakthrough this Stage.
     *
     * @param entity Affected [LivingEntity] breakthrough this Stage.
     */
    fun onBreakthrough(entity: LivingEntity?) {
        this.stage!!.onBreakthrough(this, entity)
    }

    companion object {
        const val STAGE_KEY: String = "stage"

        /**
         * Can be used to load a [StageInstance] from a [CompoundTag].
         *
         *
         * The [CompoundTag] has to be created though [StageInstance.toNBT]
         */
        @Throws(NullPointerException::class)
        @JvmStatic
        fun fromNBT(tag: CompoundTag): StageInstance {
            val location = ResourceLocation.tryParse(tag.getString(STAGE_KEY))
            val stage = StageAPI.stageRegistry.get(location) ?: throw NullPointerException("No stage found for location: $location")
            val instance = stage.createDefaultInstance()
            instance.deserialize(tag)
            return instance
        }
    }
}