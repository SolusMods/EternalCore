package io.github.solusmods.eternalcore.abilities;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.abilities.impl.AbilityRegistry;
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage;
import io.github.solusmods.eternalcore.abilities.impl.network.EternalCoreAbilityNetwork;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import static io.github.solusmods.eternalcore.abilities.ModuleConstants.MOD_ID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalCoreAbilities {

    public static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init(){
        AbilityRegistry.init();
        AbilityStorage.init();
        EternalCoreAbilityNetwork.init();
        if (Platform.getEnvironment() == Env.CLIENT) {
            EternalCoreAbilitiesClient.init();
        }
    }
}
