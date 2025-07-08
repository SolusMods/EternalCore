package io.github.solusmods.eternalcore.api.spiritual_root;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.api.registry.SpiritualRootRegistry;
import io.github.solusmods.eternalcore.impl.spiritual_root.SpiritualRootStorage;
import io.github.solusmods.eternalcore.impl.spiritual_root.network.InternalSpiritualRootPacketActions;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class SpiritualRootAPI {
    private SpiritualRootAPI() {
    }

    /**
     * This Method returns the {@link AbstractSpiritualRoot} Registry.
     * It can be used to load {@link AbstractSpiritualRoot}s from the Registry.
     */
    public static Registrar<AbstractSpiritualRoot> getSpiritualRootRegistry() {
        return SpiritualRootRegistry.SPIRITUAL_ROOTS;
    }

    /**
     * This Method returns the Registry Key of the {@link SpiritualRootRegistry}.
     * It can be used to create {@link DeferredRegister} instances
     */
    public static ResourceKey<Registry<AbstractSpiritualRoot>> getSpiritualRootRegistryKey() {
        return SpiritualRootRegistry.KEY;
    }

    /**
     * Can be used to load the {@link SpiritualRootStorage} from an {@link LivingEntity}.
     */
    public static SpiritualRoots getSpiritualRootFrom(@NonNull LivingEntity entity) {
        return entity.eternalCore$getStorage(SpiritualRootStorage.getKey());
    }

    public static Optional<SpiritualRootStorage> getStorageOptional(LivingEntity entity) {
        return entity.eternalCore$getStorageOptional(SpiritualRootStorage.getKey());
    }

    /**
     * Send {@link InternalSpiritualRootPacketActions#sendSpiritualRootAdvancePacket} with a DistExecutor on client side.
     * Used when player advance his a spiritualRoot.
     *
     * @see InternalSpiritualRootPacketActions#sendSpiritualRootAdvancePacket
     */
    public static void spiritualRootAdvancePacket(ResourceLocation location) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalSpiritualRootPacketActions.sendSpiritualRootAdvancePacket(location);
        }
    }
}
