package io.github.solusmods.eternalcore.impl.spiritual_root.network;


import io.github.solusmods.eternalcore.api.network.util.NetworkUtils;
import io.github.solusmods.eternalcore.impl.spiritual_root.network.c2s.RequestSpiritualRootAdvancePacket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpiritualRootNetwork {

    public static void init() {
        NetworkUtils.registerC2SPayload(RequestSpiritualRootAdvancePacket.TYPE,
                RequestSpiritualRootAdvancePacket.STREAM_CODEC, RequestSpiritualRootAdvancePacket::handle);
    }


}
