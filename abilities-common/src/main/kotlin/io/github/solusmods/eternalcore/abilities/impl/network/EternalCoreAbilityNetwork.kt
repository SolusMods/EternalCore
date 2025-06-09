package io.github.solusmods.eternalcore.abilities.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.RequestAbilityActivationPacket
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.RequestAbilityReleasePacket
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.RequestAbilityScrollPacket
import io.github.solusmods.eternalcore.abilities.impl.network.c2s.RequestAbilityTogglePacket
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils

object EternalCoreAbilityNetwork {
    fun init() {
        NetworkUtils.registerC2SPayload<RequestAbilityActivationPacket?>(
            RequestAbilityActivationPacket.Companion.TYPE,
            RequestAbilityActivationPacket.Companion.STREAM_CODEC
        ) { obj: RequestAbilityActivationPacket?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
        NetworkUtils.registerC2SPayload<RequestAbilityReleasePacket?>(
            RequestAbilityReleasePacket.Companion.TYPE,
            RequestAbilityReleasePacket.Companion.STREAM_CODEC
        ) { obj: RequestAbilityReleasePacket?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
        NetworkUtils.registerC2SPayload<RequestAbilityScrollPacket?>(
            RequestAbilityScrollPacket.Companion.TYPE,
            RequestAbilityScrollPacket.Companion.STREAM_CODEC
        ) { obj: RequestAbilityScrollPacket?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
        NetworkUtils.registerC2SPayload<RequestAbilityTogglePacket?>(
            RequestAbilityTogglePacket.Companion.TYPE,
            RequestAbilityTogglePacket.Companion.STREAM_CODEC
        ) { obj: RequestAbilityTogglePacket?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
    }
}
