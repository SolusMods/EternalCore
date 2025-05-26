package io.github.solusmods.eternalcore.abilities.api;

import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage;
import lombok.NonNull;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;

public interface Abilities {
    void markDirty();

    Collection<AbilityInstance> getLearnedAbilities();

    /**
     * Updates a ability instance and optionally synchronizes the change across the network.
     * <p>
     * @param updatedInstance The instance to update
     * @param sync If true, synchronizes the change to all clients/server
     */
    void updateAbility(AbilityInstance updatedInstance, boolean sync);

    default boolean learnAbility(@NotNull ResourceLocation abilityId) {
        return learnAbility(AbilityAPI.getAbilityRegistry().get(abilityId).createDefaultInstance());
    }

    default boolean learnAbility(@NotNull ResourceLocation abilityId, MutableComponent component) {
        return learnAbility(AbilityAPI.getAbilityRegistry().get(abilityId).createDefaultInstance(), component);
    }

    default boolean learnAbility(@NonNull Ability ability) {
        return learnAbility(ability.createDefaultInstance());
    }

    default boolean learnAbility(@NonNull Ability ability, MutableComponent component) {
        return learnAbility(ability.createDefaultInstance(), component);
    }

    default boolean learnAbility(AbilityInstance instance) {
        return learnAbility(instance, Component.translatable("eternalcore.ability.learn_ability", instance.getChatDisplayName(true)));
    }

    boolean learnAbility(AbilityInstance instance, MutableComponent component);

    Optional<AbilityInstance> getAbility(@NotNull ResourceLocation abilityId);

    default Optional<AbilityInstance> getAbility(@NonNull Ability ability) {
        return getAbility(ability.getRegistryName());
    }

    void forgetAbility(@NotNull ResourceLocation abilityId, @Nullable MutableComponent component);

    default void forgetAbility(@NotNull ResourceLocation abilityId) {
        forgetAbility(abilityId, null);
    }

    default void forgetAbility(@NonNull Ability ability, @Nullable MutableComponent component) {
        forgetAbility(ability.getRegistryName(), component);
    }

    default void forgetAbility(@NonNull Ability ability) {
        forgetAbility(ability.getRegistryName());
    }

    default void forgetAbility(@NonNull AbilityInstance instance, @Nullable MutableComponent component) {
        forgetAbility(instance.getAbilityId(), component);
    }

    default void forgetAbility(@NonNull AbilityInstance instance) {
        forgetAbility(instance.getAbilityId());
    }

    void forEachAbility(BiConsumer<AbilityStorage, AbilityInstance> abilityInstanceConsumer);
}
