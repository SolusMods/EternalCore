package io.github.solusmods.eternalcore.storage.neoforge;

import io.github.solusmods.eternalcore.storage.EternalCoreStorage;
import io.github.solusmods.eternalcore.storage.ModuleConstants;
import net.neoforged.fml.common.Mod;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreStorageNeoForge {
    public EternalCoreStorageNeoForge(){
        EternalCoreStorage.init();
    }
}
