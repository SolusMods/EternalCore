package io.github.solusmods.eternalcore.abilities.api;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.abilities.impl.AbilityRegistry;
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage;
import io.github.solusmods.eternalcore.abilities.impl.network.InternalAbilityPacketActions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    /**
     * Send {@link InternalAbilityPacketActions#sendAbilityActivationPacket} with a DistExecutor on client side.
     * Used when player press an ability activation key bind.
     *
     * @see InternalAbilityPacketActions#sendAbilityActivationPacket
     */
    public static void abilityActivationPacket(ResourceLocation ability, int keyNumber, int mode) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalAbilityPacketActions.sendAbilityActivationPacket(ability, keyNumber, mode);
        }
    }

    /**
     * Send {@link InternalAbilityPacketActions#sendAbilityReleasePacket} with a DistExecutor on client side.
     * Used when player release an ability activation key bind.
     *
     * @see InternalAbilityPacketActions#sendAbilityReleasePacket(ResourceLocation, int, int, int) 
     */
    public static void abilityReleasePacket(ResourceLocation ability, int keyNumber, int mode, int heldTicks) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalAbilityPacketActions.sendAbilityReleasePacket(ability, keyNumber, mode, heldTicks);
        }
    }

    /**
     * Send {@link InternalAbilityPacketActions#sendAbilityTogglePacket} with a DistExecutor on client side.
     * Used when player press a ability toggle key bind.
     *
     * @see InternalAbilityPacketActions#sendAbilityTogglePacket
     */
    public static void abilityTogglePacket(ResourceLocation ability) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalAbilityPacketActions.sendAbilityTogglePacket(ability);
        }
    }
}
