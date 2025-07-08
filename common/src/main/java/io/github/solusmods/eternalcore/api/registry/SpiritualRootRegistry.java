package io.github.solusmods.eternalcore.api.registry;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.qi_energy.ElementType;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import static io.github.solusmods.eternalcore.EternalCore.REGISTRIES;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
public class SpiritualRootRegistry {
    private static final ResourceLocation registryId = EternalCore.create("spiritual_roots");
    public static final Registrar<AbstractSpiritualRoot> SPIRITUAL_ROOTS = REGISTRIES.get().<AbstractSpiritualRoot>builder(registryId)
            .syncToClients().build();
    public static final ResourceKey<Registry<AbstractSpiritualRoot>> KEY = (ResourceKey<Registry<AbstractSpiritualRoot>>) SPIRITUAL_ROOTS.key();

    public static Registrar<AbstractSpiritualRoot> getSpiritualRootRegistry() {
        return SPIRITUAL_ROOTS;
    }

    public static ResourceKey<Registry<AbstractSpiritualRoot>> getRegistryKey() {
        return KEY;
    }

    public static RegistrySupplier<AbstractSpiritualRoot> getRegistrySupplier(AbstractSpiritualRoot abstractSpiritualRoot) {
        return getSpiritualRootRegistry().delegate(getSpiritualRootRegistry().getId(abstractSpiritualRoot));
    }


    public static void init() {
    }
}
