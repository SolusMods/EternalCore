package io.github.solusmods.eternalcore.element;

import io.github.solusmods.eternalcore.element.impl.ElementRegistry;
import io.github.solusmods.eternalcore.element.impl.ElementsStorage;
import io.github.solusmods.eternalcore.element.impl.network.ElementsNetwork;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;

import static io.github.solusmods.eternalcore.element.ModuleConstants.MOD_ID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalCoreElements {

    public static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init(){
        ElementRegistry.init();
        ElementsStorage.init();
        ElementsNetwork.init();
    }
}
