package io.github.solusmods.eternalcore.testing.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI.abilityRegistryKey
import io.github.solusmods.eternalcore.testing.ModuleConstants
import io.github.solusmods.eternalcore.element.api.Element
import io.github.solusmods.eternalcore.element.api.ElementAPI.elementRegistryKey
import io.github.solusmods.eternalcore.realm.api.RealmAPI.realmRegistryKey
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootAPI.spiritualRootRegistryKey
import io.github.solusmods.eternalcore.stage.api.StageAPI.stageRegistryKey
import java.util.function.Supplier

object RegistryTest {
    @JvmField
    val ELEMENTS: DeferredRegister<Element> =
        DeferredRegister.create<Element>(ModuleConstants.MOD_ID, elementRegistryKey)
    @JvmField
    val REALMS = DeferredRegister.create(ModuleConstants.MOD_ID, realmRegistryKey)

    @JvmField
    val STAGES = DeferredRegister.create(ModuleConstants.MOD_ID, stageRegistryKey)

    @JvmField
    val SPIRITUAL_ROOTS = DeferredRegister.create(ModuleConstants.MOD_ID, spiritualRootRegistryKey)

    @JvmField
    val ABILITIES = DeferredRegister.create(ModuleConstants.MOD_ID, abilityRegistryKey)

    @JvmField
    val TEST_ELEMENT: RegistrySupplier<AirElement> =
        ELEMENTS.register("test_element", Supplier { AirElement() })

    @JvmField
    val TEST_REALM = REALMS.register("test_realm") { TestRealm() }

    @JvmField
    val TEST_NEXT_REALM = REALMS.register("test_next_realm") { TestNextRealm() }

    @JvmField
    val TEST_STAGE = STAGES.register("test_stage") { TestStage() }

    @JvmField
    val TEST_ROOT = SPIRITUAL_ROOTS.register("test_root") { TestSpiritualRoot() }

    @JvmField
    val TEST_ABILITY = ABILITIES.register("test_ability") { TestAbility() }

    fun init() {
        ELEMENTS.register()
        REALMS.register()
        STAGES.register()
        SPIRITUAL_ROOTS.register()
        ABILITIES.register()
    }
}

