package io.github.solusmods.eternalcore.spiritual_root.api;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.element.api.Element;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
@Setter
public class SpiritualRootInstance implements Cloneable {
    public static final String KEY = "spiritual_root";
    public static final String LEVEL_KEY = "level";
    public static final String EXPERIENCE_KEY = "experience";
    public static final String STRENGTH_KEY = "strength";
    protected final RegistrySupplier<SpiritualRoot> spiritualRootRegistrySupplier;
    private float strength = 0.1F;
    private RootLevels level = RootLevels.O;
    private float experience = 0.0F;
    @Nullable
    private CompoundTag tag = null;
    @Getter
    private boolean dirty = false;

    protected SpiritualRootInstance(SpiritualRoot spiritualRoot) {
        this.spiritualRootRegistrySupplier = SpiritualRootAPI.getSpiritualRootRegistry().delegate(SpiritualRootAPI.getSpiritualRootRegistry().getId(spiritualRoot));
    }

    /**
     * Can be used to load a {@link SpiritualRootInstance} from a {@link CompoundTag}.
     * <p>
     * The {@link CompoundTag} has to be created though {@link SpiritualRootInstance#toNBT()}
     */
    public static SpiritualRootInstance fromNBT(CompoundTag tag) throws NullPointerException {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString(KEY));
        SpiritualRoot spiritualRoot = SpiritualRootAPI.getSpiritualRootRegistry().get(location);
        if (spiritualRoot == null) throw new NullPointerException("No spiritualRoot found for location: " + location);
        SpiritualRootInstance instance = spiritualRoot.createDefaultInstance();
        instance.deserialize(tag);
        return instance;
    }

    /**
     * Used to get the {@link SpiritualRoot} type of this Instance.
     */
    public SpiritualRoot getSpiritualRoot() {
        return spiritualRootRegistrySupplier.get();
    }

    public ResourceLocation getSpiritualRootId() {
        return this.spiritualRootRegistrySupplier.getId();
    }

    /**
     * This method is used to ensure that all required information are stored.
     * <p>
     * Override {@link #serialize(CompoundTag)} to store your custom Data.
     */
    public final CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(KEY, this.getSpiritualRootId().toString());
        serialize(nbt);
        return nbt;
    }

    /**
     * Can be used to save custom data.
     *
     * @param nbt Tag with data from {@link #fromNBT(CompoundTag)}
     */
    public CompoundTag serialize(CompoundTag nbt) {
        if (this.tag != null) nbt.put("tag", this.tag.copy());
        nbt.putInt(LEVEL_KEY, this.level.getLevel());
        nbt.putFloat(EXPERIENCE_KEY, this.experience);
        nbt.putFloat(STRENGTH_KEY, this.strength);
        return nbt;
    }

    /**
     * Can be used to load custom data.
     */
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
        if (tag.contains(LEVEL_KEY)) this.level = RootLevels.byId(tag.getInt(LEVEL_KEY));
        if (tag.contains(EXPERIENCE_KEY)) this.experience = tag.getFloat(EXPERIENCE_KEY);
        if (tag.contains(STRENGTH_KEY)) this.strength = tag.getFloat(STRENGTH_KEY);
    }

    /**
     * Marks the current instance as dirty.
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * This Method is invoked to indicate that a {@link SpiritualRootInstance} has been synced with the clients.
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
        SpiritualRootInstance instance = (SpiritualRootInstance) o;
        return this.getSpiritualRootId().equals(instance.getSpiritualRootId()) &&
                spiritualRootRegistrySupplier.getRegistryKey().equals(instance.spiritualRootRegistrySupplier.getRegistryKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getSpiritualRootId(), spiritualRootRegistrySupplier.getRegistryKey());
    }

    public boolean is(TagKey<SpiritualRoot> tag) {
        return this.spiritualRootRegistrySupplier.is(tag);
    }

    /**
     * Used to get the {@link MutableComponent} name of this spiritual root for translation.
     */
    public MutableComponent getDisplayName() {
        return this.getSpiritualRoot().getName();
    }

    @Override
    public SpiritualRootInstance clone() {
        try {
            SpiritualRootInstance clone = (SpiritualRootInstance) super.clone();
            clone.dirty = this.dirty;
            if (this.tag != null) clone.tag = this.tag.copy();
            clone.level = this.level;
            clone.experience = this.experience;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public RootLevels getNextLevel() {
        int level = getLevel().getLevel();
        if (getLevel().getLevel() < RootLevels.byId(10).getLevel()) {
            level = level + 1;
        } else {
            level = RootLevels.X.getLevel();
        }
        return RootLevels.byId(level);
    }

    public void setLevel(RootLevels level) {
        this.level = level;
        markDirty();
    }

    public void setExperience(float experience) {
        this.experience = experience;
        markDirty();
    }

    public void setStrength(float strength) {
        this.strength = strength;
        markDirty();
    }

    public RootType getType() {
        return this.getSpiritualRoot().getType();
    }

    public @Nullable Element getElement(LivingEntity entity){
        return this.getSpiritualRoot().getElement(this, entity);
    };

    public void addExperience(LivingEntity living, float experience) {
        this.getSpiritualRoot().addExperience(this, living, experience);
    }

    public void increaseStrength(LivingEntity living, float amount) {
        this.getSpiritualRoot().increaseStrength(this, living, amount);
    }

    /**
     * Returns the default {@link SpiritualRoot} that this {@link SpiritualRoot} advance into.
     * </p>
     *
     * @param living Affected {@link LivingEntity} advance this {@link SpiritualRoot}.
     */
    @Nullable
    public SpiritualRoot getAdvanced(LivingEntity living) {
        return this.getSpiritualRoot().getAdvanced(this, living);
    }

    /**
     * Applies the attribute modifiers of this instance on the {@link LivingEntity} when set.
     *
     * @param entity Affected {@link LivingEntity} being this {@link SpiritualRoot}.
     */
    public void addAttributeModifiers(LivingEntity entity) {
        this.getSpiritualRoot().addAttributeModifiers(this, entity);
    }

    /**
     * Removes the attribute modifiers of this instance from the {@link LivingEntity} when changing {@link SpiritualRoot}.
     *
     * @param entity Affected {@link LivingEntity} being this {@link SpiritualRoot}.
     */
    public void removeAttributeModifiers(LivingEntity entity) {
        this.getSpiritualRoot().removeAttributeModifiers(this, entity);
    }

    /**
     * Called when the {@link LivingEntity} mastering this {@link SpiritualRoot}.
     *
     * @param living Affected {@link LivingEntity} who mastering this {@link SpiritualRoot}.
     */
    public void onMastered(LivingEntity living) {
        this.getSpiritualRoot().onMastered(this, living);
    }

    /**
     * Called when the {@link LivingEntity} gets this {@link SpiritualRoot}.
     *
     * @param living Affected {@link LivingEntity} who gets this {@link SpiritualRoot}.
     */
    public void onAdd(LivingEntity living) {
        this.getSpiritualRoot().onAdd(this, living);
    }

    /**
     * Called when the {@link LivingEntity} advance this {@link SpiritualRoot}.
     *
     * @param living Affected {@link LivingEntity} who advance this {@link SpiritualRoot}.
     */
    public void onAdvance(LivingEntity living) {
        this.getSpiritualRoot().onAdvance(this, living);
    }

    public @Nullable SpiritualRoot getOpposite(LivingEntity entity){
        return getSpiritualRoot().getOpposite(this, entity);
    }
}
