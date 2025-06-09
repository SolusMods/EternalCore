package io.github.solusmods.eternalcore.abilities.api

import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage
import io.github.solusmods.eternalcore.entity.api.ProjectileHitResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ProjectileDeflection
import net.minecraft.world.phys.EntityHitResult


/**
 * This is the Registry Object for Ability.
 * Extend from this Class to create your own Abilities.
 *
 *
 * To add functionality to the [Ability], you need to implement a listener interface.
 * Those interfaces allow you to invoke a Method when an [Event] happens.
 * The Method will only be invoked for an [Entity] that learned the [Ability].
 *
 *
 * Abilitys can be learned by calling the [AbilityStorage.learnAbility] method.
 * You can simply use [AbilityAPI.getAbilitiesFrom] to get the [AbilityStorage] of an [Entity].
 *
 *
 * You're also allowed to override the [Ability.createDefaultInstance] method to create your own implementation
 * of a [AbilityInstance]. This is required if you want to attach additional data to the [Ability]
 * (for example to allow to disable a ability or make the ability gain exp on usage).
 */
abstract class Ability {
    protected val attributeModifiers: MutableMap<Holder<Attribute?>?, AttributeTemplate?> =
        Object2ObjectOpenHashMap<Holder<Attribute?>?, AttributeTemplate?>()

    /**
     * Used to create a [AbilityInstance] of this Ability.
     *
     *
     * Override this Method to use your extended version of [AbilityInstance]
     */
    fun createDefaultInstance(): AbilityInstance {
        return AbilityInstance(this)
    }

    val registryName: ResourceLocation?
        /**
         * Used to get the [ResourceLocation] id of this ability.
         */
        get() = AbilityAPI.abilityRegistry!!.getId(this)

    val name: MutableComponent?
        /**
         * Used to get the [MutableComponent] name of this ability for translation.
         */
        get() {
            val id = this.registryName
            if (id == null) return null
            return Component.translatable(
                String.format(
                    "%s.ability.%s",
                    id.namespace,
                    id.path.replace('/', '.')
                )
            )
        }

    fun getChatDisplayName(withDescription: Boolean): MutableComponent {
        var style = Style.EMPTY.withColor(ChatFormatting.GRAY)
        if (withDescription) {
            val hoverMessage = this.name!!.append("\n")
            hoverMessage.append(this.abilityDescription.withStyle(ChatFormatting.GRAY))
            style = style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage))
        }

        val component = Component.literal("[").append(
            this.name
        ).append("]")
        return component.withStyle(style)
    }

    val abilityIcon: ResourceLocation?
        /**
         * Used to get the [ResourceLocation] of this ability's icon texture.
         */
        get() {
            val id = this.registryName
            if (id == null) return null
            return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "icons/abilities/" + id.getPath()
            )
        }

    val abilityDescription: MutableComponent
        /**
         * Used to get the [MutableComponent] description of this ability for translation.
         */
        get() {
            val id = this.registryName
            if (id == null) return Component.empty()
            return Component.translatable(
                String.format(
                    "%s.ability.%s.description",
                    id.getNamespace(),
                    id.getPath().replace('/', '.')
                )
            )
        }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val ability = o as Ability
        return this.registryName == ability.registryName
    }

    /**
     * Determine if the [AbilityInstance] of this Ability can be used by [LivingEntity].
     *
     * @param instance Affected [AbilityInstance]
     * @param user   Affected [LivingEntity] owning this Ability.
     * @return false will stop [LivingEntity] from using any feature of the ability.
     */
    fun canInteractAbility(instance: AbilityInstance?, user: LivingEntity?): Boolean {
        return true
    }

    /**
     * @return the maximum number of ticks that this ability can be held down with the ability activation button.
     *
     */
    fun getMaxHeldTime(instance: AbilityInstance?, entity: LivingEntity?): Int {
        return 72000
    }

    /**
     * Determine if this ability can be toggled.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     * @return false if this ability is not toggleable.
     */
    fun canBeToggled(instance: AbilityInstance?, entity: LivingEntity?): Boolean {
        return false
    }

    /**
     * Determine if a mode of this ability can still be activated when on cooldown
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     * @return false if this ability cannot ignore cooldown.
     */
    fun canIgnoreCoolDown(instance: AbilityInstance?, entity: LivingEntity?, mode: Int): Boolean {
        return false
    }

    /**
     * Determine if this ability's [Ability.onTick] can be executed.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     * @return false if this ability cannot tick.
     */
    fun canTick(instance: AbilityInstance?, entity: LivingEntity?): Boolean {
        return false
    }

    /**
     * Determine if this ability's [Ability.onScroll] can be executed.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     * @return false if this ability cannot be scrolled.
     */
    fun canScroll(instance: AbilityInstance?, entity: LivingEntity?): Boolean {
        return false
    }

    val modes: Int
        /**
         * @return the number of modes that this ability can have.
         */
        get() = 1

    val maxMastery: Int
        /**
         * @return the maximum mastery points that this ability can have.
         */
        get() = 100

    /**
     * Determine if the [AbilityInstance] of this Ability is mastered by [LivingEntity] owning it.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     * @return true to will mark this Ability is mastered, which can be used for increase stats or additional features/modes.
     */
    fun isMastered(instance: AbilityInstance, entity: LivingEntity?): Boolean {
        return instance.mastery >= this.maxMastery
    }

    /**
     * Increase the mastery points for [AbilityInstance] of this Ability if not mastered.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     */
    fun addMasteryPoint(instance: AbilityInstance, entity: LivingEntity?) {
        if (isMastered(instance, entity)) return
        instance.mastery = (instance.mastery + 1)
        if (isMastered(instance, entity)) instance.onAbilityMastered(entity)
    }

    /**
     * Adds an attribute modifier to this ability. This method can be called for more than one attribute.
     * The attributes are applied to an entity when the ability is held and removed when it stops being held.
     *
     */
    fun addHeldAttributeModifier(
        holder: Holder<Attribute?>?,
        resourceLocation: ResourceLocation?,
        amount: Double,
        operation: AttributeModifier.Operation?
    ) {
        this.attributeModifiers.put(holder, AttributeTemplate(resourceLocation, amount, operation))
    }

    fun addHeldAttributeModifier(
        holder: Holder<Attribute?>?,
        id: String?,
        amount: Double,
        operation: AttributeModifier.Operation?
    ) {
        this.attributeModifiers.put(holder, AttributeTemplate(id, amount, operation))
    }

    /**
     * @return the amplifier for each attribute template that this ability applies.
     *
     * @param entity   Affected [LivingEntity] owning this Ability.
     * @param instance Affected [AbilityInstance]
     * @param holder   Affected [<] that this ability provides.
     * @param template Affected [AttributeTemplate] that this ability provides for an attribute.
     */
    fun getAttributeModifierAmplifier(
        instance: AbilityInstance?,
        entity: LivingEntity?,
        holder: Holder<Attribute?>?,
        template: AttributeTemplate?,
        mode: Int
    ): Double {
        return 1.0
    }

    /**
     * Applies the attribute modifiers of this ability on the [LivingEntity] holding the ability activation button.
     *
     * @param entity   Affected [LivingEntity] owning this Ability.
     * @param instance Affected [AbilityInstance]
     */
    fun addHeldAttributeModifiers(instance: AbilityInstance, entity: LivingEntity, mode: Int) {
        if (this.attributeModifiers.isEmpty()) return

        val attributeMap = entity.getAttributes()
        for (entry in this.attributeModifiers.entries) {
            val attributeInstance = attributeMap.getInstance(entry.key)

            if (attributeInstance == null) continue
            attributeInstance.removeModifier(entry.value!!.id())
            attributeInstance.addOrUpdateTransientModifier(
                entry.value!!.create(
                    instance.getAttributeModifierAmplifier(
                        entity,
                        entry.key,
                        entry.value,
                        mode
                    )
                )
            )
        }
    }

    /**
     * Removes the attribute modifiers of this ability from the [LivingEntity] holding the ability activation button.
     *
     * @param entity   Affected [LivingEntity] owning this Ability.
     */
    fun removeAttributeModifiers(instance: AbilityInstance?, entity: LivingEntity, mode: Int) {
        if (this.attributeModifiers.isEmpty()) return
        val map = entity.getAttributes()
        val dirtyInstances: MutableList<AttributeInstance?> = ArrayList<AttributeInstance?>()

        for (entry in this.attributeModifiers.entries) {
            val attributeInstance = map.getInstance(entry.key)
            if (attributeInstance == null) continue
            attributeInstance.removeModifier(entry.value!!.id())
            dirtyInstances.add(attributeInstance)
        }

        if (!dirtyInstances.isEmpty() && entity is ServerPlayer) {
            val packet = ClientboundUpdateAttributesPacket(entity.getId(), dirtyInstances)
            entity.connection.send(packet)
        }
    }

    /**
     * Called when the [LivingEntity] owing this Ability toggles it on.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     */
    fun onToggleOn(instance: AbilityInstance?, entity: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] owning this Ability toggles it off.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     */
    fun onToggleOff(instance: AbilityInstance?, entity: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Called every tick of the [LivingEntity] owning this Ability.
     *
     * @param instance Affected [AbilityInstance]
     * @param living   Affected [LivingEntity] owning this Ability.
     */
    fun onTick(instance: AbilityInstance?, living: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] owning this Ability presses the ability activation button.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     */
    fun onPressed(instance: AbilityInstance?, entity: LivingEntity?, keyNumber: Int, mode: Int) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] owning this Ability holds the ability activation button.
     *
     * @param instance Affected [AbilityInstance]
     * @param living   Affected [LivingEntity] owning this Ability.
     * @return true to continue ticking this Ability.
     */
    fun onHeld(instance: AbilityInstance?, living: LivingEntity?, heldTicks: Int, mode: Int): Boolean {
        // Override this method to add your own logic
        return false
    }

    /**
     * Called when the [LivingEntity] owning this Ability releases the ability activation button after {@param heldTicks}.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     */
    fun onRelease(instance: AbilityInstance?, entity: LivingEntity?, heldTicks: Int, keyNumber: Int, mode: Int) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] owning this Ability scrolls the mouse when holding the ability activation buttons.
     *
     * @param instance Affected [AbilityInstance]
     * @param living   Affected [LivingEntity] owning this Ability.
     * @param delta    The scroll delta of the mouse scroll.
     */
    fun onScroll(instance: AbilityInstance?, living: LivingEntity?, delta: Double, mode: Int) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] learns this Ability.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] learning this Ability.
     */
    fun onLearnAbility(instance: AbilityInstance?, entity: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] forgets this Ability.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] learning this Ability.
     */
    fun onForgetAbility(instance: AbilityInstance?, entity: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] masters this ability.
     *
     * @param instance Affected [AbilityInstance]
     * @param entity   Affected [LivingEntity] owning this Ability.
     */
    fun onAbilityMastered(instance: AbilityInstance?, entity: LivingEntity?) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] owning this Ability gains an effect.
     *
     * @see AbilityInstance.onEffectAdded
     */
    fun onEffectAdded(
        instance: AbilityInstance?,
        entity: LivingEntity?,
        source: Entity?,
        effect: Changeable<MobEffectInstance?>?
    ): Boolean {
        // Override this method to add your own logic
        return true
    }

    /**
     * Called when the [LivingEntity] owning this Ability starts to be targeted by a mob.
     *
     * @see AbilityInstance.onBeingTargeted
     */
    fun onBeingTargeted(instance: AbilityInstance?, target: Changeable<LivingEntity?>?, owner: LivingEntity?): Boolean {
        // Override this method to add your own logic
        return true
    }

    /**
     * Called when the [LivingEntity] owning this Ability starts to be attacked.
     *
     * @see AbilityInstance.onBeingDamaged
     */
    fun onBeingDamaged(
        instance: AbilityInstance?,
        entity: LivingEntity?,
        source: DamageSource?,
        amount: Float
    ): Boolean {
        // Override this method to add your own logic
        return true
    }

    /**
     * Called when the [LivingEntity] owning this Ability damage another [LivingEntity].
     *
     * @see AbilityInstance.onDamageEntity
     */
    fun onDamageEntity(
        instance: AbilityInstance?,
        owner: LivingEntity?,
        target: LivingEntity?,
        source: DamageSource?,
        amount: Changeable<Float?>?
    ): Boolean {
        // Override this method to add your own logic
        return true
    }

    /**
     * Called when the [LivingEntity] owning this Ability damage another [LivingEntity],
     *
     * @see AbilityInstance.onTouchEntity
     */
    fun onTouchEntity(
        instance: AbilityInstance?,
        owner: LivingEntity?,
        target: LivingEntity?,
        source: DamageSource?,
        amount: Changeable<Float?>?
    ): Boolean {
        // Override this method to add your own logic
        return true
    }

    /**
     * Called when the [LivingEntity] owning this Ability takes damage.
     *
     * @see AbilityInstance.onTakenDamage
     */
    fun onTakenDamage(
        instance: AbilityInstance?,
        owner: LivingEntity?,
        source: DamageSource?,
        amount: Changeable<Float?>?
    ): Boolean {
        // Override this method to add your own logic
        return true
    }

    /**
     * Called when the [LivingEntity] is hit by a projectile.
     */
    fun onProjectileHit(
        instance: AbilityInstance?,
        living: LivingEntity?,
        hitResult: EntityHitResult?,
        projectile: Projectile?,
        deflection: Changeable<ProjectileDeflection?>?,
        result: Changeable<ProjectileHitResult?>?
    ) {
        // Override this method to add your own logic
    }

    /**
     * Called when the [LivingEntity] owning this Ability dies.
     *
     * @see AbilityInstance.onDeath
     */
    fun onDeath(instance: AbilityInstance?, owner: LivingEntity?, source: DamageSource?): Boolean {
        // Override this method to add your own logic
        return true
    }

    /**
     * Called when the [ServerPlayer] owning this Ability respawns.
     */
    fun onRespawn(instance: AbilityInstance?, owner: ServerPlayer?, conqueredEnd: Boolean) {
        // Override this method to add your own logic
    }
}
