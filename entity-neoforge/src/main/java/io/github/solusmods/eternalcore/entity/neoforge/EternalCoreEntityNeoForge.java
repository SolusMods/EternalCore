package io.github.solusmods.eternalcore.entity.neoforge;

import io.github.solusmods.eternalcore.entity.EternalCoreEntity;
import io.github.solusmods.eternalcore.entity.ModuleConstants;
import net.neoforged.fml.common.Mod;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreEntityNeoForge {

    public EternalCoreEntityNeoForge(){
        EternalCoreEntity.init();
    }
}
