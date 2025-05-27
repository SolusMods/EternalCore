package io.github.solusmods.eternalcore.stage.impl;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import io.github.solusmods.eternalcore.stage.EternalCoreStage;
import io.github.solusmods.eternalcore.stage.ModuleConstants;
import io.github.solusmods.eternalcore.stage.api.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
public class StageRegistry {

    private static final ResourceLocation registryId = EternalCoreStage.create("stages");
    // endregion
    public static final Registrar<Stage> STAGES = RegistrarManager.get(ModuleConstants.MOD_ID).<Stage>builder(registryId)
            .syncToClients().build();
    public static final ResourceKey<Registry<Stage>> KEY = (ResourceKey<Registry<Stage>>) STAGES.key();




    public static void init() {}
}
