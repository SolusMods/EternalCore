package io.github.solusmods.eternalcore.element.impl;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import io.github.solusmods.eternalcore.element.EternalCoreElements;
import io.github.solusmods.eternalcore.element.api.Element;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import static io.github.solusmods.eternalcore.element.ModuleConstants.MOD_ID;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElementRegistry {

    private static final ResourceLocation registryId = EternalCoreElements.create("elements");
    public static final Registrar<Element> ELEMENTS = RegistrarManager.get(MOD_ID).<Element>builder(registryId)
            .syncToClients().build();
    public static final ResourceKey<Registry<Element>> KEY = (ResourceKey<Registry<Element>>) ELEMENTS.key();

    public static void init(){}
}
