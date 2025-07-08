package io.github.solusmods.eternalcore.api.registry;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.qi_energy.ElementType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import static io.github.solusmods.eternalcore.EternalCore.REGISTRIES;

/**
 * Реєстр елементів для моду EternalCore.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ElementTypeRegistry {

    private static final ResourceLocation REGISTRY_ID = EternalCore.create("element_types");

    public static final Registrar<ElementType> ELEMENT_TYPES = REGISTRIES.get()
            .<ElementType>builder(REGISTRY_ID)
            .syncToClients().build();

    public static final ResourceKey<Registry<ElementType>> KEY = (ResourceKey<Registry<ElementType>>) ELEMENT_TYPES.key();

    public static RegistrySupplier<ElementType> getRegistrySupplier(ElementType elementType) {
        return ELEMENT_TYPES.delegate(ELEMENT_TYPES.getId(elementType));
    }

    public static void init() {
        // Логіка ініціалізації, якщо потрібно
    }
}
