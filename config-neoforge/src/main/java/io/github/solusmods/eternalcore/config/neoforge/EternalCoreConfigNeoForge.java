package io.github.solusmods.eternalcore.config.neoforge;
import io.github.solusmods.eternalcore.config.EternalCoreConfig;
import net.neoforged.fml.common.Mod;
import io.github.solusmods.eternalcore.config.ModuleConstants;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreConfigNeoForge {
    public EternalCoreConfigNeoForge() {
        EternalCoreConfig.init();
    }


}
