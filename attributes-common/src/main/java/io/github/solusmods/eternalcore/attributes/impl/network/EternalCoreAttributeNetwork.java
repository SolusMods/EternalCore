package io.github.solusmods.eternalcore.attributes.impl.network;

import io.github.solusmods.eternalcore.attributes.impl.network.c2s.RequestGlideStartPacket;
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils;

public class EternalCoreAttributeNetwork {
    public static void init() {
        NetworkUtils.registerC2SPayload(RequestGlideStartPacket.TYPE, RequestGlideStartPacket.STREAM_CODEC, RequestGlideStartPacket::handle);
    }
}
