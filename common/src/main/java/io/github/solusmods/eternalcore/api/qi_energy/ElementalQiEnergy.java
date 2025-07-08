package io.github.solusmods.eternalcore.api.qi_energy;

import io.github.solusmods.eternalcore.api.storage.INBTSerializable;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class ElementalQiEnergy extends AbstractQiEnergy implements INBTSerializable<CompoundTag> {

    @Getter
    private ElementType element;
    @Nullable
    private CompoundTag tag = null;

    public ElementalQiEnergy(ElementType element, double amount) {
        super(amount);
        this.element = element;
    }

    /**
     * Creates an {@link ElementalQiEnergy} instance from a {@link CompoundTag}.
     *
     * @param tag The CompoundTag containing the data.
     * @return An {@link ElementalQiEnergy} instance.
     */
    public static ElementalQiEnergy fromNBT(CompoundTag tag) {
        ElementType element = ElementType.fromNBT(tag);
        double amount = tag.getDouble("amount");
        ElementalQiEnergy qiEnergy = new ElementalQiEnergy(element, amount);
        qiEnergy.deserialize(tag);
        return qiEnergy;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("Element", element.toNBT());
        nbt.putDouble("amount", amount);
        serialize(nbt);
        return nbt;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        if (this.tag != null) tag.put("tag", this.tag.copy());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
        this.element = ElementType.fromNBT(tag);
        this.amount = tag.getDouble("amount");
    }

    @Override
    public String toString() {
        return String.format("%s{element=%s, amount=%s}", this.getClass().getSimpleName(), getElement().toString(), getAmount());
    }
}
