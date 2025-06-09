package io.github.solusmods.eternalcore.realm.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils.registerC2SPayload
import io.github.solusmods.eternalcore.realm.impl.network.c2s.RequestRealmBreakthroughPacket
import io.github.solusmods.eternalcore.realm.impl.network.c2s.SyncRealmStoragePayload


object RealmNetwork {
    fun init() {
        registerC2SPayload<RequestRealmBreakthroughPacket?>(
            RequestRealmBreakthroughPacket.Companion.TYPE,
            RequestRealmBreakthroughPacket.Companion.STREAM_CODEC
        ) { obj: RequestRealmBreakthroughPacket?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
        registerC2SPayload<SyncRealmStoragePayload?>(
            SyncRealmStoragePayload.Companion.TYPE,
            SyncRealmStoragePayload.Companion.STREAM_CODEC
        ) { obj: SyncRealmStoragePayload?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
    }
}
