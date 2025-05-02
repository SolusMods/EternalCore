package io.github.solusmods.eternalcore.testing.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.config.ModuleConstants;
import io.github.solusmods.eternalcore.element.api.Element;
import io.github.solusmods.eternalcore.element.api.ElementAPI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistryTest {
    public static final DeferredRegister<Element> ELEMENTS = DeferredRegister.create(ModuleConstants.MOD_ID, ElementAPI.getElementRegistryKey());
    public static final RegistrySupplier<AirElement> TEST_ELEMENT = ELEMENTS.register("test_element", AirElement::new);

    public static void init(){
        ELEMENTS.register();
    }
}

