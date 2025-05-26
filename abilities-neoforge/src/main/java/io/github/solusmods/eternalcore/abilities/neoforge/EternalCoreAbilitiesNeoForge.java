package io.github.solusmods.eternalcore.abilities.neoforge;


import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import io.github.solusmods.eternalcore.abilities.ModuleConstants;
import net.neoforged.fml.common.Mod;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreAbilitiesNeoForge {

    public EternalCoreAbilitiesNeoForge(){
        EternalCoreAbilities.init();
    }
}
