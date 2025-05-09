package io.github.solusmods.eternalcore.stage.impl.network;

import io.github.solusmods.eternalcore.network.api.util.NetworkUtils;
import io.github.solusmods.eternalcore.stage.impl.network.c2s.SyncStagesStoragePayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StagesNetwork {

    public static void init(){
        NetworkUtils.registerC2SPayload(SyncStagesStoragePayload.TYPE, SyncStagesStoragePayload.STREAM_CODEC, SyncStagesStoragePayload::handle);
    }
}
