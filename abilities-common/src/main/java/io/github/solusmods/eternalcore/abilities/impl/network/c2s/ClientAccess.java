package io.github.solusmods.eternalcore.abilities.impl.network.c2s;

import io.github.solusmods.eternalcore.abilities.api.Abilities;
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI;
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents;
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance;
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage;
import io.github.solusmods.eternalcore.abilities.impl.TickingAbility;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class ClientAccess {

    public static void handle(RequestAbilityActivationPacket packet, Player player) {
        if(player == null) return;
        Abilities storage = AbilityAPI.getAbilitiesFrom(player);
        storage.getAbility(packet.abilityId()).ifPresent(instance -> {
            Changeable<AbilityInstance> changeable = Changeable.of(instance);
            if (AbilityEvents.ACTIVATE_ABILITY.invoker().activateAbility(changeable, player, packet.keyNumber(), packet.mode()).isFalse()) return;

            AbilityInstance abilityInstance = changeable.get();
            if (abilityInstance == null) return;
            if(!abilityInstance.canInteractAbility(player)) return;

            if (packet.mode() < 0 || packet.mode() >= abilityInstance.getModes()) return;
            if (abilityInstance.onCoolDown(packet.mode()) && !abilityInstance.canIgnoreCoolDown(player, packet.mode())) return;

            abilityInstance.onPressed(player, packet.keyNumber(), packet.mode());
            abilityInstance.addHeldAttributeModifiers(player, packet.mode());
            AbilityStorage.tickingAbilities.put(player.getUUID(), new TickingAbility(abilityInstance.getAbility(), packet.mode()));
            storage.markDirty();
        });
    }

    public static void handle(RequestAbilityReleasePacket packet, Player player){
        if (player == null) return;
        AbilityStorage storage = StorageManager.getStorage(player, AbilityStorage.getKey());
        if (storage == null) return;
        storage.handleAbilityRelease(packet.abilityId(), packet.heldTick(), packet.keyNumber(), packet.mode());
    }

    public static void handle(RequestAbilityScrollPacket packet, Player player){
        if (player == null) return;

        Abilities storage = AbilityAPI.getAbilitiesFrom(player);
        for (ResourceLocation skillId : packet.abilityList()) {
            storage.getAbility(skillId).ifPresent(abilityInstance -> {

                Changeable<AbilityInstance> skillChangeable = Changeable.of(abilityInstance);
                Changeable<Double> deltaChangeable = Changeable.of(packet.delta());
                if (AbilityEvents.ABILITY_SCROLL.invoker().scroll(skillChangeable, player, deltaChangeable).isFalse()) return;

                AbilityInstance abilityInstance1 = skillChangeable.get();
                if (abilityInstance1 == null || deltaChangeable.isEmpty()) return;
                if (!abilityInstance1.canScroll(player)) return;
                if (!abilityInstance1.canInteractAbility(player)) return;

                abilityInstance1.onScroll(player, deltaChangeable.get(), 0);
                storage.markDirty();
            });
        }
    }

    public static void handle(RequestAbilityTogglePacket packet, Player player){
        if(player == null) return;
        Abilities storage = AbilityAPI.getAbilitiesFrom(player);
        storage.getAbility(packet.abilityId()).ifPresent(abilityInstance -> {
            Changeable<AbilityInstance> changeable = Changeable.of(abilityInstance);
            if (AbilityEvents.TOGGLE_ABILITY.invoker().toggleAbility(changeable, player).isFalse()) return;

            AbilityInstance skill = changeable.get();
            if (skill == null) return;
            if(!skill.canInteractAbility(player)) return;

            if(skill.isToggled()) {
                skill.setToggled(false);
                skill.onToggleOff(player);
            } else {
                skill.setToggled(true);
                skill.onToggleOn(player);
            }
            storage.markDirty();
        });
    }
}
