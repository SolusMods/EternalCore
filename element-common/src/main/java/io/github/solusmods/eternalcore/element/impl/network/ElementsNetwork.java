package io.github.solusmods.eternalcore.element.impl.network;

import io.github.solusmods.eternalcore.element.impl.network.c2s.SyncElementsStoragePayload;
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElementsNetwork {

    public static void init() {
        NetworkUtils.registerC2SPayload(SyncElementsStoragePayload.TYPE, SyncElementsStoragePayload.STREAM_CODEC, SyncElementsStoragePayload::handle);
    }
}
