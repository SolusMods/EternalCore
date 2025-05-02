package io.github.solusmods.eternalcore.realm.neoforge;

import io.github.solusmods.eternalcore.realm.EternalCoreRealm;
import io.github.solusmods.eternalcore.realm.ModuleConstants;
import net.neoforged.fml.common.Mod;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreRealmNeoForge {

    public EternalCoreRealmNeoForge(){
        EternalCoreRealm.init();
    }
}
