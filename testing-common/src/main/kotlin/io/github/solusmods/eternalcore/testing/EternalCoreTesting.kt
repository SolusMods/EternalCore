package io.github.solusmods.eternalcore.testing

import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.platform.Platform
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI.getAbilitiesFrom
import io.github.solusmods.eternalcore.element.api.ElementAPI.getElementsFrom
import io.github.solusmods.eternalcore.realm.api.RealmAPI.getReachedRealmsFrom
import io.github.solusmods.eternalcore.realm.api.RealmAPI.getRealmFrom
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootAPI.getSpiritualRootFrom
import io.github.solusmods.eternalcore.stage.api.StageAPI.getReachedStagesFrom
import io.github.solusmods.eternalcore.stage.api.StageAPI.getStageFrom
import io.github.solusmods.eternalcore.testing.client.EternalCoreTestingClient
import io.github.solusmods.eternalcore.testing.registry.RegistryTest
import io.github.solusmods.eternalcore.testing.registry.TestAttributeRegistry
import net.fabricmc.api.EnvType
import net.minecraft.server.level.ServerPlayer
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object EternalCoreTesting {
    @JvmStatic
    val LOG: Logger = LoggerFactory.getLogger("EternalCore - Testing")
    @JvmStatic
    fun init() {
        RegistryTest.init()

        if (Platform.getEnv() == EnvType.CLIENT) {
            EternalCoreTestingClient.init()
            TestAttributeRegistry.init()
        }

        PlayerEvent.PLAYER_JOIN.register { player ->
            if (player is ServerPlayer) {
                val realms = getRealmFrom(player) ?: return@register
                val reachedRealms = getReachedRealmsFrom(player) ?: return@register
                realms.setRealm(RegistryTest.TEST_REALM.get(), false)
                reachedRealms.addRealm(RegistryTest.TEST_REALM.get(), false)
                val stages = getStageFrom(player) ?: return@register
                val reachedStages = getReachedStagesFrom(player) ?: return@register
                stages.setStage(RegistryTest.TEST_STAGE.get(), false)
                reachedStages.addStage(RegistryTest.TEST_STAGE.get(), false)
                val elements = getElementsFrom(player) ?: return@register
                elements.addElement(RegistryTest.TEST_ELEMENT.get(), false)
                val roots = getSpiritualRootFrom(player) ?: return@register
                roots.addSpiritualRoot(RegistryTest.TEST_ROOT.get(), false)
                val abilities = getAbilitiesFrom(player) ?: return@register
                abilities.learnAbility(RegistryTest.TEST_ABILITY.get())
            }

        }
    }
}
