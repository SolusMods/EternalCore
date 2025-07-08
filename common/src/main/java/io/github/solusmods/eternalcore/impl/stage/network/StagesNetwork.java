package io.github.solusmods.eternalcore.impl.stage.network;

import io.github.solusmods.eternalcore.api.network.util.NetworkUtils;
import io.github.solusmods.eternalcore.impl.stage.network.c2s.RequestStageBreakthroughPacket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StagesNetwork {

    public static void init() {
        NetworkUtils.registerC2SPayload(RequestStageBreakthroughPacket.TYPE, RequestStageBreakthroughPacket.STREAM_CODEC, RequestStageBreakthroughPacket::handle);
    }
}
