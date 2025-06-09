package io.github.solusmods.eternalcore.element.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.element.impl.network.c2s.SyncElementsStoragePayload
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils
import lombok.AccessLevel
import lombok.NoArgsConstructor

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object ElementsNetwork {
    @JvmStatic
    fun init() {
        NetworkUtils.registerC2SPayload<SyncElementsStoragePayload?>(
            SyncElementsStoragePayload.Companion.TYPE,
            SyncElementsStoragePayload.Companion.STREAM_CODEC,
            NetworkManager.NetworkReceiver { obj: SyncElementsStoragePayload?, context: NetworkManager.PacketContext? ->
                obj!!.handle(context!!)
            })
    }
}
