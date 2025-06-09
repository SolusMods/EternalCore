package io.github.solusmods.eternalcore.testing.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import io.github.solusmods.eternalcore.config.ModuleConstants
import io.github.solusmods.eternalcore.element.api.Element
import io.github.solusmods.eternalcore.element.api.ElementAPI.elementRegistryKey
import lombok.AccessLevel
import lombok.NoArgsConstructor
import java.util.function.Supplier

object RegistryTest {
    @JvmField
    val ELEMENTS: DeferredRegister<Element> =
        DeferredRegister.create<Element>(ModuleConstants.MOD_ID, elementRegistryKey)
    @JvmField
    val TEST_ELEMENT: RegistrySupplier<AirElement> =
        ELEMENTS.register("test_element", Supplier { AirElement() })

    fun init() {
        ELEMENTS.register()
    }
}

