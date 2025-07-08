package io.github.solusmods.eternalcore.api.stage;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Stages {

    Optional<AbstractStage> getStage();

    default boolean setStage(@NotNull ResourceLocation stageId, boolean notify) {
        return setStage(stageId, notify, null);
    }

    default boolean setStage(@NotNull ResourceLocation stageId, boolean notify, @Nullable MutableComponent component) {
        AbstractStage abstractStage = StageAPI.getStageRegistry().get(stageId);
        if (abstractStage == null) return false;
        return setStage(abstractStage, false, notify, component);
    }

    default boolean setStage(@NonNull AbstractStage abstractStage, boolean notify) {
        return setStage(abstractStage, notify, null);
    }

    default boolean setStage(@NonNull AbstractStage abstractStage, boolean notify, @Nullable MutableComponent component) {
        return setStage(abstractStage, false, notify, component);
    }

    boolean setStage(AbstractStage stage, boolean advancement, boolean notify, @Nullable MutableComponent component);

    default boolean breakthroughStage(@NotNull ResourceLocation stageId) {
        return breakthroughStage(stageId, null);
    }

    default boolean breakthroughStage(@NotNull ResourceLocation stageId, @Nullable MutableComponent component) {
        AbstractStage abstractStage = StageAPI.getStageRegistry().get(stageId);
        if (abstractStage == null) return false;
        return setStage(abstractStage, true, false, component);
    }

    default boolean breakthroughStage(@NonNull AbstractStage abstractStage) {
        return breakthroughStage(abstractStage, null);
    }

    default boolean breakthroughStage(@NonNull AbstractStage abstractStage, @Nullable MutableComponent component) {
        return setStage(abstractStage, true, false, component);
    }

    void markDirty();
}
