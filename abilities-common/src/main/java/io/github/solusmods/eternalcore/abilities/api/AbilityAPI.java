package io.github.solusmods.eternalcore.abilities.api;

import dev.architectury.registry.registries.Registrar;
import io.github.solusmods.eternalcore.abilities.impl.AbilityRegistry;
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AbilityAPI {

    /**
     * This Method returns the {@link Ability} Registry.
     * It can be used to load {@link Ability}s from the Registry.
     */
    public static Registrar<Ability> getAbilityRegistry() {
        return AbilityRegistry.ABILITIES;
    }

    /**
     * This Method returns the Registry Key of the {@link AbilityRegistry}.
     * It can be used to create {@link dev.architectury.registry.registries.DeferredRegister} instances
     */
    public static ResourceKey<Registry<Ability>> getAbilityRegistryKey() {
        return AbilityRegistry.KEY;
    }

    /**
     * Can be used to load the {@link AbilityStorage} from an {@link LivingEntity}.
     */
    public static Abilities getAbilitiesFrom(@NonNull LivingEntity entity) {
        return entity.eternalCore$getStorage(AbilityStorage.getKey());
    }
}
