package io.github.solusmods.eternalcore.spiritual_root.impl.network;

import io.github.solusmods.eternalcore.network.api.util.NetworkUtils;
import io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s.RequestSpiritualRootAdvancePacket;

public class SpiritualRootNetwork {

    private SpiritualRootNetwork() {
    }

    public static void init() {
        NetworkUtils.registerC2SPayload(RequestSpiritualRootAdvancePacket.TYPE,
                RequestSpiritualRootAdvancePacket.STREAM_CODEC, RequestSpiritualRootAdvancePacket::handle);
    }


}
