package io.github.solusmods.eternalcore;

import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.registries.RegistrarManager;
import io.github.solusmods.eternalcore.impl.qi_energy.QiEnergyStorage;
import io.github.solusmods.eternalcore.impl.realm.RealmStorage;
import io.github.solusmods.eternalcore.impl.realm.network.RealmNetwork;
import io.github.solusmods.eternalcore.impl.spiritual_root.SpiritualRootStorage;
import io.github.solusmods.eternalcore.impl.spiritual_root.network.SpiritualRootNetwork;
import io.github.solusmods.eternalcore.impl.stage.StageStorage;
import io.github.solusmods.eternalcore.impl.stage.network.StagesNetwork;
import io.github.solusmods.eternalcore.impl.storage.StorageManager;
import io.github.solusmods.eternalcore.impl.storage.network.StorageNetwork;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EternalCore {
    public static final Logger LOG = LoggerFactory.getLogger("EternalCore");
    public static final String MOD_ID = "eternalcore";
    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {

        // Networks
        StorageNetwork.init();
        RealmNetwork.init();
        SpiritualRootNetwork.init();
        StagesNetwork.init();
        ModDiscovery.init();
        PlatformCommandUtils.init();
        Register.init();
        ServerConfigs.init();

        // Storages
        RealmStorage.init();
        SpiritualRootStorage.init();
        QiEnergyStorage.init();
        StageStorage.init();

        // Events
        LifecycleEvent.SETUP.register(StorageManager::init);
    }
}
