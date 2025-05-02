package io.github.solusmods.eternalcore.realm.api;

import com.mojang.datafixers.util.Pair;
import dev.architectury.registry.registries.RegistrySupplier;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class RealmInstance implements Cloneable {
    public static final String REALM_KEY = "realm";
    protected final RegistrySupplier<Realm> realmRegistrySupplier;
    @Nullable
    private CompoundTag tag = null;
    @Getter
    private boolean dirty = false;

    protected RealmInstance(Realm realm) {
        this.realmRegistrySupplier = RealmAPI.getRealmRegistry().delegate(RealmAPI.getRealmRegistry().getId(realm));
    }

    /**
     * Can be used to load a {@link RealmInstance} from a {@link CompoundTag}.
     * <p>
     * The {@link CompoundTag} has to be created though {@link RealmInstance#toNBT()}
     */
    public static RealmInstance fromNBT(CompoundTag tag) throws NullPointerException {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString(REALM_KEY));
        Realm realm = RealmAPI.getRealmRegistry().get(location);
        if (realm == null) throw new NullPointerException("No spiritual_root found for location: " + location);
        RealmInstance instance = realm.createDefaultInstance();
        instance.deserialize(tag);
        return instance;
    }

    /**
     * Used to get the {@link Realm} type of this Instance.
     */
    public Realm getRealm() {
        return realmRegistrySupplier.get();
    }

    public ResourceLocation getRealmId() {
        return this.realmRegistrySupplier.getId();
    }

    /**
     * Used to get the type of this {@link RealmInstance}.
     */
    public MutableComponent getType() {
        return this.getRealm().getType();
    }

    /**
     * Used to create an exact copy of the current instance.
     */
    public RealmInstance copy() {
        RealmInstance clone = new RealmInstance(getRealm());
        clone.dirty = this.dirty;
        if (this.tag != null) clone.tag = this.tag.copy();
        return clone;
    }

    /**
     * This method is used to ensure that all required information are stored.
     * <p>
     * Override {@link RealmInstance#serialize(CompoundTag)} to store your custom Data.
     */
    public final CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(REALM_KEY, this.getRealmId().toString());
        serialize(nbt);
        return nbt;
    }

    /**
     * Can be used to save custom data.
     *
     * @param nbt Tag with data from {@link RealmInstance#fromNBT(CompoundTag)}
     */
    public CompoundTag serialize(CompoundTag nbt) {
        if (this.tag != null) nbt.put("tag", this.tag.copy());
        return nbt;
    }

    /**
     * Can be used to load custom data.
     */
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
    }

    /**
     * Marks the current instance as dirty.
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * This Method is invoked to indicate that a {@link RealmInstance} has been synced with the clients.
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
        RealmInstance instance = (RealmInstance) o;
        return this.getRealmId().equals(instance.getRealmId()) &&
                realmRegistrySupplier.getRegistryKey().equals(instance.realmRegistrySupplier.getRegistryKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getRealmId(), realmRegistrySupplier.getRegistryKey());
    }

    public boolean is(TagKey<Realm> tag) {
        return this.realmRegistrySupplier.is(tag);
    }

    /**
     * Used to get the {@link MutableComponent} name of this spiritual_root for translation.
     */
    public MutableComponent getDisplayName() {
        return this.getRealm().getName();
    }

    public MutableComponent getChatDisplayName(boolean withDescription) {
        Style style = Style.EMPTY.withColor(ChatFormatting.GRAY);
        if (withDescription) {
            MutableComponent hoverMessage = getDisplayName().append("\n");
            hoverMessage.append(this.getRealm().getName().withStyle(ChatFormatting.GRAY));
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
        }

        MutableComponent component = Component.literal("[").append(getDisplayName()).append("]");
        return component.withStyle(style);
    }

    public MutableComponent getTrackedName() {
        return this.getRealm().getTrackedName();
    }

    /**
     * Return base health for this {@link RealmInstance}
     */
    public double getBaseHealth() {
        return this.getRealm().getBaseHealth();
    }

    /**
     * Return {@link Pair} of min and max Qi for this {@link RealmInstance}
     */
    public Pair<Float, Float> getBaseQiRange() {
        return this.getRealm().getBaseQiRange();
    }

    /**
     * Return base attack damage for this {@link RealmInstance}
     */
    public double getBaseAttackDamage() {
        return this.getRealm().getBaseAttackDamage();
    }

    /**
     * Return base attack speed for this {@link RealmInstance}
     */
    public double getBaseAttackSpeed() {
        return this.getRealm().getBaseAttackSpeed();
    }

    /**
     * Return knock back Resistance for this {@link RealmInstance}
     */
    public double getKnockBackResistance() {
        return this.getRealm().getKnockBackResistance();
    }

    /**
     * Return jump height for this {@link RealmInstance}
     */
    public double getJumpHeight() {
        return this.getRealm().getJumpHeight();
    }

    /**
     * Return movement speed for this {@link RealmInstance}
     */
    public double getMovementSpeed() {
        return this.getRealm().getMovementSpeed();
    }

    /**
     * Return sprint speed for this {@link RealmInstance}
     */
    public double getSprintSpeed() {
        return this.getRealm().getSprintSpeed();
    }

    /**
     * Return min Qi from {@link #getBaseQiRange()} for this {@link RealmInstance}
     */
    public float getMinBaseQi() {
        return this.getRealm().getMinBaseQi();
    }

    /**
     * Return max Qi from {@link #getBaseQiRange()} for this {@link RealmInstance}
     */
    public float getMaxBaseQi() {
        return this.getRealm().getMaxBaseQi();
    }

    public double getCoefficient() {
        return this.getRealm().getCoefficient();
    }

    /**
     * Returns a list of all {@link Realm} that this Realm can break through into.
     * </p>
     *
     * @param living Affected {@link LivingEntity} breakthrough this spiritual_root.
     */
    public List<Realm> getNextBreakthroughs(LivingEntity living) {
        return this.getRealm().getNextBreakthroughs(this, living);
    }

    /**
     * Returns a list of all {@link Realm} that breakthrough into this Realm.
     * </p>
     *
     * @param living Affected {@link LivingEntity} being this spiritual_root.
     */
    public List<Realm> getPreviousBreakthroughs(LivingEntity living) {
        return this.getRealm().getPreviousBreakthroughs(this, living);
    }

    /**
     * Returns the default {@link Realm} that this Realm breakthrough into.
     * </p>
     *
     * @param living Affected {@link LivingEntity} breakthrough this spiritual_root.
     */
    @Nullable
    public Realm getDefaultBreakthrough(LivingEntity living) {
        return this.getRealm().getDefaultBreakthrough(this, living);
    }

    /**
     * Called when the {@link LivingEntity} sets to this spiritual_root.
     *
     * @param living Affected {@link LivingEntity} sets to this spiritual_root.
     */
    public void onSet(LivingEntity living) {
        this.getRealm().onSet(this, living);
    }

    /**
     * Called when the {@link LivingEntity} reach this Realm.
     *
     * @param living Affected {@link LivingEntity} reach this Realm.
     */
    public void onReach(LivingEntity living) {
        this.getRealm().onReach(this, living);
    }

    /**
     * Called when the {@link LivingEntity} track on GUI this Realm.
     *
     * @param living Affected {@link LivingEntity} track on GUI this Realm.
     */
    public void onTrack(LivingEntity living) {
        this.getRealm().onTrack(this, living);
    }

    /**
     * Called when the {@link LivingEntity} breakthrough this Realm.
     *
     * @param entity Affected {@link LivingEntity} breakthrough this Realm.
     */
    public void onBreakthrough(LivingEntity entity) {
        this.getRealm().onBreakthrough(this, entity);
    }

    /**
     * Applies the attribute modifiers of this instance on the {@link LivingEntity} when set.
     *
     * @param entity Affected {@link LivingEntity} being thisrealm.
     */
    public void addAttributeModifiers(LivingEntity entity) {
        this.getRealm().addAttributeModifiers(this, entity);
    }

    /**
     * Removes the attribute modifiers of this instance from the {@link LivingEntity} when changing spiritual_root.
     *
     * @param entity Affected {@link LivingEntity} being this spiritual_root.
     */
    public void removeAttributeModifiers(LivingEntity entity) {
        this.getRealm().removeAttributeModifiers(this, entity);
    }

    /**
     * Returns the dimension that {@link LivingEntity} respawns at as this Realm.
     * Decides whether if the game should spawn a 3x3 platform of {@link BlockState} when no valid spawn is found.
     * </p>
     *
     * @param player Affected {@link LivingEntity} being this spiritual_root.
     */
    public Pair<ResourceKey<Level>, BlockState> getRespawnDimension(LivingEntity player) {
        return this.getRealm().getRespawnDimension(this, player);
    }

    public boolean passivelyFriendlyWith(LivingEntity entity) {
        return false;
    }

    public boolean canFly() {
        return false;
    }

    /**
     * Called every tick if this instance is set for {@link LivingEntity}.
     *
     * @param living Affected {@link LivingEntity} being this Realm.
     */
    public void onTick(LivingEntity living) {
        this.getRealm().onTick(this, living);
    }


    @Override
    public RealmInstance clone() {
        try {
            RealmInstance clone = (RealmInstance) super.clone();
            clone.dirty = this.dirty;
            if (this.tag != null) clone.tag = this.tag.copy();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
