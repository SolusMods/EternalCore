package io.github.solusmods.eternalcore.api.stage;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IReachedStages {

    Collection<AbstractStage> getReachedStages();

    default boolean addStage(@NotNull ResourceLocation stageId, boolean teleportToSpawn) {
        return addStage(stageId, teleportToSpawn, null);
    }

    default boolean addStage(@NotNull ResourceLocation stageId, boolean teleportToSpawn, @Nullable MutableComponent component) {
        AbstractStage abstractStage = StageAPI.getStageRegistry().get(stageId);
        if (abstractStage == null) return false;
        return addStage(abstractStage, false, teleportToSpawn, component);
    }

    default boolean addStage(@NonNull AbstractStage abstractStage, boolean teleportToSpawn) {
        return addStage(abstractStage, teleportToSpawn, null);
    }

    default boolean addStage(@NonNull AbstractStage abstractStage, boolean teleportToSpawn, @Nullable MutableComponent component) {
        return addStage(abstractStage, false, teleportToSpawn, component);
    }

    boolean addStage(AbstractStage stage, boolean breakthrough, boolean teleportToSpawn, @Nullable MutableComponent component);

    void markDirty();
}
