package io.github.solusmods.eternalcore.stage.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils.registerC2SPayload
import io.github.solusmods.eternalcore.stage.impl.network.c2s.RequestStageBreakthroughPacket
import io.github.solusmods.eternalcore.stage.impl.network.c2s.SyncStagesStoragePayload


object StagesNetwork {
    fun init() {
        registerC2SPayload<SyncStagesStoragePayload?>(
            SyncStagesStoragePayload.Companion.TYPE,
            SyncStagesStoragePayload.Companion.STREAM_CODEC
        ) { obj: SyncStagesStoragePayload?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
        registerC2SPayload<RequestStageBreakthroughPacket?>(
            RequestStageBreakthroughPacket.Companion.TYPE,
            RequestStageBreakthroughPacket.Companion.STREAM_CODEC
        ) { obj: RequestStageBreakthroughPacket?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
    }
}
