package io.github.solusmods.eternalcore.neoforge;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.qi_energy.AbstractQiEnergy;
import io.github.solusmods.eternalcore.api.qi_energy.AutoQiEnergyConfig;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.realm.AutoRealmConfig;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.spiritual_root.AutoSpiritualRootConfig;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import io.github.solusmods.eternalcore.api.stage.AutoStageConfig;
import net.neoforged.fml.ModList;
import org.objectweb.asm.Type;

import java.util.*;

public class ModDiscoveryImpl {
    public static List<AbstractRealm> getRealmsForConfig() {
        var allScanData = ModList.get().getAllScanData();
        Set<String> realmClassName = new HashSet<>();

        allScanData.forEach((scanData) -> {
            scanData.getAnnotations().forEach(annotation -> {
                if (Objects.equals(annotation.annotationType(), Type.getType(AutoRealmConfig.class))) {
                    realmClassName.add(annotation.memberName());
                }
            });
        });

        var realms = new ArrayList<AbstractRealm>();
        realmClassName.forEach((realmName) -> {
            try {
                Class<?> pluginClass = Class.forName(realmName);
                var pluginClassSubclass = pluginClass.asSubclass(AbstractRealm.class);
                var constructor = pluginClassSubclass.getDeclaredConstructor();
                var instance = constructor.newInstance();
                realms.add(instance);
            } catch (Exception e) {
                EternalCore.LOG.error("ModDiscovery: {}, {}", realmName, e);
            }
        });

        return realms;
    }

    public static List<AbstractStage> getStagesForConfig() {
        var allScanData = ModList.get().getAllScanData();
        Set<String> stageClassNames = new HashSet<>();

        allScanData.forEach((scanData) -> {
            scanData.getAnnotations().forEach(annotation -> {
                if (Objects.equals(annotation.annotationType(), Type.getType(AutoStageConfig.class))) {
                    stageClassNames.add(annotation.memberName());
                }
            });
        });

        var stages = new ArrayList<AbstractStage>();
        stageClassNames.forEach((stageName) -> {
            try {
                Class<?> pluginClass = Class.forName(stageName);
                var pluginClassSubclass = pluginClass.asSubclass(AbstractStage.class);
                var constructor = pluginClassSubclass.getDeclaredConstructor();
                var instance = constructor.newInstance();
                stages.add(instance);
            } catch (Exception e) {
                EternalCore.LOG.error("ModDiscovery: {}, {}", stageName, e);
            }
        });

        return stages;
    }

    public static List<AbstractSpiritualRoot> getSpiritualRootsForConfig() {
        var allScanData = ModList.get().getAllScanData();
        Set<String> spiritualRootNames = new HashSet<>();

        allScanData.forEach((scanData) -> {
            scanData.getAnnotations().forEach(annotation -> {
                if (Objects.equals(annotation.annotationType(), Type.getType(AutoSpiritualRootConfig.class))) {
                    spiritualRootNames.add(annotation.memberName());
                }
            });
        });

        var spiritualRoots = new ArrayList<AbstractSpiritualRoot>();
        spiritualRootNames.forEach((rootName) -> {
            try {
                Class<?> pluginClass = Class.forName(rootName);
                var pluginClassSubclass = pluginClass.asSubclass(AbstractSpiritualRoot.class);
                var constructor = pluginClassSubclass.getDeclaredConstructor();
                var instance = constructor.newInstance();
                spiritualRoots.add(instance);
            } catch (Exception e) {
                EternalCore.LOG.error("ModDiscovery: {}, {}", rootName, e);
            }
        });

        return spiritualRoots;
    }

    public static List<AbstractQiEnergy> getQiEnergyForConfig() {
        var allScanData = ModList.get().getAllScanData();
        Set<String> qiEnergiesNames = new HashSet<>();

        allScanData.forEach((scanData) -> {
            scanData.getAnnotations().forEach(annotation -> {
                if (Objects.equals(annotation.annotationType(), Type.getType(AutoQiEnergyConfig.class))) {
                    qiEnergiesNames.add(annotation.memberName());
                }
            });
        });

        var qiEnergies = new ArrayList<AbstractQiEnergy>();
        qiEnergiesNames.forEach((qiEnergyName) -> {
            try {
                Class<?> pluginClass = Class.forName(qiEnergyName);
                var pluginClassSubclass = pluginClass.asSubclass(AbstractQiEnergy.class);
                var constructor = pluginClassSubclass.getDeclaredConstructor();
                var instance = constructor.newInstance();
                qiEnergies.add(instance);
            } catch (Exception e) {
                EternalCore.LOG.error("ModDiscovery: {}, {}", qiEnergyName, e);
            }
        });

        return qiEnergies;
    }

    public static void init() {

    }
}
