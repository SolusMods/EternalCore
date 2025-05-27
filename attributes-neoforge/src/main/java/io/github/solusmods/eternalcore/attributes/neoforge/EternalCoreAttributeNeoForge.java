package io.github.solusmods.eternalcore.attributes.neoforge;

import io.github.solusmods.eternalcore.attributes.EternalCoreAttribute;
import io.github.solusmods.eternalcore.attributes.ModuleConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ModuleConstants.MOD_ID)
public class EternalCoreAttributeNeoForge {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ModuleConstants.MOD_ID);
    public EternalCoreAttributeNeoForge(IEventBus bus) {
        EternalCoreAttribute.init();
        ATTRIBUTES.register(bus);
    }
}
