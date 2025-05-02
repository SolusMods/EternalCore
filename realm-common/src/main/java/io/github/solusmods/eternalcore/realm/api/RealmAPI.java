package io.github.solusmods.eternalcore.realm.api;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import io.github.solusmods.eternalcore.realm.impl.RealmRegistry;
import io.github.solusmods.eternalcore.realm.impl.RealmStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RealmAPI {
    /**
     * This Method returns the {@link Realm} Registry.
     * It can be used to load {@link Realm}s from the Registry.
     */
    public static Registrar<Realm> getRealmRegistry() {
        return RealmRegistry.REALMS;
    }

    /**
     * This Method returns the Registry Key of the {@link RealmRegistry}.
     * It can be used to create {@link DeferredRegister} instances
     */
    public static ResourceKey<Registry<Realm>> getRealmRegistryKey() {
        return RealmRegistry.KEY;
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
}
