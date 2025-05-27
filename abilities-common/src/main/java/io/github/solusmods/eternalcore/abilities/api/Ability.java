package io.github.solusmods.eternalcore.abilities.api;


import dev.architectury.event.Event;
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage;
import io.github.solusmods.eternalcore.entity.api.ProjectileHitResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This is the Registry Object for Ability.
 * Extend from this Class to create your own Abilities.
 * <p>
 * To add functionality to the {@link Ability}, you need to implement a listener interface.
 * Those interfaces allow you to invoke a Method when an {@link Event} happens.
 * The Method will only be invoked for an {@link Entity} that learned the {@link Ability}.
 * <p>
 * Abilitys can be learned by calling the {@link AbilityStorage#learnAbility} method.
 * You can simply use {@link AbilityAPI#getAbilitiesFrom(LivingEntity)} to get the {@link AbilityStorage} of an {@link Entity}.
 * <p>
 * You're also allowed to override the {@link Ability#createDefaultInstance()} method to create your own implementation
 * of a {@link AbilityInstance}. This is required if you want to attach additional data to the {@link Ability}
 * (for example to allow to disable a ability or make the ability gain exp on usage).
 */
public abstract class Ability {
    protected final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();

    /**
     * Used to create a {@link AbilityInstance} of this Ability.
     * <p>
     * Override this Method to use your extended version of {@link AbilityInstance}
     */
    public AbilityInstance createDefaultInstance() {
        return new AbilityInstance(this);
    }

    /**
     * Used to get the {@link ResourceLocation} id of this ability.
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return AbilityAPI.getAbilityRegistry().getId(this);
    }

    /**
     * Used to get the {@link MutableComponent} name of this ability for translation.
     */
    @Nullable
    public MutableComponent getName() {
        final ResourceLocation id = getRegistryName();
        if (id == null) return null;
        return Component.translatable(String.format("%s.ability.%s", id.getNamespace(), id.getPath().replace('/', '.')));
    }

    public MutableComponent getChatDisplayName(boolean withDescription) {
        Style style = Style.EMPTY.withColor(ChatFormatting.GRAY);
        if (withDescription) {
            MutableComponent hoverMessage = this.getName().append("\n");
            hoverMessage.append(this.getAbilityDescription().withStyle(ChatFormatting.GRAY));
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
        }

        MutableComponent component = Component.literal("[").append(this.getName()).append("]");
        return component.withStyle(style);
    }

    /**
     * Used to get the {@link ResourceLocation} of this ability's icon texture.
     */
    @Nullable
    public ResourceLocation getAbilityIcon() {
        ResourceLocation id = this.getRegistryName();
        if (id == null) return null;
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "icons/abilities/" + id.getPath());
    }

    /**
     * Used to get the {@link MutableComponent} description of this ability for translation.
     */
    public MutableComponent getAbilityDescription() {
        ResourceLocation id = this.getRegistryName();
        if (id == null) return Component.empty();
        return Component.translatable(String.format("%s.ability.%s.description", id.getNamespace(), id.getPath().replace('/', '.')));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ability ability = (Ability) o;
        return Objects.equals(this.getRegistryName(), ability.getRegistryName());
    }

    /**
     * Determine if the {@link AbilityInstance} of this Ability can be used by {@link LivingEntity}.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param user   Affected {@link LivingEntity} owning this Ability.
     * @return false will stop {@link LivingEntity} from using any feature of the ability.
     */
    public boolean canInteractAbility(AbilityInstance instance, LivingEntity user) {
        return true;
    }

    /**
     * @return the maximum number of ticks that this ability can be held down with the ability activation button.
     * </p>
     */
    public int getMaxHeldTime(AbilityInstance instance, LivingEntity entity) {
        return 72000;
    }

    /**
     * Determine if this ability can be toggled.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     * @return false if this ability is not toggleable.
     */
    public boolean canBeToggled(AbilityInstance instance, LivingEntity entity) {
        return false;
    }

    /**
     * Determine if a mode of this ability can still be activated when on cooldown
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     * @return false if this ability cannot ignore cooldown.
     */
    public boolean canIgnoreCoolDown(AbilityInstance instance, LivingEntity entity, int mode) {
        return false;
    }

    /**
     * Determine if this ability's {@link Ability#onTick} can be executed.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     * @return false if this ability cannot tick.
     */
    public boolean canTick(AbilityInstance instance, LivingEntity entity) {
        return false;
    }

    /**
     * Determine if this ability's {@link Ability#onScroll} can be executed.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     * @return false if this ability cannot be scrolled.
     */
    public boolean canScroll(AbilityInstance instance, LivingEntity entity) {
        return false;
    }

    /**
     * @return the number of modes that this ability can have.
     */
    public int getModes() {
        return 1;
    }

    /**
     * @return the maximum mastery points that this ability can have.
     */
    public int getMaxMastery() {
        return 100;
    }

    /**
     * Determine if the {@link AbilityInstance} of this Ability is mastered by {@link LivingEntity} owning it.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     * @return true to will mark this Ability is mastered, which can be used for increase stats or additional features/modes.
     */
    public boolean isMastered(AbilityInstance instance, LivingEntity entity) {
        return instance.getMastery() >= getMaxMastery();
    }

    /**
     * Increase the mastery points for {@link AbilityInstance} of this Ability if not mastered.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     */
    public void addMasteryPoint(AbilityInstance instance, LivingEntity entity) {
        if (isMastered(instance, entity)) return;
        instance.setMastery(instance.getMastery() + 1);
        if (isMastered(instance, entity)) instance.onAbilityMastered(entity);
    }

    /**
     * Adds an attribute modifier to this ability. This method can be called for more than one attribute.
     * The attributes are applied to an entity when the ability is held and removed when it stops being held.
     * </p>
     */
    public void addHeldAttributeModifier(Holder<Attribute> holder, ResourceLocation resourceLocation, double amount, AttributeModifier.Operation operation) {
        this.attributeModifiers.put(holder, new AttributeTemplate(resourceLocation, amount, operation));
    }

    public void addHeldAttributeModifier(Holder<Attribute> holder, String id, double amount, AttributeModifier.Operation operation) {
        this.attributeModifiers.put(holder, new AttributeTemplate(id, amount, operation));
    }

    /**
     * @return the amplifier for each attribute template that this ability applies.
     * </p>
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     * @param instance Affected {@link AbilityInstance}
     * @param holder   Affected {@link Holder<Attribute>} that this ability provides.
     * @param template Affected {@link AttributeTemplate} that this ability provides for an attribute.
     */
    public double getAttributeModifierAmplifier(AbilityInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
        return 1;
    }

    /**
     * Applies the attribute modifiers of this ability on the {@link LivingEntity} holding the ability activation button.
     *
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     * @param instance Affected {@link AbilityInstance}
     */
    public void addHeldAttributeModifiers(AbilityInstance instance, LivingEntity entity, int mode) {
        if (this.attributeModifiers.isEmpty()) return;

        AttributeMap attributeMap = entity.getAttributes();
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());

            if (attributeInstance == null) continue;
            attributeInstance.removeModifier(entry.getValue().id());
            attributeInstance.addOrUpdateTransientModifier(entry.getValue().create(instance.getAttributeModifierAmplifier(entity, entry.getKey(), entry.getValue(), mode)));
        }
    }

    /**
     * Removes the attribute modifiers of this ability from the {@link LivingEntity} holding the ability activation button.
     *
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     */
    public void removeAttributeModifiers(AbilityInstance instance, LivingEntity entity, int mode) {
        if (this.attributeModifiers.isEmpty()) return;
        AttributeMap map = entity.getAttributes();
        List<AttributeInstance> dirtyInstances = new ArrayList<>();

        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = map.getInstance(entry.getKey());
            if (attributeInstance == null) continue;
            attributeInstance.removeModifier(entry.getValue().id());
            dirtyInstances.add(attributeInstance);
        }

        if (!dirtyInstances.isEmpty() && entity instanceof ServerPlayer player) {
            ClientboundUpdateAttributesPacket packet = new ClientboundUpdateAttributesPacket(player.getId(), dirtyInstances);
            player.connection.send(packet);
        }
    }

    /**
     * Called when the {@link LivingEntity} owing this Ability toggles it on.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     */
    public void onToggleOn(AbilityInstance instance, LivingEntity entity) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability toggles it off.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     */
    public void onToggleOff(AbilityInstance instance, LivingEntity entity) {
        // Override this method to add your own logic
    }

    /**
     * Called every tick of the {@link LivingEntity} owning this Ability.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param living   Affected {@link LivingEntity} owning this Ability.
     */
    public void onTick(AbilityInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability presses the ability activation button.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     */
    public void onPressed(AbilityInstance instance, LivingEntity entity, int keyNumber, int mode) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability holds the ability activation button.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param living   Affected {@link LivingEntity} owning this Ability.
     * @return true to continue ticking this Ability.
     */
    public boolean onHeld(AbilityInstance instance, LivingEntity living, int heldTicks, int mode) {
        // Override this method to add your own logic
        return false;
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability releases the ability activation button after {@param heldTicks}.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     */
    public void onRelease(AbilityInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability scrolls the mouse when holding the ability activation buttons.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param living   Affected {@link LivingEntity} owning this Ability.
     * @param delta    The scroll delta of the mouse scroll.
     */
    public void onScroll(AbilityInstance instance, LivingEntity living, double delta, int mode) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} learns this Ability.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} learning this Ability.
     */
    public void onLearnAbility(AbilityInstance instance, LivingEntity entity) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} forgets this Ability.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} learning this Ability.
     */
    public void onForgetAbility(AbilityInstance instance, LivingEntity entity) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} masters this ability.
     *
     * @param instance Affected {@link AbilityInstance}
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     */
    public void onAbilityMastered(AbilityInstance instance, LivingEntity entity) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability gains an effect.
     *
     * @see AbilityInstance#onEffectAdded(LivingEntity, Entity, Changeable)
     */
    public boolean onEffectAdded(AbilityInstance instance, LivingEntity entity, @Nullable Entity source, Changeable<MobEffectInstance> effect) {
        // Override this method to add your own logic
        return true;
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability starts to be targeted by a mob.
     *
     * @see AbilityInstance#onBeingTargeted(Changeable, LivingEntity)
     */
    public boolean onBeingTargeted(AbilityInstance instance, Changeable<LivingEntity> target, LivingEntity owner) {
        // Override this method to add your own logic
        return true;
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability starts to be attacked.
     *
     * @see AbilityInstance#onBeingDamaged(LivingEntity, DamageSource, float)
     */
    public boolean onBeingDamaged(AbilityInstance instance, LivingEntity entity, DamageSource source, float amount) {
        // Override this method to add your own logic
        return true;
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability damage another {@link LivingEntity}.
     *
     * @see AbilityInstance#onDamageEntity(LivingEntity, LivingEntity, DamageSource, Changeable)
     */
    public boolean onDamageEntity(AbilityInstance instance, LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
        // Override this method to add your own logic
        return true;
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability damage another {@link LivingEntity},
     *
     * @see AbilityInstance#onTouchEntity(LivingEntity, LivingEntity, DamageSource, Changeable)
     */
    public boolean onTouchEntity(AbilityInstance instance, LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
        // Override this method to add your own logic
        return true;
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability takes damage.
     *
     * @see AbilityInstance#onTakenDamage(LivingEntity, DamageSource, Changeable)
     */
    public boolean onTakenDamage(AbilityInstance instance, LivingEntity owner, DamageSource source, Changeable<Float> amount) {
        // Override this method to add your own logic
        return true;
    }

    /**
     * Called when the {@link LivingEntity} is hit by a projectile.
     */
    public void onProjectileHit(AbilityInstance instance, LivingEntity living, EntityHitResult hitResult, Projectile projectile, Changeable<ProjectileDeflection> deflection, Changeable<ProjectileHitResult> result) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability dies.
     *
     * @see AbilityInstance#onDeath(LivingEntity, DamageSource)
     */
    public boolean onDeath(AbilityInstance instance, LivingEntity owner, DamageSource source) {
        // Override this method to add your own logic
        return true;
    }

    /**
     * Called when the {@link ServerPlayer} owning this Ability respawns.
     */
    public void onRespawn(AbilityInstance instance, ServerPlayer owner, boolean conqueredEnd) {
        // Override this method to add your own logic
    }
}
