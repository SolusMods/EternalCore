package io.github.solusmods.eternalcore.element.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public abstract class Element {
    private final ElementType type;

    public ElementInstance createDefaultInstance() {
        return new ElementInstance(this);
    }


    /**
     * Used to get the {@link ResourceLocation} id of this {@link Element}.
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return ElementAPI.getElementRegistry().getId(this);
    }

    /**
     * Used to get the {@link MutableComponent} name of this {@link Element} for translation.
     */
    @Nullable
    public MutableComponent getName() {
        ResourceLocation id = this.getRegistryName();
        return id == null ? null : Component.translatable(String.format("%s.element.%s", id.getNamespace(), id.getPath().replace('/', '.')));
    }

    public @Nullable Element getOpposite(ElementInstance instance, LivingEntity entity){return null;}
}
