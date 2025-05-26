package io.github.solusmods.eternalcore.abilities.api;

import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Attribute Template for easier attribute modifier implementation.
 */
public record AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
    public AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        this.id = id;
        this.amount = amount;
        this.operation = operation;
    }

    public AttributeTemplate(String id, double amount, AttributeModifier.Operation operation) {
        this(EternalCoreAbilities.create(id), amount, operation);
    }

    public AttributeModifier create(double i) {
        return new AttributeModifier(this.id, this.amount * i, this.operation);
    }

    public AttributeModifier create(ResourceLocation location, double i) {
        return new AttributeModifier(location, this.amount * i, this.operation);
    }

    public ResourceLocation id() {
        return this.id;
    }

    public double amount() {
        return this.amount;
    }

    public AttributeModifier.Operation operation() {
        return this.operation;
    }
}
