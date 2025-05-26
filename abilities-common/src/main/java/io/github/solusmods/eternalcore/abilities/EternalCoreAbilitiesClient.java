package io.github.solusmods.eternalcore.abilities;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.networking.NetworkManager;
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI;
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents;
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance;
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.RequestAbilityScrollPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class EternalCoreAbilitiesClient {
    public static void init() {
        ClientRawInputEvent.MOUSE_SCROLLED.register((client, amountX, amountY) -> {
            Player player = client.player;
            if (player == null) return EventResult.pass();

            List<ResourceLocation> packetSkills = new ArrayList<>();
            for (AbilityInstance skillInstance : AbilityAPI.getAbilitiesFrom(player).getLearnedAbilities()) {
                if (AbilityEvents.ABILITY_SCROLL_CLIENT.invoker().scroll(skillInstance, player, amountY).isFalse()) continue;
                if (!skillInstance.canScroll(player)) continue;
                packetSkills.add(skillInstance.getAbilityId());
            }

            if (!packetSkills.isEmpty()) {
                NetworkManager.sendToServer(new RequestAbilityScrollPacket(amountY, packetSkills));
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }
}
