package io.github.solusmods.eternalcore.spiritual_root;

import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootRegistry;
import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootStorage;
import io.github.solusmods.eternalcore.spiritual_root.impl.network.SpiritualRootNetwork;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;

import static io.github.solusmods.eternalcore.spiritual_root.ModuleConstants.MOD_ID;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalCoreSpiritualRoot {
    public static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init(){
        SpiritualRootNetwork.init();
        SpiritualRootRegistry.init();
        SpiritualRootStorage.init();
    }
}
