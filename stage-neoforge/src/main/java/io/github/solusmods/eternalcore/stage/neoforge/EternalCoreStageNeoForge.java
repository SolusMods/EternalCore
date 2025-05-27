package io.github.solusmods.eternalcore.stage.neoforge;

import io.github.solusmods.eternalcore.stage.EternalCoreStage;
import io.github.solusmods.eternalcore.stage.ModuleConstants;
import net.neoforged.fml.common.Mod;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreStageNeoForge {

    public EternalCoreStageNeoForge(){
        EternalCoreStage.init();
    }
}
