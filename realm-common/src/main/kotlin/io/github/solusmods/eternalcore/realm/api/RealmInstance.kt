package io.github.solusmods.eternalcore.realm.api

import dev.architectury.registry.registries.RegistrySupplier
import io.github.solusmods.eternalcore.stage.api.Stage
import lombok.Getter
import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.jetbrains.annotations.ApiStatus
import java.util.*

open class RealmInstance(realm: Realm?) : Cloneable {
    protected val realmRegistrySupplier: RegistrySupplier<Realm?> = RealmAPI.realmRegistry!!.delegate(RealmAPI.realmRegistry!!.getId(realm))
    private var tag: CompoundTag? = null

    @Getter
    private var dirty = false

    val realm: Realm?
        /**
         * Used to get the [Realm] type of this Instance.
         */
        get() = realmRegistrySupplier.get()

    val realmId: ResourceLocation?
        get() = this.realmRegistrySupplier.getId()

    val rType: Type?
        /**
         * Used to get the type of this [RealmInstance].
         */
        get() = this.realm!!.type

    /**
     * Used to create an exact copy of the current instance.
     */
    fun copy(): RealmInstance {
        val clone = RealmInstance(this.realm)
        clone.dirty = this.dirty
        if (this.tag != null) clone.tag = this.tag!!.copy()
        return clone
    }

    /**
     * This method is used to ensure that all required information are stored.
     *
     *
     * Override [RealmInstance.serialize] to store your custom Data.
     */
    fun toNBT(): CompoundTag {
        val nbt = CompoundTag()
        nbt.putString(REALM_KEY, this.realmId.toString())
        serialize(nbt)
        return nbt
    }

    /**
     * Can be used to save custom data.
     *
     * @param nbt Tag with data from [RealmInstance.fromNBT]
     */
    fun serialize(nbt: CompoundTag): CompoundTag {
        if (this.tag != null) nbt.put("tag", this.tag!!.copy())
        return nbt
    }

    /**
     * Can be used to load custom data.
     */
    fun deserialize(tag: CompoundTag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag")
    }

    /**
     * Marks the current instance as dirty.
     */
    fun markDirty() {
        this.dirty = true
    }

    /**
     * This Method is invoked to indicate that a [RealmInstance] has been synced with the clients.
     *
     *
     * Do **NOT** use that method on our own!
     */
    @ApiStatus.Internal
    fun resetDirty() {
        this.dirty = false
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val instance = o as RealmInstance
        return this.realmId == instance.realmId &&
                realmRegistrySupplier.getRegistryKey() == instance.realmRegistrySupplier.getRegistryKey()
    }

    override fun hashCode(): Int {
        return Objects.hash(this.realmId, realmRegistrySupplier.getRegistryKey())
    }

    fun `is`(tag: TagKey<Realm?>?): Boolean {
        return this.realmRegistrySupplier.`is`(tag)
    }

    val displayName: MutableComponent?
        /**
         * Used to get the [MutableComponent] name of this spiritual_root for translation.
         */
        get() = this.realm!!.name

    fun getChatDisplayName(withDescription: Boolean): MutableComponent {
        var style = Style.EMPTY.withColor(ChatFormatting.GRAY)
        if (withDescription) {
            val hoverMessage = this.displayName?.append("\n")
            hoverMessage?.append(this.realm!!.name!!.withStyle(ChatFormatting.GRAY))
            style = style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage))
        }

        val component = Component.literal("[").append(
            this.displayName
        ).append("]")
        return component.withStyle(style)
    }

    val trackedName: MutableComponent?
        get() = this.realm!!.trackedName

    val baseHealth: Double
        /**
         * Return base health for this [RealmInstance]
         */
        get() = this.realm!!.baseHealth

    val baseQiRange: Pair<Float?, Float?>?
        /**
         * Return [Pair] of min and max Qi for this [RealmInstance]
         */
        get() = this.realm!!.baseQiRange

    val baseAttackDamage: Double
        /**
         * Return base attack damage for this [RealmInstance]
         */
        get() = this.realm!!.baseAttackDamage

    val baseAttackSpeed: Double
        /**
         * Return base attack speed for this [RealmInstance]
         */
        get() = this.realm!!.baseAttackSpeed

    val knockBackResistance: Double
        /**
         * Return knock back Resistance for this [RealmInstance]
         */
        get() = this.realm!!.knockBackResistance

    val jumpHeight: Double
        /**
         * Return jump height for this [RealmInstance]
         */
        get() = this.realm!!.jumpHeight

    val movementSpeed: Double
        /**
         * Return movement speed for this [RealmInstance]
         */
        get() = this.realm!!.movementSpeed

    val sprintSpeed: Double
        /**
         * Return sprint speed for this [RealmInstance]
         */
        get() = this.realm!!.sprintSpeed

    val minBaseQi: Float
        /**
         * Return min Qi from [.getBaseQiRange] for this [RealmInstance]
         */
        get() = this.realm!!.minBaseQi

    val maxBaseQi: Float
        /**
         * Return max Qi from [.getBaseQiRange] for this [RealmInstance]
         */
        get() = this.realm!!.maxBaseQi

    val coefficient: Double
        get() = this.realm!!.coefficient

    /**
     * Returns a list of all [Realm] that this Realm can break through into.
     *
     *
     * @param living Affected [LivingEntity] breakthrough this spiritual_root.
     */
    fun getNextBreakthroughs(living: LivingEntity?): MutableList<Realm?>? {
        return this.realm!!.getNextBreakthroughs(this, living)
    }

    /**
     * Returns a list of all [Realm] that breakthrough into this Realm.
     *
     *
     * @param living Affected [LivingEntity] being this spiritual_root.
     */
    fun getPreviousBreakthroughs(living: LivingEntity?): MutableList<Realm?>? {
        return this.realm!!.getPreviousBreakthroughs(this, living)
    }

    /**
     * Returns the default [Realm] that this Realm breakthrough into.
     *
     *
     * @param living Affected [LivingEntity] breakthrough this spiritual_root.
     */
    fun getDefaultBreakthrough(living: LivingEntity?): Realm? {
        return this.realm!!.getDefaultBreakthrough(this, living)
    }

    /**
     * Returns a [List] of all [Stage]s for this [Realm].
     *
     *
     * @param living Affected [LivingEntity] being this spiritual_root.
     */
    fun getRealmStages(living: LivingEntity?): MutableList<Stage?>? {
        return this.realm!!.getRealmStages(this, living)
    }

    /**
     * Called when the [LivingEntity] sets to this spiritual_root.
     *
     * @param living Affected [LivingEntity] sets to this spiritual_root.
     */
    fun onSet(living: LivingEntity?) {
        this.realm!!.onSet(this, living)
    }

    /**
     * Called when the [LivingEntity] reach this Realm.
     *
     * @param living Affected [LivingEntity] reach this Realm.
     */
    fun onReach(living: LivingEntity?) {
        this.realm!!.onReach(this, living)
    }

    /**
     * Called when the [LivingEntity] track on GUI this Realm.
     *
     * @param living Affected [LivingEntity] track on GUI this Realm.
     */
    fun onTrack(living: LivingEntity?) {
        this.realm!!.onTrack(this, living)
    }

    /**
     * Called when the [LivingEntity] breakthrough this Realm.
     *
     * @param entity Affected [LivingEntity] breakthrough this Realm.
     */
    fun onBreakthrough(entity: LivingEntity?) {
        this.realm!!.onBreakthrough(this, entity)
    }

    /**
     * Applies the attribute modifiers of this instance on the [LivingEntity] when set.
     *
     * @param entity Affected [LivingEntity] being thisrealm.
     */
    fun addAttributeModifiers(entity: LivingEntity, i: Int) {
        this.realm!!.addAttributeModifiers(this, entity, i)
    }

    /**
     * Removes the attribute modifiers of this instance from the [LivingEntity] when changing spiritual_root.
     *
     * @param entity Affected [LivingEntity] being this spiritual_root.
     */
    fun removeAttributeModifiers(entity: LivingEntity) {
        this.realm!!.removeAttributeModifiers(this, entity)
    }

    fun createModifiers(entity: LivingEntity) {
        this.realm!!.createModifiers(this, 0, (java.util.function.BiConsumer { attributeHolder: Holder<Attribute?>?, attributeModifier: AttributeModifier? ->
            val instance = entity.getAttribute(attributeHolder)
            instance?.addOrReplacePermanentModifier(attributeModifier)
        }))
    }

    /**
     * Returns the dimension that [LivingEntity] respawns at as this Realm.
     * Decides whether if the game should spawn a 3x3 platform of [BlockState] when no valid spawn is found.
     *
     *
     * @param player Affected [LivingEntity] being this spiritual_root.
     */
    fun getRespawnDimension(player: LivingEntity?): com.mojang.datafixers.util.Pair<ResourceKey<Level?>?, BlockState?>? {
        return this.realm!!.getRespawnDimension(this, player)
    }

    fun passivelyFriendlyWith(entity: LivingEntity?): Boolean {
        return this.realm!!.passivelyFriendlyWith(this, entity)
    }

    fun canFly(entity: LivingEntity?): Boolean {
        return this.realm!!.canFly(this, entity)
    }

    /**
     * Called every tick if this instance is set for [LivingEntity].
     *
     * @param living Affected [LivingEntity] being this Realm.
     */
    fun onTick(living: LivingEntity?) {
        this.realm!!.onTick(this, living)
    }


    public override fun clone(): RealmInstance {
        try {
            val clone = super.clone() as RealmInstance
            clone.dirty = this.dirty
            if (this.tag != null) clone.tag = this.tag!!.copy()
            return clone
        } catch (e: CloneNotSupportedException) {
            throw AssertionError()
        }
    }

    companion object {
        const val REALM_KEY: String = "stage"

        /**
         * Can be used to load a [RealmInstance] from a [CompoundTag].
         *
         *
         * The [CompoundTag] has to be created though [RealmInstance.toNBT]
         */
        @Throws(NullPointerException::class)
        fun fromNBT(tag: CompoundTag?): RealmInstance {
            val location = ResourceLocation.tryParse(tag!!.getString(REALM_KEY))
            val realm = RealmAPI.realmRegistry!!.get(location)
            if (realm == null) throw NullPointerException("No realm found for location: " + location)
            val instance = realm.createDefaultInstance()
            instance.deserialize(tag)
            return instance
        }
    }
}
