package io.github.solusmods.eternalcore.element.api;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import io.github.solusmods.eternalcore.element.impl.ElementRegistry;
import io.github.solusmods.eternalcore.element.impl.ElementsStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElementAPI {
    /**
     * This Method returns the {@link Element} Registry.
     * It can be used to load {@link Element}s from the Registry.
     */
    public static Registrar<Element> getElementRegistry() {
        return ElementRegistry.ELEMENTS;
    }

    /**
     * This Method returns the Registry Key of the {@link ElementRegistry}.
     * It can be used to create {@link DeferredRegister} instances
     */
    public static ResourceKey<Registry<Element>> getElementRegistryKey() {
        return ElementRegistry.KEY;
    }

    /**
     * Can be used to load the {@link ElementsStorage} from an {@link LivingEntity}.
     */
    public static Elements getDominantElementFrom(@NonNull LivingEntity entity) {
        return entity.eternalCore$getStorage(ElementsStorage.getKey());
    }

    /**
     * Can be used to load the {@link ElementsStorage} from an {@link LivingEntity}.
     */
    public static Elements getElementsFrom(@NonNull LivingEntity entity) {
        return entity.eternalCore$getStorage(ElementsStorage.getKey());
    }
}
