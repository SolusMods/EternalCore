package io.github.solusmods.eternalcore.testing;

import dev.architectury.platform.Platform;
import io.github.solusmods.eternalcore.testing.client.EternalCoreTestingClient;
import io.github.solusmods.eternalcore.testing.registry.RegistryTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.api.EnvType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalCoreTesting {
    public static final Logger LOG = LoggerFactory.getLogger("EternalCore - Testing");
    public static void init(){
        RegistryTest.init();

        if (Platform.getEnv() == EnvType.CLIENT) {
            EternalCoreTestingClient.init();
        }
    }
}
