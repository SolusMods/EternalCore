package io.github.solusmods.eternalcore.abilities.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.abilities.api.Ability
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents.*
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.RequestAbilityActivationPacket
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.RequestAbilityReleasePacket
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.RequestAbilityTogglePacket
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object InternalAbilityPacketActions {
    /**
     * This Method filters [Ability] that meets the conditions of the [AbilityActivationEvent] then send packet for them.
     * Only executes on client using the dist executor.
     */
    fun sendAbilityActivationPacket(skillId: ResourceLocation?, keyNumber: Int, mode: Int) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        if (player == null) return
        NetworkManager.sendToServer<RequestAbilityActivationPacket?>(
            RequestAbilityActivationPacket(
                keyNumber,
                skillId,
                mode
            )
        )
    }

    /**
     * This Method filters [Ability] that meets the conditions of the [AbilityReleaseEvent] then send packet for them.
     * Only executes on client using the dist executor.
     */
    fun sendAbilityReleasePacket(skillId: ResourceLocation?, keyNumber: Int, mode: Int, heldTicks: Int) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        if (player == null || heldTicks < 0) return
        NetworkManager.sendToServer<RequestAbilityReleasePacket?>(
            RequestAbilityReleasePacket(
                heldTicks,
                keyNumber,
                mode,
                skillId
            )
        )
    }

    /**
     * This Method filters [Ability] that meets the conditions of the [AbilityToggleEvent] then send packet for them.
     * Only executes on client using the dist executor.
     */
    fun sendAbilityTogglePacket(skillId: ResourceLocation?) {
        val minecraft = Minecraft.getInstance()
        val player: Player? = minecraft.player
        if (player == null) return
        NetworkManager.sendToServer<RequestAbilityTogglePacket?>(RequestAbilityTogglePacket(skillId))
    }
}
