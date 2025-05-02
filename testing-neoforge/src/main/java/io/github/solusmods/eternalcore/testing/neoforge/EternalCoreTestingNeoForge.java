package io.github.solusmods.eternalcore.testing.neoforge;

import io.github.solusmods.eternalcore.testing.ModuleConstants;
import io.github.solusmods.eternalcore.testing.EternalCoreTesting;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreTestingNeoForge {

    public EternalCoreTestingNeoForge(IEventBus bus){
        EternalCoreTesting.init();
    }
}
