package io.github.solusmods.eternalcore.stage;

import io.github.solusmods.eternalcore.stage.impl.StageStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.solusmods.eternalcore.stage.ModuleConstants.MOD_ID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalCoreStage {
    public static final Logger LOG = LoggerFactory.getLogger("EternalCore - Stage");

    public static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init(){
        StageStorage.init();
    }

}
