package io.github.solusmods.eternalcore.config.impl.network.s2c

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.config.ConfigRegistry.Companion.loadConfigSyncData
import io.github.solusmods.eternalcore.config.EternalCoreConfig.create
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamEncoder
import net.minecraft.network.protocol.common.custom.CustomPacketPayload


/**
 * Handles the syncing of configuration data from the server to the client.
 * This is triggered when a player joins a multiplayer server.
 */
@JvmRecord
data class SyncConfigToClientPayload(
    val configData: MutableMap<String?, String?>?
) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(
        buf.readMap<String?, String?>(
            StreamDecoder { obj: FriendlyByteBuf? -> obj!!.readUtf() },
            StreamDecoder { obj: FriendlyByteBuf? -> obj!!.readUtf() })
    )

    fun encode(buf: FriendlyByteBuf) {
        buf.writeMap<String, String>(
            this.configData!!,
            StreamEncoder { obj: FriendlyByteBuf?, string: String? -> obj!!.writeUtf(string) },
            StreamEncoder { obj: FriendlyByteBuf?, string: String? -> obj!!.writeUtf(string) })
    }

    fun handle(context: NetworkManager.PacketContext) {
        if (context.environment != Env.CLIENT) return
        context.queue { loadConfigSyncData(this.configData) }
    }

    override fun type(): CustomPacketPayload.Type<SyncConfigToClientPayload> {
        return TYPE
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncConfigToClientPayload> =
            CustomPacketPayload.Type<SyncConfigToClientPayload>(
                create("sync_config_to_client")
            )
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, SyncConfigToClientPayload> =
            CustomPacketPayload.codec<FriendlyByteBuf, SyncConfigToClientPayload>(
                { obj: SyncConfigToClientPayload?, buf: FriendlyByteBuf? -> obj!!.encode(buf!!) },
                { buf: FriendlyByteBuf? ->
                    SyncConfigToClientPayload(
                        buf!!
                    )
                })
    }
}
