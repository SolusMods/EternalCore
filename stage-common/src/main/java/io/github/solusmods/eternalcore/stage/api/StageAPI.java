package io.github.solusmods.eternalcore.stage.api;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import io.github.solusmods.eternalcore.stage.impl.StageRegistry;
import io.github.solusmods.eternalcore.stage.impl.StageStorage;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;

public class StageAPI {
    private StageAPI() {
    }

    /**
     * This Method returns the {@link Stage} Registry.
     * It can be used to load {@link Stage}s from the Registry.
     */
    public static Registrar<Stage> getStageRegistry() {
        return StageRegistry.STAGES;
    }

    /**
     * This Method returns the Registry Key of the {@link StageRegistry}.
     * It can be used to create {@link DeferredRegister} instances
     */
    public static ResourceKey<Registry<Stage>> getStageRegistryKey() {
        return StageRegistry.KEY;
    }

    /**
     * Can be used to load the {@link StageStorage} from an {@link LivingEntity}.
     */
    public static Stages getStageFrom(@NonNull LivingEntity entity) {
        return entity.eternalCraft$getStorage(StageStorage.getKey());
    }

    /**
     * Can be used to load the {@link StageStorage} from an {@link LivingEntity}.
     */
    public static IReachedStages getReachedStagesFrom(@NonNull LivingEntity entity) {
        return entity.eternalCraft$getStorage(StageStorage.getKey());
    }
}
