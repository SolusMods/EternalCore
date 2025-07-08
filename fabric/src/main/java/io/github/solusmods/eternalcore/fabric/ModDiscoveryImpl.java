package io.github.solusmods.eternalcore.fabric;

import io.github.solusmods.eternalcore.api.qi_energy.AbstractQiEnergy;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;

import java.util.ArrayList;
import java.util.List;

public class ModDiscoveryImpl {
    public static List<AbstractRealm> getRealmsForConfig() {
        return new ArrayList<>();
    }

    public static List<AbstractStage> getStagesForConfig() {
        return new ArrayList<>();
    }

    public static List<AbstractSpiritualRoot> getSpiritualRootsForConfig() {
        return new ArrayList<>();
    }

    public static List<AbstractQiEnergy> getQiEnergyForConfig() {
        return new ArrayList<>();
    }

    public static void init() {
    }
}
