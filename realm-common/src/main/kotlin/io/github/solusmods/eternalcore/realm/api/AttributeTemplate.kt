package io.github.solusmods.eternalcore.realm.api

import it.unimi.dsi.fastutil.ints.Int2DoubleFunction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.AttributeModifier

/**
 * Запис, що представляє шаблон модифікатора атрибутів.
 *
 *
 * Використовується для створення модифікаторів атрибутів при застосуванні
 * Реалму  до сутності.
 *
 */
@JvmRecord
data class AttributeTemplate(
    val id: ResourceLocation?,
    val amount: Double,
    val operation: AttributeModifier.Operation?,
    val curve: Int2DoubleFunction?
) {
    constructor(id: ResourceLocation?, amount: Double, operation: AttributeModifier.Operation?) : this(
        id,
        amount,
        operation,
        null
    )

    fun create(i: Int): AttributeModifier {
        return if (this.curve != null) AttributeModifier(
            this.id,
            this.curve.apply(i),
            this.operation
        ) else AttributeModifier(this.id, this.amount * (i + 1).toDouble(), this.operation)
    }
}
