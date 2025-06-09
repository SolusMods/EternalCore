package io.github.solusmods.eternalcore.spiritual_root.api

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.AttributeModifier

/**
 * Створює новий шаблон модифікатора атрибутів.
 *
 * @param id Ідентифікатор модифікатора
 * @param amount Значення модифікатора
 * @param operation Операція модифікатора
 */
data class AttributeTemplate(val id: ResourceLocation, val amount: Double, val operation: AttributeModifier.Operation) {
    /**
     * Створює новий модифікатор атрибутів на основі цього шаблону.
     *
     * @return Новий модифікатор атрибутів
     */
    fun create(): AttributeModifier {
        return AttributeModifier(this.id, this.amount, this.operation)
    }

    /**
     * Отримує ідентифікатор модифікатора.
     *
     * @return Ідентифікатор модифікатора
     */
    fun id(): ResourceLocation? {
        return this.id
    }
}
