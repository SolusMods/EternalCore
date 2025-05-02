package io.github.solusmods.eternalcore.spiritual_root.neoforge;

import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot;
import io.github.solusmods.eternalcore.spiritual_root.ModuleConstants;
import net.neoforged.fml.common.Mod;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreSpiritualRootNeoForge {

    public EternalCoreSpiritualRootNeoForge(){
        EternalCoreSpiritualRoot.init();
    }
}
