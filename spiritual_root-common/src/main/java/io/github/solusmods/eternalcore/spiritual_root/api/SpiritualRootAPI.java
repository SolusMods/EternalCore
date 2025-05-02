package io.github.solusmods.eternalcore.spiritual_root.api;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootRegistry;
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootStorage;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;

public class SpiritualRootAPI {
    private SpiritualRootAPI() {
    }

    /**
     * This Method returns the {@link SpiritualRoot} Registry.
     * It can be used to load {@link SpiritualRoot}s from the Registry.
     */
    public static Registrar<SpiritualRoot> getSpiritualRootRegistry() {
        return SpiritualRootRegistry.SPIRITUAL_ROOTS;
    }

    /**
     * This Method returns the Registry Key of the {@link SpiritualRootRegistry}.
     * It can be used to create {@link DeferredRegister} instances
     */
    public static ResourceKey<Registry<SpiritualRoot>> getSpiritualRootRegistryKey() {
        return SpiritualRootRegistry.KEY;
    }

    /**
     * Can be used to load the {@link SpiritualRootStorage} from an {@link LivingEntity}.
     */
    public static SpiritualRoots getSpiritualRootFrom(@NonNull LivingEntity entity) {
        return entity.eternalCore$getStorage(SpiritualRootStorage.getKey());
    }
}
