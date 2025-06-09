package io.github.solusmods.eternalcore.testing.configs

import io.github.solusmods.eternalcore.config.ConfigRegistry
import io.github.solusmods.eternalcore.config.api.Comment
import io.github.solusmods.eternalcore.config.api.CoreConfig
import io.github.solusmods.eternalcore.config.api.CoreSubConfig
import io.github.solusmods.eternalcore.config.api.SyncToClient
import io.github.solusmods.eternalcore.testing.EternalCoreTesting.LOG
import io.github.solusmods.eternalcore.testing.registry.RegistryTest
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*

@SyncToClient
@Suppress("unchecked_cast")
class TestConfig : CoreConfig() {
    public override fun getFileName(): String {
        return "eternalcore_test/test_folder/test_config"
    }

    var testResourceLocation: ResourceLocation? = RegistryTest.TEST_ELEMENT.getId()

    @Comment("Random Lists of Values")
    var random_lists: RandomLists = RandomLists()

    class RandomLists : CoreSubConfig() {
        var numberLists: NumberLists = NumberLists()

        class NumberLists : CoreSubConfig() {
            var doubleList: MutableList<Double?> = mutableListOf<Double?>(1.0, 2.0, 3.0)
            var intList: MutableList<Int?> = mutableListOf<Int?>(69, 420)
            var longList: MutableList<Long?> = mutableListOf<Long?>(1L, 2L, 3L)
        }

        @Comment("Who doesn't hate bugs?")
        var stringList: MutableList<String?> = mutableListOf<String?>("I", "Hate", "Bugs", "soooooo much!")
    }

    @Comment("Test Sub Config")
    var test_subConfig: TestSubConfig = TestSubConfig()

    class TestSubConfig : CoreSubConfig() {
        var initialMessage: String = "Config working!"
    }

    companion object {
        fun printTestConfig(player: Player) {
            val level = player.level()
            logConfigValue(
                player,
                level,
                "Test Config Sync",
                Objects.requireNonNull<TestConfig>(ConfigRegistry.getConfig<TestConfig>(TestConfig::class.java as Class<TestConfig?>)).testResourceLocation
            )
        }

        private fun logConfigValue(player: Player, level: Level, configType: String?, value: Any?) {
            LOG.info(
                "{} for entity {} on {}:\n{}", configType, player.getName(),
                if (level.isClientSide()) "client" else "server", value
            )
        }
    }
}
