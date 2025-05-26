package io.github.solusmods.eternalcore.spiritual_root.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Запис, що представляє шаблон модифікатора атрибутів.
 * <p>
 * Використовується для створення модифікаторів атрибутів при застосуванні
 * Духовного Кореня до сутності.
 * </p>
 */
public record AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
    /**
     * Створює новий шаблон модифікатора атрибутів.
     *
     * @param id Ідентифікатор модифікатора
     * @param amount Значення модифікатора
     * @param operation Операція модифікатора
     */
    public AttributeTemplate {
    }

    /**
     * Створює новий модифікатор атрибутів на основі цього шаблону.
     *
     * @return Новий модифікатор атрибутів
     */
    public AttributeModifier create() {
        return new AttributeModifier(this.id, this.amount, this.operation);
    }

    /**
     * Отримує ідентифікатор модифікатора.
     *
     * @return Ідентифікатор модифікатора
     */
    public ResourceLocation id() {
        return this.id;
    }

    /**
     * Отримує значення модифікатора.
     *
     * @return Значення модифікатора
     */
    public double amount() {
        return this.amount;
    }

    /**
     * Отримує операцію модифікатора.
     *
     * @return Операція модифікатора
     */
    public AttributeModifier.Operation operation() {
        return this.operation;
    }
}
