package io.github.solusmods.eternalcore.testing;

import io.github.solusmods.eternalcore.testing.registry.RegistryTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalCoreTesting {
    public static final Logger LOG = LoggerFactory.getLogger("EternalCore - Testing");
    public static void init(){
        RegistryTest.init();
    }
}
