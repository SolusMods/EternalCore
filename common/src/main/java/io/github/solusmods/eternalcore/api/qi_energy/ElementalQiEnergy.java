package io.github.solusmods.eternalcore.api.qi_energy;

import io.github.solusmods.eternalcore.api.storage.INBTSerializable;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
        var element = QiEnergyAPI.getElementRegistry().get(ResourceLocation.parse(tag.getString("Element")));
        var qiEnergy = new ElementalQiEnergy(element, 0);
        qiEnergy.deserialize(tag);
        return qiEnergy;
    }

    @Override
    public CompoundTag toNBT() {
        var nbt = new CompoundTag();
        nbt.putString("Element", getElement().getResource().toString());
        serialize(nbt);
        return nbt;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        if (this.tag != null) tag.put("tag", this.tag.copy());
        tag.putDouble("amount", amount);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
        this.amount = tag.getDouble("amount");
    }

    @Override
    public String toString() {
        return String.format("%s{element=%s, amount=%s}", this.getClass().getSimpleName(), getElement().toString(), getAmount());
    }
}
