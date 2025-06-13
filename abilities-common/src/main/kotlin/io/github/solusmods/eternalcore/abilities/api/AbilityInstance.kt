package io.github.solusmods.eternalcore.abilities.api

import dev.architectury.registry.registries.RegistrySupplier
import io.github.solusmods.eternalcore.entity.api.ProjectileHitResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import net.minecraft.core.Holder
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ProjectileDeflection
import net.minecraft.world.phys.EntityHitResult
import org.jetbrains.annotations.ApiStatus
import java.lang.Integer.max
import java.util.*
import java.util.stream.Collectors
import kotlin.jvm.javaClass

open class AbilityInstance(ability: Ability) {
    private var removeTime = -1
    private var masteryPoint = 0.0
    private var toggled = false
    private var cooldownList: MutableList<Int?>
    private var tag: CompoundTag? = null

    var dirty = false
    protected val abilityRegistrySupplier: RegistrySupplier<Ability?> = AbilityAPI.abilityRegistry!!.delegate(AbilityAPI.abilityRegistry!!.getId(ability))

    init {
        cooldownList = NonNullList.withSize<Int?>(ability.modes, 0)
    }

    val ability: Ability?
        /**
         * Used to get the [Ability] type of this Instance.
         */
        get() = abilityRegistrySupplier.get()

    val abilityId: ResourceLocation?
        get() = this.abilityRegistrySupplier.getId()

    /**
     * Used to create an exact copy of the current instance.
     */
    open fun copy(): AbilityInstance {
        val clone = AbilityInstance(this.ability!!)
        clone.dirty = this.dirty
        clone.cooldownList = ArrayList<Int?>(this.cooldownList)
        clone.removeTime = this.removeTime
        clone.masteryPoint = this.masteryPoint
        clone.toggled = this.toggled
        if (this.tag != null) clone.tag = this.tag!!.copy()
        return clone
    }

    /**
     * This method is used to ensure that all required information are stored.
     *
     *
     * Override [AbilityInstance.serialize] to store your custom Data.
     */
    open fun toNBT(): CompoundTag {
        val nbt = CompoundTag()
        nbt.putString("ability", this.abilityId.toString())
        serialize(nbt)
        return nbt
    }

    /**
     * Can be used to save custom data.
     *
     * @param nbt Tag with data from [AbilityInstance.fromNBT]
     */
    open fun serialize(nbt: CompoundTag): CompoundTag {
        nbt.putInt(REMOVE_TIME_TAG, this.removeTime)
        nbt.putDouble(MASTERY_TAG, this.masteryPoint)
        nbt.putBoolean(TOGGLED_TAG, this.toggled)
        nbt.putIntArray(COOLDOWN_LIST_TAG, this.cooldownList)
        if (this.tag != null) nbt.put("tag", this.tag!!.copy())
        return nbt
    }

    /**
     * Can be used to load custom data.
     */
    open fun deserialize(tag: CompoundTag?) {
        this.removeTime = tag!!.getInt(REMOVE_TIME_TAG)
        this.masteryPoint = tag.getDouble(MASTERY_TAG)
        this.toggled = tag.getBoolean(TOGGLED_TAG)
        this.cooldownList = Arrays.stream(tag.getIntArray(COOLDOWN_LIST_TAG)).boxed().collect(Collectors.toList())
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag")
    }

    /**
     * Marks the current instance as dirty.
     */
    fun markDirty() {
        this.dirty = true
    }

    /**
     * This Method is invoked to indicate that a [AbilityInstance] has been synced with the clients.
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
        val instance = o as AbilityInstance
        return this.abilityId == instance.abilityId &&
                abilityRegistrySupplier.getRegistryKey() == instance.abilityRegistrySupplier.getRegistryKey()
    }

    override fun hashCode(): Int {
        return Objects.hash(this.abilityId, abilityRegistrySupplier.getRegistryKey())
    }

    /**
     * Determine if this instance can be used by [LivingEntity].
     *
     * @param user Affected [LivingEntity]
     * @return false will stop [LivingEntity] from using any feature of the ability.
     */
    fun canInteractAbility(user: LivingEntity?): Boolean {
        return this.ability!!.canInteractAbility(this, user)
    }

    /**
     * @return the maximum number of ticks that this ability can be held down with the ability activation button.
     */
    fun getMaxHeldTime(entity: LivingEntity?): Int {
        return this.ability!!.getMaxHeldTime(this, entity)
    }

    /**
     * Determine if the [Ability] type of this instance can be toggled.
     *
     * @param entity Affected [LivingEntity] owning this Ability.
     * @return false if this ability is not toggleable.
     */
    fun canBeToggled(entity: LivingEntity?): Boolean {
        return this.ability!!.canBeToggled(this, entity)
    }

    /**
     * Determine if the [Ability] type of this instance can still be activated when on cooldown.
     *
     * @param entity Affected [LivingEntity] owning this Ability.
     * @return false if this ability cannot ignore cooldown.
     */
    fun canIgnoreCoolDown(entity: LivingEntity?, mode: Int): Boolean {
        return this.ability!!.canIgnoreCoolDown(this, entity, mode)
    }

    /**
     * Determine if this instance's [AbilityInstance.onTick] can be executed.
     *
     * @param entity Affected [LivingEntity] owning this Ability.
     * @return false if this ability cannot tick.
     */
    open fun canTick(entity: LivingEntity?): Boolean {
        return this.ability!!.canTick(this, entity)
    }

    /**
     * Determine if this instance's [AbilityInstance.onScroll] can be executed.
     *
     * @param entity Affected [LivingEntity] owning this Ability.
     * @return false if this ability cannot be scrolled.
     */
    fun canScroll(entity: LivingEntity?): Boolean {
        return this.ability!!.canScroll(this, entity)
    }

    val modes: Int
        /**
         * @return the number of modes that this ability instance has.
         */
        get() = this.ability!!.modes

    val maxMastery: Int
        /**
         * @return the maximum mastery points that this ability instance can have.
         */
        get() = this.ability!!.maxMastery

    /**
     * Determine if the [Ability] type of this instance is mastered by [LivingEntity] owning it.
     *
     * @param entity Affected [LivingEntity] owning this Ability.
     */
    fun isMastered(entity: LivingEntity?): Boolean {
        return this.ability!!.isMastered(this, entity)
    }

    /**
     * Increase the mastery point of the [Ability] type of this instance.
     *
     * @param entity Affected [LivingEntity] owning this Ability.
     */
    fun addMasteryPoint(entity: LivingEntity?) {
        this.ability!!.addMasteryPoint(this, entity)
    }

    var mastery: Double
        /**
         * @return the mastery point of the [Ability] type of this instance.
         */
        get() = this.masteryPoint
        /**
         * Set the mastery point of the [Ability] type of this instance.
         */
        set(point) {
            this.masteryPoint = point
            markDirty()
        }

    /**
     * @return the cooldown of a specific mode of this instance.
     */
    fun getCoolDown(mode: Int): Int {
        if (mode < 0 || mode >= cooldownList.size) return 0
        return this.cooldownList.get(mode)!!
    }

    /**
     * @return if a specific mode of this instance is on cooldown.
     */
    fun onCoolDown(mode: Int): Boolean {
        if (mode < 0 || mode >= cooldownList.size) return false
        return this.cooldownList.get(mode)!! > 0
    }

    /**
     * Set the cooldown of a specific mode of this instance.
     */
    fun setCoolDown(coolDown: Int, mode: Int) {
        if (mode < 0 || mode >= cooldownList.size) return
        this.cooldownList.set(mode, coolDown)
        markDirty()
    }

    /**
     * Set the cooldown of every mode of this instance.
     */
    fun setCoolDowns(coolDown: Int) {
        Collections.fill<Int?>(this.cooldownList, coolDown)
        markDirty()
    }

    /**
     * Decrease the cooldown of a specific mode of this instance.
     */
    fun decreaseCoolDown(coolDown: Int, mode: Int) {
        if (mode < 0 || mode >= cooldownList.size) return
        this.cooldownList.set(mode, max(0, this.cooldownList.get(mode)!! - coolDown))
        markDirty()
    }

    /**
     * Edit the entire cooldown list of this instance.
     */
    fun setCoolDownList(list: MutableList<Int?>) {
        this.cooldownList = list
    }

    val isTemporaryAbility: Boolean
        /**
         * @return if this ability instance is temporary, which should be removed when its time runs out.
         */
        get() = this.removeTime != -1

    /**
     * @return the removal time of this instance.
     */
    fun getRemoveTime(): Int {
        return this.removeTime
    }

    /**
     * @return if this ability instance needs to be removed.
     */
    fun shouldRemove(): Boolean {
        return this.removeTime == 0
    }

    /**
     * Set the remove time of this instance.
     */
    fun setRemoveTime(removeTime: Int) {
        this.removeTime = removeTime
        markDirty()
    }

    /**
     * Decrease the remove time of this instance.
     */
    fun decreaseRemoveTime(time: Int) {
        if (this.removeTime > 0) {
            this.removeTime = max(0, this.removeTime - time)
            markDirty()
        }
    }

    /**
     * @return if this instance is toggled.
     */
    fun isToggled(): Boolean {
        return this.toggled
    }

    /**
     * Toggle on/off this instance.
     */
    fun setToggled(toggled: Boolean) {
        this.toggled = toggled
        markDirty()
    }

    /**
     * @return compound tag of this instance.
     */
    fun getTag(): CompoundTag? {
        return this.tag
    }

    val orCreateTag: CompoundTag?
        /**
         * Used to add/create additional tags for this instance.
         *
         * @return compound tag of this instance or create if null.
         */
        get() {
            if (this.tag == null) {
                this.setTag(CompoundTag())
                this.markDirty()
            }
            return this.tag
        }

    /**
     * Used to add/create additional tags for this instance.
     * Set the tag of this instance.
     */
    fun setTag(tag: CompoundTag?) {
        this.tag = tag
        markDirty()
    }

    /**
     * @return the amplifier for each attribute modifier that this instance applies.
     *
     * @param entity   Affected [LivingEntity] owning this Ability.
     * @param holder   Affected [&lt;Attribute&gt;][Holder] that this ability provides.
     * @param template Affected [AttributeTemplate] that this ability provides for an attribute.
     */
    fun getAttributeModifierAmplifier(
        entity: LivingEntity?,
        holder: Holder<Attribute?>?,
        template: AttributeTemplate?,
        mode: Int
    ): Double {
        return this.ability!!.getAttributeModifierAmplifier(this, entity, holder, template, mode)
    }

    /**
     * Applies the attribute modifiers of this instance on the [LivingEntity] holding the ability activation button.
     *
     * @param entity   Affected [LivingEntity] owning this Ability.
     */
    fun addHeldAttributeModifiers(entity: LivingEntity?, mode: Int) {
        this.ability!!.addHeldAttributeModifiers(this, entity!!, mode)
    }

    /**
     * Removes the attribute modifiers of this instance from the [LivingEntity] holding the ability activation button.
     *
     * @param entity   Affected [LivingEntity] owning this Ability.
     */
    fun removeAttributeModifiers(entity: LivingEntity?, mode: Int) {
        this.ability!!.removeAttributeModifiers(this, entity!!, mode)
    }

    /**
     * Called when the [LivingEntity] owning this Ability toggles this [Ability] type of this instance on.
     *
     * @param entity Affected [LivingEntity] owning this Ability.
     */
    fun onToggleOn(entity: LivingEntity?) {
        this.ability!!.onToggleOn(this, entity)
    }

    /**
     * Called when the [LivingEntity] owning this Ability toggles this [Ability] type of this instance off.
     *
     * @param entity Affected [LivingEntity] owning this instance.
     */
    fun onToggleOff(entity: LivingEntity?) {
        this.ability!!.onToggleOff(this, entity)
    }

    /**
     * Called every tick if this instance is obtained by [LivingEntity].
     *
     * @param living Affected [LivingEntity] owning this instance.
     */
    fun onTick(living: LivingEntity?) {
        this.ability!!.onTick(this, living)
    }

    /**
     * Called when the [LivingEntity] owning this Ability presses the ability activation button.
     *
     * @param entity    Affected [LivingEntity] owning this instance.
     * @param keyNumber The key number that was pressed.
     * @param mode      The mode that was activated.
     */
    fun onPressed(entity: LivingEntity?, keyNumber: Int, mode: Int) {
        this.ability!!.onPressed(this, entity, keyNumber, mode)
    }

    /**
     * Called when the [LivingEntity] owning this Ability holds the ability activation button.
     *
     * @param entity    Affected [LivingEntity] owning this instance.
     * @param heldTicks The number of ticks the ability activation button is being held down.
     * @param mode      The mode that is being held down.
     * @return true to continue ticking this instance.
     */
    fun onHeld(entity: LivingEntity?, heldTicks: Int, mode: Int): Boolean {
        return this.ability!!.onHeld(this, entity, heldTicks, mode)
    }

    /**
     * Called when the [LivingEntity] owning this Ability releases the ability activation button after {@param heldTicks}.
     *
     * @param entity    Affected [LivingEntity] owning this instance.
     * @param heldTicks The number of ticks the ability activation button is held down.
     * @param keyNumber The key number that was pressed.
     * @param mode      The mode that was activated.
     */
    fun onRelease(entity: LivingEntity?, heldTicks: Int, keyNumber: Int, mode: Int) {
        this.ability!!.onRelease(this, entity, heldTicks, keyNumber, mode)
    }

    /**
     * Called when the [LivingEntity] owning this Ability scrolls the mouse when holding the ability activation buttons.
     *
     * @param entity    Affected [LivingEntity] owning this instance.
     * @param delta     The scroll delta of the mouse scroll.
     * @param mode      The mode that was activated.
     */
    fun onScroll(entity: LivingEntity?, delta: Double, mode: Int) {
        this.ability!!.onScroll(this, entity, delta, mode)
    }

    /**
     * Called when the [LivingEntity] learns this instance.
     *
     * @param entity Affected [LivingEntity] learning this instance.
     */
    fun onLearnAbility(entity: LivingEntity?) {
        this.ability!!.onLearnAbility(this, entity)
    }

    /**
     * Called when the [LivingEntity] forgets this instance.
     *
     * @param entity Affected [LivingEntity] learning this instance.
     */
    fun onForgetAbility(entity: LivingEntity?) {
        this.ability!!.onForgetAbility(this, entity)
    }

    /**
     * Called when the [LivingEntity] masters this instance.
     *
     * @param entity Affected [LivingEntity] owning this Ability.
     */
    fun onAbilityMastered(entity: LivingEntity?) {
        this.ability!!.onAbilityMastered(this, entity)
    }

    /**
     * Called when the [LivingEntity] owning this instance gains an effect.
     *
     * @param entity owning this instance.
     */
    fun onEffectAdded(entity: LivingEntity?, source: Entity?, instance: Changeable<MobEffectInstance?>?): Boolean {
        return this.ability!!.onEffectAdded(this, entity, source, instance)
    }

    /**
     * Called when the [LivingEntity] owning this instance starts to be targeted by a mob.
     *
     * @return false will stop the mob from targeting the owner.
     */
    fun onBeingTargeted(owner: Changeable<LivingEntity?>?, mob: LivingEntity?): Boolean {
        return this.ability!!.onBeingTargeted(this, owner, mob)
    }

    /**
     * Called when the [LivingEntity] owning this instance starts to be attacked.
     *
     *
     * Gets executed before [AbilityInstance.onDamageEntity]
     *
     * @return false will prevent the owner from taking damage.
     */
    fun onBeingDamaged(entity: LivingEntity?, source: DamageSource?, amount: Float): Boolean {
        return this.ability!!.onBeingDamaged(this, entity, source, amount)
    }

    /**
     * Called when the [LivingEntity] owning this instance starts attacking another [LivingEntity].
     *
     *
     * Gets executed after [AbilityInstance.onBeingDamaged]<br></br>
     * Gets executed before [AbilityInstance.onTouchEntity]
     *
     * @return false will prevent the owner from dealing damage
     */
    fun onDamageEntity(
        owner: LivingEntity?,
        target: LivingEntity?,
        source: DamageSource?,
        amount: Changeable<Float?>?
    ): Boolean {
        return this.ability!!.onDamageEntity(this, owner, target, source, amount)
    }

    /**
     * Called when the [LivingEntity] owning this instance hurts another [LivingEntity] (after effects like Barriers are consumed the damage amount).
     *
     *
     * Gets executed after [AbilityInstance.onDamageEntity]
     * Gets executed before [AbilityInstance.onTakenDamage]
     *
     * @return false will prevent the owner from dealing damage.
     */
    fun onTouchEntity(
        owner: LivingEntity?,
        target: LivingEntity?,
        source: DamageSource?,
        amount: Changeable<Float?>?
    ): Boolean {
        return this.ability!!.onTouchEntity(this, owner, target, source, amount)
    }

    /**
     * Called when the [LivingEntity] owning this instance takes damage.
     *
     *
     * Gets executed after [AbilityInstance.onTouchEntity]
     *
     * @return false will prevent the owner from taking damage.
     */
    fun onTakenDamage(owner: LivingEntity?, source: DamageSource?, amount: Changeable<Float?>?): Boolean {
        return this.ability!!.onTakenDamage(this, owner, source, amount)
    }

    /**
     * Called when the [LivingEntity] owning this Ability is hit by a projectile.
     */
    fun onProjectileHit(
        living: LivingEntity?,
        hitResult: EntityHitResult?,
        projectile: Projectile?,
        deflection: Changeable<ProjectileDeflection?>?,
        result: Changeable<ProjectileHitResult?>?
    ) {
        this.ability!!.onProjectileHit(this, living, hitResult, projectile, deflection, result)
    }

    /**
     * Called when the [LivingEntity] owning this Ability dies.
     *
     * @return false will prevent the owner from dying.
     */
    fun onDeath(owner: LivingEntity?, source: DamageSource?): Boolean {
        return this.ability!!.onDeath(this, owner, source)
    }

    /**
     * Called when the [ServerPlayer] owning this Ability respawns.
     */
    fun onRespawn(owner: ServerPlayer?, conqueredEnd: Boolean) {
        this.ability!!.onRespawn(this, owner, conqueredEnd)
    }

    val displayName: MutableComponent?
        get() = this.ability!!.name

    fun getChatDisplayName(withDescription: Boolean): MutableComponent? {
        return this.ability!!.getChatDisplayName(withDescription)
    }

    fun `is`(tag: TagKey<Ability?>?): Boolean {
        return this.abilityRegistrySupplier.`is`(tag)
    }

    companion object {
        const val REMOVE_TIME_TAG: String = "RemoveTime"
        const val MASTERY_TAG: String = "Mastery"
        const val TOGGLED_TAG: String = "Toggled"
        const val COOLDOWN_LIST_TAG: String = "CooldownList"

        /**
         * Can be used to load a [AbilityInstance] from a [CompoundTag].
         *
         *
         * The [CompoundTag] has to be created though [AbilityInstance.toNBT]
         */
        @Throws(NullPointerException::class)
        fun fromNBT(tag: CompoundTag?): AbilityInstance {
            val abilityLocation = ResourceLocation.tryParse(tag!!.getString("ability"))
            val ability = AbilityAPI.abilityRegistry!!.get(abilityLocation)
            requireNotNull(ability) { "Ability not found in registry: " + abilityLocation }
            val instance = ability.createDefaultInstance()
            instance.deserialize(tag)
            return instance
        }
    }
}
