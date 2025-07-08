package io.github.solusmods.eternalcore.impl.realm.network;


import io.github.solusmods.eternalcore.api.network.util.NetworkUtils;
import io.github.solusmods.eternalcore.impl.realm.network.c2s.RequestRealmBreakthroughPacket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RealmNetwork {

    public static void init() {
        NetworkUtils.registerC2SPayload(RequestRealmBreakthroughPacket.TYPE,
                RequestRealmBreakthroughPacket.STREAM_CODEC, RequestRealmBreakthroughPacket::handle);
    }


}
