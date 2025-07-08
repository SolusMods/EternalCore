package io.github.solusmods.eternalcore.api.realm;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.api.registry.RealmRegistry;
import io.github.solusmods.eternalcore.impl.realm.RealmStorage;
import io.github.solusmods.eternalcore.impl.realm.network.InternalRealmPacketActions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RealmAPI {
    /**
     * This Method returns the {@link AbstractRealm} Registry.
     * It can be used to load {@link AbstractRealm}s from the Registry.
     */
    public static Registrar<AbstractRealm> getRealmRegistry() {
        return RealmRegistry.getRealmRegistry();
    }

    /**
     * This Method returns the Registry Key of the {@link RealmRegistry}.
     * It can be used to create {@link DeferredRegister} instances
     */
    public static ResourceKey<Registry<AbstractRealm>> getRealmRegistryKey() {
        return RealmRegistry.getRegistryKey();
    }

    /**
     * Can be used to load the {@link RealmStorage} from an {@link LivingEntity}.
     */
    public static Realms getRealmFrom(@NonNull LivingEntity entity) {
        return entity.eternalCore$getStorage(RealmStorage.getKey());
    }

    /**
     * Can be used to load the {@link RealmStorage} from an {@link LivingEntity}.
     */
    public static IReachedRealms getReachedRealmsFrom(@NonNull LivingEntity entity) {
        return entity.eternalCore$getStorage(RealmStorage.getKey());
    }

    public static Optional<RealmStorage> getStorageOptional(LivingEntity entity) {
        return entity.eternalCore$getStorageOptional(RealmStorage.getKey());
    }

    /**
     * Send {@link InternalRealmPacketActions#sendRealmBreakthroughPacket} with a DistExecutor on client side.
     * Used when player break into a stage.
     *
     * @see InternalRealmPacketActions#sendRealmBreakthroughPacket(ResourceLocation)
     */
    public static void realmBreakthroughPacket(ResourceLocation location) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalRealmPacketActions.sendRealmBreakthroughPacket(location);
        }
    }
}
