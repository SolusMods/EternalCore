package io.github.solusmods.eternalcore.neoforge;

import io.github.solusmods.eternalcore.ModDiscovery;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import io.github.solusmods.eternalcore.config.*;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class ServerConfigsImpl {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<Integer> SERVER_LOADED;

    private static final Map<String, RealmEntry> REALM_CONFIGS = new HashMap<>();
    private static final Map<String, StageEntry> STAGE_CONFIGS = new HashMap<>();
    private static final Map<String, SpiritualRootEntry> ROOT_CONFIGS = new HashMap<>();

    static {
        BUILDER.comment("Other Configs");
        {
            BUILDER.push("ServerConfigs");
            SERVER_LOADED = BUILDER.define("ServerLoaded", 1);
            BUILDER.pop();
        }
        BUILDER.comment("Individual Realm Configuration");
        BUILDER.push("Realms");

        ModDiscovery.getRealmsForConfig().forEach(ServerConfigsImpl::createRealmConfig);

        BUILDER.pop();

        BUILDER.comment("Individual Stages Configuration");
        BUILDER.push("Stages");

        ModDiscovery.getStagesForConfig().forEach(ServerConfigsImpl::createStageConfig);

        BUILDER.pop();

        BUILDER.comment("Individual Spiritual Roots Configuration");
        BUILDER.push("SpiritualRoots");

        ModDiscovery.getSpiritualRootsForConfig().forEach(ServerConfigsImpl::createRootConfig);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void createRealmConfig(AbstractRealm realm) {
        RealmConfig config = realm.getDefaultConfig();
        BUILDER.push(realm.getId());

        RealmEntry entry = new RealmEntry(
                BUILDER.defineInRange("BaseHealth", config.baseHealth, 0.0, Double.MAX_VALUE),
                BUILDER.defineInRange("MinQi", config.minQi, 0.0, Double.MAX_VALUE),
                BUILDER.defineInRange("MaxQi", config.maxQi, config.minQi, Double.MAX_VALUE),
                BUILDER.defineInRange("BaseAttackDamage", config.baseAttackDamage, 0.0, Double.MAX_VALUE),
                BUILDER.defineInRange("BaseAttackSpeed", config.baseAttackSpeed, 0.0, Double.MAX_VALUE),
                BUILDER.defineInRange("KnockbackResistance", config.knockBackResistance, 0.0, 1.0),
                BUILDER.defineInRange("JumpHeight", config.jumpHeight, 0.0, Double.MAX_VALUE),
                BUILDER.defineInRange("MovementSpeed", config.movementSpeed, 0.0, Double.MAX_VALUE),
                BUILDER.defineInRange("SprintSpeed", config.sprintSpeed, 0.0, Double.MAX_VALUE),
                BUILDER.defineInRange("Coefficient", config.coefficient, 0.0, Double.MAX_VALUE),
                BUILDER.define("CanFly", config.canFly)
        );

        REALM_CONFIGS.put(realm.getId(), entry);

        BUILDER.pop();
    }

    public static void createStageConfig(AbstractStage stage) {
        StageConfig config = stage.getDefaultConfig();
        BUILDER.push(stage.getId());

        StageEntry entry = new StageEntry(
                BUILDER.defineInRange("MinQi", config.minQi, 0.0, Double.MAX_VALUE),
                BUILDER.defineInRange("MaxQi", config.maxQi, config.minQi, Double.MAX_VALUE),
                BUILDER.defineInRange("Coefficient", config.coefficient, 0.0, Double.MAX_VALUE),
                BUILDER.define("CanBreakthrough", config.canBreakthrough)
        );

        STAGE_CONFIGS.put(stage.getId(), entry);

        BUILDER.pop();
    }

    public static void createRootConfig(AbstractSpiritualRoot spiritualRoot) {
        SpiritualRootConfig config = spiritualRoot.getDefaultConfig();
        BUILDER.push(spiritualRoot.getId());

        SpiritualRootEntry.SpiritualRootEntryBuilder rootEntry = SpiritualRootEntry.builder();

        rootEntry.experiencePerLevel(BUILDER.define("ExperiencePerLevel", config.experiencePerLevel));
        rootEntry.maxLevel(BUILDER.define("MaxLevel", config.maxLevel));


        ROOT_CONFIGS.put(spiritualRoot.getId(), rootEntry.build());

        BUILDER.pop();
    }

    public static RealmEntry getRealmConfig(AbstractRealm realm) {
        return REALM_CONFIGS.getOrDefault(realm.getId(), null);
    }

    public static StageEntry getStageConfig(AbstractStage stage) {
        return STAGE_CONFIGS.getOrDefault(stage.getId(), null);
    }

    public static SpiritualRootEntry getSpiritualRootConfig(AbstractSpiritualRoot root) {
        return ROOT_CONFIGS.getOrDefault(root.getId(), null);
    }

    public static void init(){}
}
