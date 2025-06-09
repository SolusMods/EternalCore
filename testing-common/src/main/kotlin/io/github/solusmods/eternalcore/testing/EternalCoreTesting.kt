package io.github.solusmods.eternalcore.testing

import dev.architectury.platform.Platform
import io.github.solusmods.eternalcore.testing.client.EternalCoreTestingClient
import io.github.solusmods.eternalcore.testing.registry.RegistryTest
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.fabricmc.api.EnvType
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object EternalCoreTesting {
    @JvmStatic
    val LOG: Logger = LoggerFactory.getLogger("EternalCore - Testing")
    @JvmStatic
    fun init() {
        RegistryTest.init()

        if (Platform.getEnv() == EnvType.CLIENT) {
            EternalCoreTestingClient.init()
        }
    }
}
