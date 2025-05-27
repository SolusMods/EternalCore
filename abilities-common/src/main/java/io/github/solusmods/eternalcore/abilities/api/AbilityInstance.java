package io.github.solusmods.eternalcore.abilities.api;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.entity.api.ProjectileHitResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class AbilityInstance {
    public static final String REMOVE_TIME_TAG = "RemoveTime";
    public static final String MASTERY_TAG = "Mastery";
    public static final String TOGGLED_TAG = "Toggled";
    public static final String COOLDOWN_LIST_TAG = "CooldownList";
    private int removeTime = -1;
    private double masteryPoint = 0;
    private boolean toggled = false;
    private List<Integer> cooldownList;
    @Nullable
    private CompoundTag tag = null;
    @Getter
    private boolean dirty = false;
    protected final RegistrySupplier<Ability> abilityRegistrySupplier;

    protected AbilityInstance(Ability ability) {
        this.abilityRegistrySupplier = AbilityAPI.getAbilityRegistry().delegate(AbilityAPI.getAbilityRegistry().getId(ability));
        cooldownList = NonNullList.withSize(ability.getModes(), 0);
    }

    /**
     * Used to get the {@link Ability} type of this Instance.
     */
    public Ability getAbility() {
        return abilityRegistrySupplier.get();
    }

    public ResourceLocation getAbilityId() {
        return this.abilityRegistrySupplier.getId();
    }

    /**
     * Used to create an exact copy of the current instance.
     */
    public AbilityInstance copy() {
        AbilityInstance clone = new AbilityInstance(getAbility());
        clone.dirty = this.dirty;
        clone.cooldownList = new ArrayList<>(this.cooldownList);
        clone.removeTime = this.removeTime;
        clone.masteryPoint = this.masteryPoint;
        clone.toggled = this.toggled;
        if (this.tag != null) clone.tag = this.tag.copy();
        return clone;
    }

    /**
     * This method is used to ensure that all required information are stored.
     * <p>
     * Override {@link AbilityInstance#serialize(CompoundTag)} to store your custom Data.
     */
    public final CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("ability", this.getAbilityId().toString());
        serialize(nbt);
        return nbt;
    }

    /**
     * Can be used to save custom data.
     *
     * @param nbt Tag with data from {@link AbilityInstance#fromNBT(CompoundTag)}
     */
    public CompoundTag serialize(CompoundTag nbt) {
        nbt.putInt(REMOVE_TIME_TAG, this.removeTime);
        nbt.putDouble(MASTERY_TAG, this.masteryPoint);
        nbt.putBoolean(TOGGLED_TAG, this.toggled);
        nbt.putIntArray(COOLDOWN_LIST_TAG, this.cooldownList);
        if (this.tag != null) nbt.put("tag", this.tag.copy());
        return nbt;
    }

    /**
     * Can be used to load custom data.
     */
    public void deserialize(CompoundTag tag) {
        this.removeTime = tag.getInt(REMOVE_TIME_TAG);
        this.masteryPoint = tag.getDouble(MASTERY_TAG);
        this.toggled = tag.getBoolean(TOGGLED_TAG);
        this.cooldownList = Arrays.stream(tag.getIntArray(COOLDOWN_LIST_TAG)).boxed().collect(Collectors.toList());
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
    }

    /**
     * Can be used to load a {@link AbilityInstance} from a {@link CompoundTag}.
     * <p>
     * The {@link CompoundTag} has to be created though {@link AbilityInstance#toNBT()}
     */
    public static AbilityInstance fromNBT(CompoundTag tag) throws NullPointerException {
        ResourceLocation abilityLocation = ResourceLocation.tryParse(tag.getString("ability"));
        Ability ability = AbilityAPI.getAbilityRegistry().get(abilityLocation);
        if (ability == null) throw new IllegalArgumentException("Ability not found in registry: " + abilityLocation);
        AbilityInstance instance = ability.createDefaultInstance();
        instance.deserialize(tag);
        return instance;
    }

    /**
     * Marks the current instance as dirty.
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * This Method is invoked to indicate that a {@link AbilityInstance} has been synced with the clients.
     * <p>
     * Do <strong>NOT</strong> use that method on our own!
     */
    @ApiStatus.Internal
    public void resetDirty() {
        this.dirty = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbilityInstance instance = (AbilityInstance) o;
        return this.getAbilityId().equals(instance.getAbilityId()) &&
                abilityRegistrySupplier.getRegistryKey().equals(instance.abilityRegistrySupplier.getRegistryKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getAbilityId(), abilityRegistrySupplier.getRegistryKey());
    }

    /**
     * Determine if this instance can be used by {@link LivingEntity}.
     *
     * @param user Affected {@link LivingEntity}
     * @return false will stop {@link LivingEntity} from using any feature of the ability.
     */
    public boolean canInteractAbility(LivingEntity user) {
        return this.getAbility().canInteractAbility(this, user);
    }

    /**
     * @return the maximum number of ticks that this ability can be held down with the ability activation button.
     */
    public int getMaxHeldTime(LivingEntity entity) {
        return this.getAbility().getMaxHeldTime(this, entity);
    }

    /**
     * Determine if the {@link Ability} type of this instance can be toggled.
     *
     * @param entity Affected {@link LivingEntity} owning this Ability.
     * @return false if this ability is not toggleable.
     */
    public boolean canBeToggled(LivingEntity entity) {
        return this.getAbility().canBeToggled(this, entity);
    }

    /**
     * Determine if the {@link Ability} type of this instance can still be activated when on cooldown.
     *
     * @param entity Affected {@link LivingEntity} owning this Ability.
     * @return false if this ability cannot ignore cooldown.
     */
    public boolean canIgnoreCoolDown(LivingEntity entity, int mode) {
        return this.getAbility().canIgnoreCoolDown(this, entity, mode);
    }

    /**
     * Determine if this instance's {@link AbilityInstance#onTick} can be executed.
     *
     * @param entity Affected {@link LivingEntity} owning this Ability.
     * @return false if this ability cannot tick.
     */
    public boolean canTick(LivingEntity entity) {
        return this.getAbility().canTick(this, entity);
    }

    /**
     * Determine if this instance's {@link AbilityInstance#onScroll} can be executed.
     *
     * @param entity Affected {@link LivingEntity} owning this Ability.
     * @return false if this ability cannot be scrolled.
     */
    public boolean canScroll(LivingEntity entity) {
        return this.getAbility().canScroll(this, entity);
    }

    /**
     * @return the number of modes that this ability instance has.
     */
    public int getModes() {
        return this.getAbility().getModes();
    }

    /**
     * @return the maximum mastery points that this ability instance can have.
     */
    public int getMaxMastery() {
        return this.getAbility().getMaxMastery();
    }

    /**
     * Determine if the {@link Ability} type of this instance is mastered by {@link LivingEntity} owning it.
     *
     * @param entity Affected {@link LivingEntity} owning this Ability.
     */
    public boolean isMastered(LivingEntity entity) {
        return this.getAbility().isMastered(this, entity);
    }

    /**
     * Increase the mastery point of the {@link Ability} type of this instance.
     *
     * @param entity Affected {@link LivingEntity} owning this Ability.
     */
    public void addMasteryPoint(LivingEntity entity) {
        this.getAbility().addMasteryPoint(this, entity);
    }

    /**
     * @return the mastery point of the {@link Ability} type of this instance.
     */
    public double getMastery() {
        return this.masteryPoint;
    }

    /**
     * Set the mastery point of the {@link Ability} type of this instance.
     */
    public void setMastery(double point) {
        this.masteryPoint = point;
        markDirty();
    }

    /**
     * @return the cooldown of a specific mode of this instance.
     */
    public int getCoolDown(int mode) {
        if (mode < 0 || mode >= cooldownList.size()) return 0;
        return this.cooldownList.get(mode);
    }

    /**
     * @return if a specific mode of this instance is on cooldown.
     */
    public boolean onCoolDown(int mode) {
        if (mode < 0 || mode >= cooldownList.size()) return false;
        return this.cooldownList.get(mode) > 0;
    }

    /**
     * Set the cooldown of a specific mode of this instance.
     */
    public void setCoolDown(int coolDown, int mode) {
        if (mode < 0 || mode >= cooldownList.size()) return;
        this.cooldownList.set(mode, coolDown);
        markDirty();
    }

    /**
     * Set the cooldown of every mode of this instance.
     */
    public void setCoolDowns(int coolDown) {
        Collections.fill(this.cooldownList, coolDown);
        markDirty();
    }

    /**
     * Decrease the cooldown of a specific mode of this instance.
     */
    public void decreaseCoolDown(int coolDown, int mode) {
        if (mode < 0 || mode >= cooldownList.size()) return;
        this.cooldownList.set(mode, Math.max(0, this.cooldownList.get(mode) - coolDown));
        markDirty();
    }

    /**
     * Edit the entire cooldown list of this instance.
     */
    public void setCoolDownList(List<Integer> list) {
        this.cooldownList = list;
    }

    /**
     * @return if this ability instance is temporary, which should be removed when its time runs out.
     */
    public boolean isTemporaryAbility() {
        return this.removeTime != -1;
    }

    /**
     * @return the removal time of this instance.
     */
    public int getRemoveTime() {
        return this.removeTime;
    }

    /**
     * @return if this ability instance needs to be removed.
     */
    public boolean shouldRemove() {
        return this.removeTime == 0;
    }

    /**
     * Set the remove time of this instance.
     */
    public void setRemoveTime(int removeTime) {
        this.removeTime = removeTime;
        markDirty();
    }

    /**
     * Decrease the remove time of this instance.
     */
    public void decreaseRemoveTime(int time) {
        if (this.removeTime > 0) {
            this.removeTime = Math.max(0, this.removeTime - time);
            markDirty();
        }
    }

    /**
     * @return if this instance is toggled.
     */
    public boolean isToggled() {
        return this.toggled;
    }

    /**
     * Toggle on/off this instance.
     */
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        markDirty();
    }

    /**
     * @return compound tag of this instance.
     */
    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    /**
     * Used to add/create additional tags for this instance.
     *
     * @return compound tag of this instance or create if null.
     */
    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new CompoundTag());
            this.markDirty();
        }
        return this.tag;
    }

    /**
     * Used to add/create additional tags for this instance.
     * Set the tag of this instance.
     */
    public void setTag(@Nullable CompoundTag tag) {
        this.tag = tag;
        markDirty();
    }

    /**
     * @return the amplifier for each attribute modifier that this instance applies.
     * </p>
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     * @param holder   Affected {@link Holder <Attribute>} that this ability provides.
     * @param template Affected {@link AttributeTemplate} that this ability provides for an attribute.
     */
    public double getAttributeModifierAmplifier(LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
        return this.getAbility().getAttributeModifierAmplifier(this, entity, holder, template, mode);
    }

    /**
     * Applies the attribute modifiers of this instance on the {@link LivingEntity} holding the ability activation button.
     *
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     */
    public void addHeldAttributeModifiers(LivingEntity entity, int mode) {
        this.getAbility().addHeldAttributeModifiers(this, entity, mode);
    }

    /**
     * Removes the attribute modifiers of this instance from the {@link LivingEntity} holding the ability activation button.
     *
     * @param entity   Affected {@link LivingEntity} owning this Ability.
     */
    public void removeAttributeModifiers(LivingEntity entity, int mode) {
        this.getAbility().removeAttributeModifiers(this, entity, mode);
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability toggles this {@link Ability} type of this instance on.
     *
     * @param entity Affected {@link LivingEntity} owning this Ability.
     */
    public void onToggleOn(LivingEntity entity) {
        this.getAbility().onToggleOn(this, entity);
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability toggles this {@link Ability} type of this instance off.
     *
     * @param entity Affected {@link LivingEntity} owning this instance.
     */
    public void onToggleOff(LivingEntity entity) {
        this.getAbility().onToggleOff(this, entity);
    }

    /**
     * Called every tick if this instance is obtained by {@link LivingEntity}.
     *
     * @param living Affected {@link LivingEntity} owning this instance.
     */
    public void onTick(LivingEntity living) {
        this.getAbility().onTick(this, living);
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability presses the ability activation button.
     *
     * @param entity    Affected {@link LivingEntity} owning this instance.
     * @param keyNumber The key number that was pressed.
     * @param mode      The mode that was activated.
     */
    public void onPressed(LivingEntity entity, int keyNumber, int mode) {
        this.getAbility().onPressed(this, entity, keyNumber, mode);
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability holds the ability activation button.
     *
     * @param entity    Affected {@link LivingEntity} owning this instance.
     * @param heldTicks The number of ticks the ability activation button is being held down.
     * @param mode      The mode that is being held down.
     * @return true to continue ticking this instance.
     */
    public boolean onHeld(LivingEntity entity, int heldTicks, int mode) {
        return this.getAbility().onHeld(this, entity, heldTicks, mode);
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability releases the ability activation button after {@param heldTicks}.
     *
     * @param entity    Affected {@link LivingEntity} owning this instance.
     * @param heldTicks The number of ticks the ability activation button is held down.
     * @param keyNumber The key number that was pressed.
     * @param mode      The mode that was activated.
     */
    public void onRelease(LivingEntity entity, int heldTicks, int keyNumber, int mode) {
        this.getAbility().onRelease(this, entity, heldTicks, keyNumber, mode);
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability scrolls the mouse when holding the ability activation buttons.
     *
     * @param entity    Affected {@link LivingEntity} owning this instance.
     * @param delta     The scroll delta of the mouse scroll.
     * @param mode      The mode that was activated.
     */
    public void onScroll(LivingEntity entity, double delta, int mode) {
        this.getAbility().onScroll(this, entity, delta, mode);
    }

    /**
     * Called when the {@link LivingEntity} learns this instance.
     *
     * @param entity Affected {@link LivingEntity} learning this instance.
     */
    public void onLearnAbility(LivingEntity entity) {
        this.getAbility().onLearnAbility(this, entity);
    }

    /**
     * Called when the {@link LivingEntity} forgets this instance.
     *
     * @param entity Affected {@link LivingEntity} learning this instance.
     */
    public void onForgetAbility(LivingEntity entity) {
        this.getAbility().onForgetAbility(this, entity);
    }

    /**
     * Called when the {@link LivingEntity} masters this instance.
     *
     * @param entity Affected {@link LivingEntity} owning this Ability.
     */
    public void onAbilityMastered(LivingEntity entity) {
        this.getAbility().onAbilityMastered(this, entity);
    }

    /**
     * Called when the {@link LivingEntity} owning this instance gains an effect.
     *
     * @param entity owning this instance.
     */
    public boolean onEffectAdded(LivingEntity entity, @Nullable Entity source, Changeable<MobEffectInstance> instance) {
        return this.getAbility().onEffectAdded(this, entity, source, instance);
    }

    /**
     * Called when the {@link LivingEntity} owning this instance starts to be targeted by a mob.
     *
     * @return false will stop the mob from targeting the owner.
     */
    public boolean onBeingTargeted(Changeable<LivingEntity> owner, LivingEntity mob) {
        return this.getAbility().onBeingTargeted(this, owner, mob);
    }

    /**
     * Called when the {@link LivingEntity} owning this instance starts to be attacked.
     * <p>
     * Gets executed before {@link AbilityInstance#onDamageEntity}
     *
     * @return false will prevent the owner from taking damage.
     */
    public boolean onBeingDamaged(LivingEntity entity, DamageSource source, float amount) {
        return this.getAbility().onBeingDamaged(this, entity, source, amount);
    }

    /**
     * Called when the {@link LivingEntity} owning this instance starts attacking another {@link LivingEntity}.
     * <p>
     * Gets executed after {@link AbilityInstance#onBeingDamaged}<br>
     * Gets executed before {@link AbilityInstance#onTouchEntity}
     *
     * @return false will prevent the owner from dealing damage
     */
    public boolean onDamageEntity(LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
        return this.getAbility().onDamageEntity(this, owner, target, source, amount);
    }

    /**
     * Called when the {@link LivingEntity} owning this instance hurts another {@link LivingEntity} (after effects like Barriers are consumed the damage amount).
     * <p>
     * Gets executed after {@link AbilityInstance#onDamageEntity}
     * Gets executed before {@link AbilityInstance#onTakenDamage}
     *
     * @return false will prevent the owner from dealing damage.
     */
    public boolean onTouchEntity(LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
        return this.getAbility().onTouchEntity(this, owner, target, source, amount);
    }

    /**
     * Called when the {@link LivingEntity} owning this instance takes damage.
     * <p>
     * Gets executed after {@link AbilityInstance#onTouchEntity}
     *
     * @return false will prevent the owner from taking damage.
     */
    public boolean onTakenDamage(LivingEntity owner, DamageSource source, Changeable<Float> amount) {
        return this.getAbility().onTakenDamage(this, owner, source, amount);
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability is hit by a projectile.
     */
    public void onProjectileHit(LivingEntity living, EntityHitResult hitResult, Projectile projectile, Changeable<ProjectileDeflection> deflection, Changeable<ProjectileHitResult> result) {
        this.getAbility().onProjectileHit(this, living, hitResult, projectile, deflection, result);
    }

    /**
     * Called when the {@link LivingEntity} owning this Ability dies.
     *
     * @return false will prevent the owner from dying.
     */
    public boolean onDeath(LivingEntity owner, DamageSource source) {
        return this.getAbility().onDeath(this, owner, source);
    }

    /**
     * Called when the {@link ServerPlayer} owning this Ability respawns.
     */
    public void onRespawn(ServerPlayer owner, boolean conqueredEnd) {
        this.getAbility().onRespawn(this, owner, conqueredEnd);
    }

    public MutableComponent getDisplayName() {
        return this.getAbility().getName();
    }

    public MutableComponent getChatDisplayName(boolean withDescription) {
        return this.getAbility().getChatDisplayName(withDescription);
    }

    public boolean is(TagKey<Ability> tag) {
        return this.abilityRegistrySupplier.is(tag);
    }
}
