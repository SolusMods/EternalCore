package io.github.solusmods.eternalcore.api.realm;

import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

/**
 * Запис, що представляє шаблон модифікатора атрибутів.
 * <p>
 * Використовується для створення модифікаторів атрибутів при застосуванні
 * Реалму  до сутності.
 * </p>
 */

public record AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation,
                                @Nullable Int2DoubleFunction curve) {
    public AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        this(id, amount, operation, null);
    }

    public AttributeModifier create(int i) {
        return this.curve != null ? new AttributeModifier(this.id, this.curve.apply(i), this.operation) : new AttributeModifier(this.id, this.amount * (double) (i + 1), this.operation);
    }
}
