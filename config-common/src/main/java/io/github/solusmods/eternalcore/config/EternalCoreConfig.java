package io.github.solusmods.eternalcore.config;

import com.electronwill.nightconfig.core.Config;
import dev.architectury.event.events.common.PlayerEvent;
import io.github.solusmods.eternalcore.config.impl.network.EternalConfigNetwork;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.solusmods.eternalcore.config.ModuleConstants.MOD_ID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalCoreConfig {
    public static final Logger LOG = LoggerFactory.getLogger("EternalCore - Config");

    public static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        Config.setInsertionOrderPreserved(true);
        EternalConfigNetwork.init();
        PlayerEvent.PLAYER_JOIN.register(player -> EternalConfigNetwork.syncToClients());
    }
}
