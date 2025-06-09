package io.github.solusmods.eternalcore.testing.client

import dev.architectury.event.CompoundEventResult
import dev.architectury.event.events.client.ClientChatEvent
import io.github.solusmods.eternalcore.testing.configs.TestConfig
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.Component

object EternalCoreTestingClient {
    fun init() {
//        KeybindingTest.init()
        ClientChatEvent.RECEIVED.register(ClientChatEvent.Received { type: ChatType.Bound?, message: Component? ->
            val player = Minecraft.getInstance().player
            if (player != null) {
                TestConfig.Companion.printTestConfig(player)
            }
            CompoundEventResult.pass()
        })
    }
}
