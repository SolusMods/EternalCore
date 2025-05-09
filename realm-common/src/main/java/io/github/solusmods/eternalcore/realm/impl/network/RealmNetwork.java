package io.github.solusmods.eternalcore.realm.impl.network;


import io.github.solusmods.eternalcore.network.api.util.NetworkUtils;
import io.github.solusmods.eternalcore.realm.impl.network.c2s.RequestRealmBreakthroughPacket;
import io.github.solusmods.eternalcore.realm.impl.network.c2s.SyncRealmStoragePayload;

public class RealmNetwork {

    private RealmNetwork() {
    }

    public static void init() {
        NetworkUtils.registerC2SPayload(RequestRealmBreakthroughPacket.TYPE,
                RequestRealmBreakthroughPacket.STREAM_CODEC, RequestRealmBreakthroughPacket::handle);
        NetworkUtils.registerC2SPayload(SyncRealmStoragePayload.TYPE, SyncRealmStoragePayload.STREAM_CODEC, SyncRealmStoragePayload::handle);
    }


}
