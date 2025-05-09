package io.github.solusmods.eternalcore.stage.api;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Stages {

    Optional<StageInstance> getStage();

    default boolean setStage(@NotNull ResourceLocation stageId, boolean notify) {
        return setStage(stageId, notify, null);
    }

    default boolean setStage(@NotNull ResourceLocation stageId, boolean notify, @Nullable MutableComponent component) {
        Stage stage = StageAPI.getStageRegistry().get(stageId);
        if (stage == null) return false;
        return setStage(stage.createDefaultInstance(), false, notify, component);
    }

    default boolean setStage(@NonNull Stage stage, boolean notify) {
        return setStage(stage, notify, null);
    }

    default boolean setStage(@NonNull Stage stage, boolean notify, @Nullable MutableComponent component) {
        return setStage(stage.createDefaultInstance(), false, notify, component);
    }

    default boolean setStage(StageInstance instance, boolean advancement, boolean notify) {
        return setStage(instance, advancement, notify, null);
    }

    boolean setStage(StageInstance instance, boolean advancement, boolean notify, @Nullable MutableComponent component);

    void markDirty();

    void sync();
}
