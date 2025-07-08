package io.github.solusmods.eternalcore;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.solusmods.eternalcore.api.qi_energy.AbstractQiEnergy;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;

import java.util.List;

public final class ModDiscovery {

    @ExpectPlatform
    public static List<AbstractRealm> getRealmsForConfig() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<AbstractStage> getStagesForConfig() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<AbstractSpiritualRoot> getSpiritualRootsForConfig() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<AbstractQiEnergy> getQiEnergyForConfig() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void init() {}
}
