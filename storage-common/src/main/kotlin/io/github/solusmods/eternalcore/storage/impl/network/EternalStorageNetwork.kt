package io.github.solusmods.eternalcore.storage.impl.network

import dev.architectury.networking.NetworkManager
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncChunkStoragePayload
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncEntityStoragePayload
import io.github.solusmods.eternalcore.storage.impl.network.s2c.SyncWorldStoragePayload

object EternalStorageNetwork {

    fun init() {
        // Register S2C payloads with safe null handling
        NetworkUtils.registerS2CPayload(
            SyncChunkStoragePayload.TYPE,
            SyncChunkStoragePayload.STREAM_CODEC
        ) { payload: SyncChunkStoragePayload?, context: NetworkManager.PacketContext? ->
            payload?.handle(requireNotNull(context))
        }

        NetworkUtils.registerS2CPayload(
            SyncEntityStoragePayload.TYPE,
            SyncEntityStoragePayload.STREAM_CODEC
        ) { payload: SyncEntityStoragePayload?, context: NetworkManager.PacketContext? ->
            payload?.handle(requireNotNull(context))
        }

        NetworkUtils.registerS2CPayload(
            SyncWorldStoragePayload.TYPE,
            SyncWorldStoragePayload.STREAM_CODEC
        ) { payload: SyncWorldStoragePayload?, context: NetworkManager.PacketContext? ->
            payload?.handle(requireNotNull(context))
        }
    }
}