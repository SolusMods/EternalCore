package io.github.solusmods.eternalcore.abilities.impl.network;

import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.abilities.api.Ability;
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InternalAbilityPacketActions {
    
    /**
     * This Method filters {@link io.github.solusmods.eternalcore.abilities.api.Ability} that meets the conditions of the {@link io.github.solusmods.eternalcore.abilities.api.AbilityEvents.AbilityActivationEvent} then send packet for them.
     * Only executes on client using the dist executor.
     */
    public static void sendAbilityActivationPacket(ResourceLocation skillId, int keyNumber, int mode) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new RequestAbilityActivationPacket(keyNumber, skillId, mode));
    }

    /**
     * This Method filters {@link Ability} that meets the conditions of the {@link io.github.solusmods.eternalcore.abilities.api.AbilityEvents.AbilityReleaseEvent} then send packet for them.
     * Only executes on client using the dist executor.
     */
    public static void sendAbilityReleasePacket(ResourceLocation skillId, int keyNumber, int mode, int heldTicks) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || heldTicks < 0) return;
        NetworkManager.sendToServer(new RequestAbilityReleasePacket(heldTicks, keyNumber, mode, skillId));
    }

    /**
     * This Method filters {@link Ability} that meets the conditions of the {@link io.github.solusmods.eternalcore.abilities.api.AbilityEvents.AbilityToggleEvent} then send packet for them.
     * Only executes on client using the dist executor.
     */
    public static void sendAbilityTogglePacket(ResourceLocation skillId) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        NetworkManager.sendToServer(new RequestAbilityTogglePacket(skillId));
    }
}
