package io.github.solusmods.eternalcore.abilities

import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientRawInputEvent
import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.RequestAbilityScrollPacket
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

object EternalCoreAbilitiesClient {
    fun init() {
        ClientRawInputEvent.MOUSE_SCROLLED.register(ClientRawInputEvent.MouseScrolled { client: Minecraft?, amountX: Double, amountY: Double ->
            val player: Player? = client!!.player
            if (player == null) return@MouseScrolled EventResult.pass()

            val packetSkills: MutableList<ResourceLocation?> = ArrayList<ResourceLocation?>()
            for (skillInstance in AbilityAPI.getAbilitiesFrom(player)!!.learnedAbilities!!) {
                if (AbilityEvents.Companion.ABILITY_SCROLL_CLIENT.invoker()!!.scroll(skillInstance, player, amountY)!!.isFalse
                ) continue
                if (!skillInstance!!.canScroll(player)) continue
                packetSkills.add(skillInstance.abilityId)
            }

            if (!packetSkills.isEmpty()) {
                NetworkManager.sendToServer<RequestAbilityScrollPacket?>(
                    RequestAbilityScrollPacket(
                        amountY,
                        packetSkills
                    )
                )
                return@MouseScrolled EventResult.interruptFalse()
            }
            EventResult.pass()
        })
    }
}
