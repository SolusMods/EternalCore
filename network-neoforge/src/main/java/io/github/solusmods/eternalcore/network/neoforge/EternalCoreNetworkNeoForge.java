package io.github.solusmods.eternalcore.network.neoforge;

import io.github.solusmods.eternalcore.network.EternalCoreNetwork;
import io.github.solusmods.eternalcore.network.ModuleConstants;
import net.neoforged.fml.common.Mod;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreNetworkNeoForge {

    public EternalCoreNetworkNeoForge(){
        EternalCoreNetwork.init();
    }
}
