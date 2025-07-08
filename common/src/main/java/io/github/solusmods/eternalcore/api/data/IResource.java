package io.github.solusmods.eternalcore.api.data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public interface IResource {

    String getId();

    String getClassName();

    ResourceLocation getResource();

    @Nullable
    default MutableComponent getName() {
        var id = getResource();
        return id == null ? null : Component.translatable(id.getNamespace() + "." + getClassName().toLowerCase() + "." + id.getPath().replace('/', '.'));
    }

    default String getNameTranslationKey() {
        return ((TranslatableContents) getName().getContents()).getKey();
    }
}
