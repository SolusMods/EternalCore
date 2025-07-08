package io.github.solusmods.eternalcore.impl.storage.network;

import io.github.solusmods.eternalcore.api.network.util.NetworkUtils;
import io.github.solusmods.eternalcore.impl.storage.network.s2c.SyncChunkStoragePayload;
import io.github.solusmods.eternalcore.impl.storage.network.s2c.SyncEntityStoragePayload;
import io.github.solusmods.eternalcore.impl.storage.network.s2c.SyncWorldStoragePayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StorageNetwork {
    public static void init() {
        NetworkUtils.registerS2CPayload(SyncChunkStoragePayload.TYPE, SyncChunkStoragePayload.STREAM_CODEC, SyncChunkStoragePayload::handle);
        NetworkUtils.registerS2CPayload(SyncEntityStoragePayload.TYPE, SyncEntityStoragePayload.STREAM_CODEC, SyncEntityStoragePayload::handle);
        NetworkUtils.registerS2CPayload(SyncWorldStoragePayload.TYPE, SyncWorldStoragePayload.STREAM_CODEC, SyncWorldStoragePayload::handle);
    }
}
