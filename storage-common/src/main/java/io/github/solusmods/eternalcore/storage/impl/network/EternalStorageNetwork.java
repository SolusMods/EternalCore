package io.github.solusmods.eternalcore.storage.impl.network;


import io.github.solusmods.eternalcore.network.api.util.NetworkUtils;
import io.github.solusmods.eternalcore.storage.impl.network.c2s.SyncChunkStorageC2SPayload;
import io.github.solusmods.eternalcore.storage.impl.network.c2s.SyncEntityStorageC2SPayload;
import io.github.solusmods.eternalcore.storage.impl.network.c2s.SyncWorldStorageCTSPayload;
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncChunkStoragePayload;
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncEntityStoragePayload;
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncWorldStoragePayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalStorageNetwork {

    public static void init() {
        NetworkUtils.registerS2CPayload(SyncChunkStoragePayload.TYPE, SyncChunkStoragePayload.STREAM_CODEC, SyncChunkStoragePayload::handle);
        NetworkUtils.registerS2CPayload(SyncEntityStoragePayload.TYPE, SyncEntityStoragePayload.STREAM_CODEC, SyncEntityStoragePayload::handle);
        NetworkUtils.registerS2CPayload(SyncWorldStoragePayload.TYPE, SyncWorldStoragePayload.STREAM_CODEC, SyncWorldStoragePayload::handle);
        NetworkUtils.registerC2SPayload(SyncChunkStorageC2SPayload.TYPE, SyncChunkStorageC2SPayload.STREAM_CODEC, SyncChunkStorageC2SPayload::handle);
        NetworkUtils.registerC2SPayload(SyncEntityStorageC2SPayload.TYPE, SyncEntityStorageC2SPayload.STREAM_CODEC, SyncEntityStorageC2SPayload::handle);
        NetworkUtils.registerC2SPayload(SyncWorldStorageCTSPayload.TYPE, SyncWorldStorageCTSPayload.STREAM_CODEC, SyncWorldStorageCTSPayload::handle);
    }
}
