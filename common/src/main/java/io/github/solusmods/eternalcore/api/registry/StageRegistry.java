package io.github.solusmods.eternalcore.api.registry;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import static io.github.solusmods.eternalcore.EternalCore.REGISTRIES;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
public class StageRegistry {

    private static final ResourceLocation registryId = EternalCore.create("stages");

    /**
     * This is the {@link Registrar} for {@link AbstractStage}s.
     * It can be used to register new {@link AbstractStage}s.
     */
    public static final Registrar<AbstractStage> STAGES = REGISTRIES.get().<AbstractStage>builder(registryId)
            .syncToClients().build();

    /**
     * This is the {@link ResourceKey} for the {@link AbstractStage} Registry.
     * It can be used to create {@link dev.architectury.registry.registries.DeferredRegister} instances
     */
    public static final ResourceKey<Registry<AbstractStage>> KEY = (ResourceKey<Registry<AbstractStage>>) STAGES.key();

    /**
     * This Method returns the {@link AbstractStage} Registry.
     * It can be used to load {@link AbstractStage}s from the Registry.
     */
    public static Registrar<AbstractStage> getStageRegistry() {
        return STAGES;
    }

    /**
     * This Method returns the Registry Key of the {@link StageRegistry}.
     * It can be used to create {@link dev.architectury.registry.registries.DeferredRegister} instances
     */
    public static ResourceKey<Registry<AbstractStage>> getRegistryKey() {
        return KEY;
    }

    /**
     * This Method returns a {@link RegistrySupplier} for the given {@link AbstractStage}.
     * It can be used to get the {@link ResourceLocation} of the {@link AbstractStage}.
     *
     * @param abstractStage The {@link AbstractStage} to get the {@link RegistrySupplier} for.
     * @return The {@link RegistrySupplier} for the given {@link AbstractStage}.
     */
    public static RegistrySupplier<AbstractStage> getRegistrySupplier(AbstractStage abstractStage) {
        return getStageRegistry().delegate(getStageRegistry().getId(abstractStage));
    }

    public static void init() {
    }
}
