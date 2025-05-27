package io.github.solusmods.eternalcore.stage.api;

import com.mojang.datafixers.util.Pair;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.stage.ModuleConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public abstract class Stage {
    private final Type type;

    /**
     * Return {@link Pair} of min and max Qi for this {@link Stage}
     *
     * @see StageInstance#getBaseQiRange()
     */
    public abstract Pair<Float, Float> getBaseQiRange();

    /**
     * Return min Qi from {@link Stage#getBaseQiRange()} for this {@link Stage}
     *
     * @see StageInstance#getMinBaseQi()
     */
    public float getMinBaseQi() {
        return this.getBaseQiRange().getFirst();
    }

    /**
     * Return max Qi from {@link Stage#getBaseQiRange()} for this {@link Stage}
     *
     * @see StageInstance#getMaxBaseQi()
     */
    public float getMaxBaseQi() {
        return this.getBaseQiRange().getSecond();
    }

    public Changeable<MobEffectInstance> getEffect(StageInstance instance, LivingEntity living) {
        return Changeable.of(null);
    }

    /**
     * Returns a list of all {@link Stage} that this Stage can break through into.
     *
     * @see StageInstance#getNextBreakthroughs(LivingEntity)
     */
    public abstract List<Stage> getNextBreakthroughs(StageInstance instance, LivingEntity living);

    /**
     * Returns a list of all {@link Stage} that break through into this Stage.
     *
     * @see StageInstance#getPreviousBreakthroughs(LivingEntity)
     */
    public abstract List<Stage> getPreviousBreakthroughs(StageInstance instance, LivingEntity living);

    public List<MutableComponent> getInfo(StageInstance instance, LivingEntity entity) {
        List<MutableComponent> info = new ArrayList<>();
        return info;
    }

    /**
     * Returns the default {@link Stage} that this Stage break through into.
     *
     * @see StageInstance#getDefaultBreakthrough(LivingEntity)
     */
    @Nullable
    public abstract Stage getDefaultBreakthrough(StageInstance instance, LivingEntity living);

    /**
     * Used to get the {@link ResourceLocation} id of this stage.
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return StageAPI.getStageRegistry().getId(this);
    }

    /**
     * Used to get the {@link MutableComponent} name of this stage for translation.
     */
    @Nullable
    public MutableComponent getName() {
        ResourceLocation id = this.getRegistryName();
        return id == null ? null : Component.translatable(String.format("%s.realm.stage.%s", id.getNamespace(), id.getPath().replace('/', '.')));
    }

    public String getNameTranslationKey() {
        return ((TranslatableContents) this.getName().getContents()).getKey();
    }

    /**
     * Called when the {@link LivingEntity} sets to this Stage.
     *
     * @see StageInstance#onSet(LivingEntity)
     */
    public void onSet(StageInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} reach to this Stage.
     *
     * @see StageInstance#onReach(LivingEntity)
     */
    public void onReach(StageInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} track to this Stage.
     *
     * @see StageInstance#onTrack(LivingEntity)
     */
    public void onTrack(StageInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Called every tick for this {@link Stage}.
     *
     * @see StageInstance#onTick(LivingEntity)
     */
    public void onTick(StageInstance instance, LivingEntity living) {
        StageEvents.STAGE_POST_TICK.invoker().tick(instance, living);
    }

    /**
     * Called when the {@link LivingEntity}
     * <br>
     * Breakthrough to next {@link Stage#getDefaultBreakthrough(StageInstance, LivingEntity)}.
     *
     * @see StageInstance#onBreakthrough(LivingEntity)
     */
    public void onBreakthrough(StageInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Used to create a {@link StageInstance} of this Stage.
     * <p>
     * Override this Method to use your extended version of {@link StageInstance}
     */
    public StageInstance createDefaultInstance() {
        return new StageInstance(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Stage stage = (Stage) o;
            return this.getRegistryName().equals(stage.getRegistryName());
        } else {
            return false;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        EARLY(1, 0, 1, Component.translatable("%s.stage.type.early".formatted(ModuleConstants.MOD_ID)), Color.GREEN),
        MIDDLE(2, 1, 2, Component.translatable("%s.stage.type.middle".formatted(ModuleConstants.MOD_ID)), Color.YELLOW),
        LATE(3, 2, 3, Component.translatable("%s.stage.type.late".formatted(ModuleConstants.MOD_ID)), Color.ORANGE),
        PEAK(4, 3, 4, Component.translatable("%s.stage.type.peak".formatted(ModuleConstants.MOD_ID)), Color.RED);

        private final int id;
        private final int min;
        private final int max;
        private final MutableComponent name;
        private final Color color;

        @Setter
        private boolean track;
    }
}