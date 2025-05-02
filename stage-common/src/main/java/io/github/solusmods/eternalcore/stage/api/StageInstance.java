package io.github.solusmods.eternalcore.stage.api;

import com.mojang.datafixers.util.Pair;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class StageInstance {
    public static final String STAGE_KEY = "stage";
    protected final RegistrySupplier<Stage> stageRegistrySupplier;
    @Nullable
    private CompoundTag tag = null;
    @Getter
    private boolean dirty = false;

    protected StageInstance(Stage stage) {
        this.stageRegistrySupplier = StageAPI.getStageRegistry().delegate(StageAPI.getStageRegistry().getId(stage));
    }

    /**
     * Can be used to load a {@link StageInstance} from a {@link CompoundTag}.
     * <p>
     * The {@link CompoundTag} has to be created though {@link StageInstance#toNBT()}
     */
    public static StageInstance fromNBT(CompoundTag tag) throws NullPointerException {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString(STAGE_KEY));
        Stage stage = StageAPI.getStageRegistry().get(location);
        if (stage == null) throw new NullPointerException("No stage found for location: " + location);
        StageInstance instance = stage.createDefaultInstance();
        instance.deserialize(tag);
        return instance;
    }

    /**
     * Used to get the {@link Stage} type of this Instance.
     */
    public Stage getStage() {
        return stageRegistrySupplier.get();
    }

    public ResourceLocation getStageId() {
        return this.stageRegistrySupplier.getId();
    }

    /**
     * Used to get the type of this {@link Stage}.
     */
    public Stage.Type getType() {
        return this.getStage().getType();
    }

    /**
     * Used to create an exact copy of the current instance.
     */
    public StageInstance copy() {
        StageInstance clone = new StageInstance(getStage());
        clone.dirty = this.dirty;
        if (this.tag != null) clone.tag = this.tag.copy();
        return clone;
    }

    /**
     * This method is used to ensure that all required information are stored.
     * <p>
     * Override {@link StageInstance#serialize(CompoundTag)} to store your custom Data.
     */
    public final CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(STAGE_KEY, this.getStageId().toString());
        serialize(nbt);
        return nbt;
    }

    /**
     * Can be used to save custom data.
     *
     * @param nbt Tag with data from {@link StageInstance#fromNBT(CompoundTag)}
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
     * This Method is invoked to indicate that a {@link StageInstance} has been synced with the clients.
     * <p>
     * Do <strong>NOT</strong> use that method on your own!
     */
    @ApiStatus.Internal
    public void resetDirty() {
        this.dirty = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StageInstance instance = (StageInstance) o;
        return this.getStageId().equals(instance.getStageId()) &&
                stageRegistrySupplier.getRegistryKey().equals(instance.stageRegistrySupplier.getRegistryKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getStageId(), stageRegistrySupplier.getRegistryKey());
    }

    public boolean is(TagKey<Stage> tag) {
        return this.stageRegistrySupplier.is(tag);
    }

    /**
     * Used to get the {@link MutableComponent} name of this stage for translation.
     */
    public MutableComponent getDisplayName() {
        return this.getStage().getName();
    }

    public MutableComponent getChatDisplayName(boolean withDescription) {
        Style style = Style.EMPTY.withColor(ChatFormatting.GRAY);
        if (withDescription) {
            MutableComponent hoverMessage = getDisplayName().append("\n");
            hoverMessage.append(this.getStage().getName().withStyle(ChatFormatting.GRAY));
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
        }

        MutableComponent component = Component.literal("[").append(getDisplayName()).append("]");
        return component.withStyle(style);
    }

    /**
     * Return {@link Pair} of min and max Qi for this {@link StageInstance}
     */
    public Pair<Float, Float> getBaseQiRange() {
        return this.getStage().getBaseQiRange();
    }

    /**
     * Return min Qi from {@link #getBaseQiRange()} for this {@link StageInstance}
     */
    public float getMinBaseQi() {
        return this.getStage().getMinBaseQi();
    }

    /**
     * Return max Qi from {@link #getBaseQiRange()} for this {@link StageInstance}
     */
    public float getMaxBaseQi() {
        return this.getStage().getMaxBaseQi();
    }

    /**
     * Return stage effect
     *
     * @param entity Affected {@link LivingEntity} for this stage
     */
    public Changeable<MobEffectInstance> getEffect(LivingEntity entity) {
        return this.getStage().getEffect(this, entity);
    }

    /**
     * Returns a list of all {@link Stage} that this Stage can break through into.
     * </p>
     *
     * @param entity Affected {@link LivingEntity} breakthrough this stage.
     */
    public List<Stage> getNextBreakthroughs(LivingEntity entity) {
        return this.getStage().getNextBreakthroughs(this, entity);
    }

    /**
     * Returns a list of all {@link Stage} that breakthrough into this Stage.
     * </p>
     *
     * @param living Affected {@link LivingEntity} being this stage.
     */
    public List<Stage> getPreviousBreakthroughs(LivingEntity living) {
        return this.getStage().getPreviousBreakthroughs(this, living);
    }

    /**
     * Returns the default {@link Stage} that this Stage breakthrough into.
     * </p>
     *
     * @param living Affected {@link LivingEntity} evolving this stage.
     */
    @Nullable
    public Stage getDefaultBreakthrough(LivingEntity living) {
        return this.getStage().getDefaultBreakthrough(this, living);
    }

    public List<MutableComponent> getInfo(LivingEntity living) {
        return this.getStage().getInfo(this, living);
    }

    /**
     * Called when the {@link LivingEntity} sets to this Stage.
     *
     * @param living Affected {@link LivingEntity} sets to this Stage.
     */
    public void onSet(LivingEntity living) {
        this.getStage().onSet(this, living);
    }

    /**
     * Called when the {@link LivingEntity} reach this Stage.
     *
     * @param living Affected {@link LivingEntity} reach this Stage.
     */
    public void onReach(LivingEntity living) {
        this.getStage().onReach(this, living);
    }

    /**
     * Called when the {@link LivingEntity} track on GUI this Stage.
     *
     * @param living Affected {@link LivingEntity} track on GUI this Stage.
     */
    public void onTrack(LivingEntity living) {
        this.getStage().onTrack(this, living);
    }

    /**
     * Called every tick for this Stage.
     *
     * @param living Affected {@link LivingEntity} track on GUI this Stage.
     */
    public void onTick(LivingEntity living) {
        this.getStage().onTick(this, living);
    }

    /**
     * Called when the {@link LivingEntity} breakthrough this Stage.
     *
     * @param entity Affected {@link LivingEntity} breakthrough this Stage.
     */
    public void onBreakthrough(LivingEntity entity) {
        this.getStage().onBreakthrough(this, entity);
    }
}