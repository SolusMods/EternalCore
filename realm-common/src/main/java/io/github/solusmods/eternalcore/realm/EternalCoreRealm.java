package io.github.solusmods.eternalcore.realm;

import io.github.solusmods.eternalcore.realm.impl.RealmRegistry;
import io.github.solusmods.eternalcore.realm.impl.RealmStorage;
import io.github.solusmods.eternalcore.realm.impl.network.RealmNetwork;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;

import static io.github.solusmods.eternalcore.realm.ModuleConstants.MOD_ID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EternalCoreRealm {

    public static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init(){
        RealmStorage.init();
        RealmRegistry.init();
        RealmNetwork.init();
    }
}
