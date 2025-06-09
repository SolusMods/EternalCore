package io.github.solusmods.eternalcore.config.impl.network

import dev.architectury.networking.NetworkManager
import dev.architectury.utils.GameInstance
import io.github.solusmods.eternalcore.config.ConfigRegistry.Companion.configSyncData
import io.github.solusmods.eternalcore.config.ConfigRegistry.Companion.getConfigSyncData
import io.github.solusmods.eternalcore.config.api.CoreConfig
import io.github.solusmods.eternalcore.config.impl.network.s2c.SyncConfigToClientPayload
import io.github.solusmods.eternalcore.network.api.util.NetworkUtils
import net.minecraft.server.level.ServerPlayer

object EternalConfigNetwork {
    fun init() {
        NetworkUtils.registerS2CPayload(
            SyncConfigToClientPayload.Companion.TYPE,
            SyncConfigToClientPayload.Companion.STREAM_CODEC
        ) { obj: SyncConfigToClientPayload?, context: NetworkManager.PacketContext? ->
            obj!!.handle(context!!)
        }
    }

    /**
     * Syncs all registered server-side configurations to a specific player.
     *
     *
     *
     * @param player The player to send the config data to.
     */
    fun syncToClient(player: ServerPlayer) {
        NetworkManager.sendToPlayer(player, SyncConfigToClientPayload(configSyncData))
    }

    /**
     * Syncs a specific server-side configuration to a specific player.
     *
     *
     *
     * @param player The player to send the config data to.
     * @param config The specific configuration class to sync.
     */
    fun syncToClient(player: ServerPlayer, config: Class<out CoreConfig?>) {
        NetworkManager.sendToPlayer(
            player,
            SyncConfigToClientPayload(getConfigSyncData(config))
        )
    }

    /**
     * Syncs all registered server-side configurations to all connected clients.
     *
     *
     * If called when no players are online, this method will do nothing.
     */
    fun syncToClients() {
        val server = GameInstance.getServer()
        if (server == null) throw RuntimeException("Failed to find the Server.")
        NetworkManager.sendToPlayers(
            server.playerList.players,
            SyncConfigToClientPayload(configSyncData)
        )
    }

    /**
     * Syncs a specific server-side configuration to all connected clients.
     *
     *
     *
     * @param config The specific configuration class to sync.
     */
    fun syncToClients(config: Class<out CoreConfig?>) {
        val server = GameInstance.getServer()
        if (server == null) throw RuntimeException("Failed to find the Server.")
        NetworkManager.sendToPlayers(
            server.playerList.players,
            SyncConfigToClientPayload(getConfigSyncData(config))
        )
    }
}
