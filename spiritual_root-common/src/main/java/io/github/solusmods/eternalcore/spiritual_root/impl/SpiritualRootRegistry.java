package io.github.solusmods.eternalcore.spiritual_root.impl;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot;
import io.github.solusmods.eternalcore.spiritual_root.ModuleConstants;
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRoot;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpiritualRootRegistry {
    private static final ResourceLocation registryId = EternalCoreSpiritualRoot.create("spiritual_roots");
    public static final Registrar<SpiritualRoot> SPIRITUAL_ROOTS = RegistrarManager.get(ModuleConstants.MOD_ID).<SpiritualRoot>builder(registryId)
            .syncToClients().build();
    public static final ResourceKey<Registry<SpiritualRoot>> KEY = (ResourceKey<Registry<SpiritualRoot>>) SPIRITUAL_ROOTS.key();



    public static void init() {
    }
}
