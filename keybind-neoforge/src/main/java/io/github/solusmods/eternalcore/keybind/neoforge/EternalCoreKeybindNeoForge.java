package io.github.solusmods.eternalcore.keybind.neoforge;

import io.github.solusmods.eternalcore.keybind.ModuleConstants;
import io.github.solusmods.eternalcore.keybind.EternalCoreKeybind;
import net.neoforged.fml.common.Mod;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreKeybindNeoForge {

    public EternalCoreKeybindNeoForge(){
        EternalCoreKeybind.init();
    }
}
