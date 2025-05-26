package io.github.solusmods.eternalcore.abilities.impl.network;

import io.github.solusmods.eternalcore.abilities.impl.network.c2s.*;
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils;

public class EternalCoreAbilityNetwork {
    public static void init() {
        NetworkUtils.registerC2SPayload(RequestAbilityActivationPacket.TYPE, RequestAbilityActivationPacket.STREAM_CODEC, RequestAbilityActivationPacket::handle);
        NetworkUtils.registerC2SPayload(RequestAbilityReleasePacket.TYPE, RequestAbilityReleasePacket.STREAM_CODEC, RequestAbilityReleasePacket::handle);
        NetworkUtils.registerC2SPayload(RequestAbilityScrollPacket.TYPE, RequestAbilityScrollPacket.STREAM_CODEC, RequestAbilityScrollPacket::handle);
        NetworkUtils.registerC2SPayload(RequestAbilityTogglePacket.TYPE, RequestAbilityTogglePacket.STREAM_CODEC, RequestAbilityTogglePacket::handle);
    }
}
