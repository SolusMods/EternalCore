package io.github.solusmods.eternalcore.realm.impl.network;


import io.github.solusmods.eternalcore.network.api.util.NetworkUtils;
import io.github.solusmods.eternalcore.realm.impl.network.c2s.RequestRealmBreakthroughPacket;

public class RealmNetwork {

    private RealmNetwork() {
    }

    public static void init() {
        NetworkUtils.registerC2SPayload(RequestRealmBreakthroughPacket.TYPE,
                RequestRealmBreakthroughPacket.STREAM_CODEC, RequestRealmBreakthroughPacket::handle);
    }


}
