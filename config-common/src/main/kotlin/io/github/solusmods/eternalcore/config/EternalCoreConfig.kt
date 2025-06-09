package io.github.solusmods.eternalcore.config

import com.electronwill.nightconfig.core.Config
import dev.architectury.event.events.common.PlayerEvent
import io.github.solusmods.eternalcore.config.impl.network.EternalConfigNetwork
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@NoArgsConstructor(access = AccessLevel.PRIVATE)
object EternalCoreConfig {
    @JvmField
    val LOG: Logger? = LoggerFactory.getLogger("EternalCore - Config")

    @JvmStatic
    fun create(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(ModuleConstants.MOD_ID, path)
    }

    @JvmStatic
    fun init() {
        Config.setInsertionOrderPreserved(true)
        EternalConfigNetwork.init()
        PlayerEvent.PLAYER_JOIN.register(PlayerEvent.PlayerJoin { player: ServerPlayer? -> EternalConfigNetwork.syncToClients() })
    }
}
