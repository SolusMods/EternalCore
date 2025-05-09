package io.github.solusmods.eternalcore.stage.api;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IReachedStages {

    Collection<StageInstance> getReachedStages();

    default boolean addStage(@NotNull ResourceLocation stageId, boolean teleportToSpawn) {
        return addStage(stageId, teleportToSpawn, null);
    }

    default boolean addStage(@NotNull ResourceLocation stageId, boolean teleportToSpawn, @Nullable MutableComponent component) {
        Stage stage = StageAPI.getStageRegistry().get(stageId);
        if (stage == null) return false;
        return addStage(stage.createDefaultInstance(), false, teleportToSpawn, component);
    }

    default boolean addStage(@NonNull Stage stage, boolean teleportToSpawn) {
        return addStage(stage, teleportToSpawn, null);
    }

    default boolean addStage(@NonNull Stage stage, boolean teleportToSpawn, @Nullable MutableComponent component) {
        return addStage(stage.createDefaultInstance(), false, teleportToSpawn, component);
    }

    default boolean addStage(StageInstance instance, boolean breakthrough, boolean teleportToSpawn) {
        return addStage(instance, breakthrough, teleportToSpawn, null);
    }

    boolean addStage(StageInstance instance, boolean breakthrough, boolean teleportToSpawn, @Nullable MutableComponent component);

    void markDirty();

    void sync();
}
