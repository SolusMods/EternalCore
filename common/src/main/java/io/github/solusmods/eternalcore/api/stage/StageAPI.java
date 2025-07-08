package io.github.solusmods.eternalcore.api.stage;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.api.registry.StageRegistry;
import io.github.solusmods.eternalcore.impl.stage.StageStorage;
import io.github.solusmods.eternalcore.impl.stage.network.InternalStagePacketActions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StageAPI {


    /**
     * This Method returns the {@link AbstractStage} Registry.
     * It can be used to load {@link AbstractStage}s from the Registry.
     */
    public static Registrar<AbstractStage> getStageRegistry() {
        return StageRegistry.STAGES;
    }

    /**
     * This Method returns the Registry Key of the {@link StageRegistry}.
     * It can be used to create {@link DeferredRegister} instances
     */
    public static ResourceKey<Registry<AbstractStage>> getStageRegistryKey() {
        return StageRegistry.KEY;
    }

    /**
     * Can be used to load the {@link StageStorage} from an {@link LivingEntity}.
     */
    public static Stages getStageFrom(@NonNull LivingEntity entity) {
        return entity.eternalCore$getStorage(StageStorage.getKey());
    }

    /**
     * Can be used to load the {@link StageStorage} from an {@link LivingEntity}.
     */
    public static IReachedStages getReachedStagesFrom(@NonNull LivingEntity entity) {
        return entity.eternalCore$getStorage(StageStorage.getKey());
    }

    public static Optional<StageStorage> getStorageOptional(LivingEntity entity) {
        return entity.eternalCore$getStorageOptional(StageStorage.getKey());
    }

    /**
     * Send {@link InternalStagePacketActions#sendStageBreakthroughPacket} with a DistExecutor on client side.
     * Used when player Breakthrough into a stage.
     *
     * @see InternalStagePacketActions#sendStageBreakthroughPacket
     */
    public static void stageBreakthroughPacket(ResourceLocation location) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalStagePacketActions.sendStageBreakthroughPacket(location);
        }
    }
}
