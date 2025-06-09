package io.github.solusmods.eternalcore.abilities.api

import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.AttributeModifier

/**
 * Attribute Template for easier attribute modifier implementation.
 */
@JvmRecord
data class AttributeTemplate(
    val location: ResourceLocation?,
    val amount: Double,
    val operation: AttributeModifier.Operation?
) {
    constructor(
        id: String?,
        amount: Double,
        operation: AttributeModifier.Operation?
    ) : this(EternalCoreAbilities.create(id!!), amount, operation)

    fun create(i: Double): AttributeModifier {
        return AttributeModifier(this.location, this.amount * i, this.operation)
    }

    fun create(location: ResourceLocation?, i: Double): AttributeModifier {
        return AttributeModifier(location, this.amount * i, this.operation)
    }

    fun id(): ResourceLocation? {
        return this.location
    }
}
