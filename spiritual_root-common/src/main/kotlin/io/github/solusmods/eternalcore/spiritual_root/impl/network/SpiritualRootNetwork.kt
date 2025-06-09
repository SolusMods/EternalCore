package io.github.solusmods.eternalcore.spiritual_root.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils.registerC2SPayload
import io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s.RequestSpiritualRootAdvancePacket
import io.github.solusmods.eternalcore.spiritual_root.impl.network.c2s.SyncSpiritualRootStoragePayload

object SpiritualRootNetwork {
    fun init() {
        registerC2SPayload<RequestSpiritualRootAdvancePacket?>(
            RequestSpiritualRootAdvancePacket.Companion.TYPE,
            RequestSpiritualRootAdvancePacket.Companion.STREAM_CODEC
        ) { obj: RequestSpiritualRootAdvancePacket?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
        registerC2SPayload<SyncSpiritualRootStoragePayload?>(
            SyncSpiritualRootStoragePayload.Companion.TYPE,
            SyncSpiritualRootStoragePayload.Companion.STREAM_CODEC
        ) { obj: SyncSpiritualRootStoragePayload?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
    }
}
