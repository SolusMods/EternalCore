package io.github.solusmods.eternalcore.testing.configs;

import io.github.solusmods.eternalcore.config.ConfigRegistry;
import io.github.solusmods.eternalcore.config.api.Comment;
import io.github.solusmods.eternalcore.config.api.CoreConfig;
import io.github.solusmods.eternalcore.config.api.CoreSubConfig;
import io.github.solusmods.eternalcore.config.api.SyncToClient;
import io.github.solusmods.eternalcore.testing.registry.RegistryTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

import static io.github.solusmods.eternalcore.config.EternalCoreConfig.LOG;

@SyncToClient
public class TestConfig extends CoreConfig {
    public String getFileName() {
        return "eternalcore_test/test_folder/test_config";
    }

    public ResourceLocation testResourceLocation = RegistryTest.TEST_ELEMENT.getId();

    @Comment("Random Lists of Values")
    public RandomLists random_lists = new RandomLists();
    public static class RandomLists extends CoreSubConfig {
        public NumberLists numberLists = new NumberLists();
        public static class NumberLists extends CoreSubConfig {
            public List<Double> doubleList = List.of(1.0, 2D, 3d);
            public List<Integer> intList = List.of(69, 420);
            public List<Long> longList = List.of(1L, 2L, 3L);
        }
        @Comment("Who doesn't hate bugs?")
        public List<String> stringList = List.of("I", "Hate", "Bugs", "soooooo much!");
    }

    @Comment("Test Sub Config")
    public TestSubConfig test_subConfig = new TestSubConfig();
    public static class TestSubConfig extends CoreSubConfig {
        public String initialMessage = "Config working!";
    }

    public static void printTestConfig(Player player) {
        Level level = player.level();
        logConfigValue(player, level, "Test Config Sync", ConfigRegistry.getConfig(TestConfig.class).testResourceLocation);
    }

    private static void logConfigValue(Player player, Level level, String configType, Object value) {
        LOG.info("{} for entity {} on {}:\n{}", configType, player.getName(),
                level.isClientSide() ? "client" : "server", value);
    }
}
