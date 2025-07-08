package io.github.solusmods.eternalcore.api.qi_energy;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.data.IResource;
import io.github.solusmods.eternalcore.api.registry.ElementTypeRegistry;
import io.github.solusmods.eternalcore.api.storage.INBTSerializable;
import lombok.Getter;
import lombok.val;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.HashMap;

@Getter
public abstract class ElementType implements INBTSerializable<CompoundTag>, IResource {

    private String elementId;
    private String elementName;
    private final int color;

    public ElementType(int color) {
        this.color = color;
    }

    public static ElementType fromNBT(CompoundTag tag) throws NullPointerException {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString("id"));
        ElementType abstractRealm = QiEnergyAPI.getElementRegistry().get(location);
        abstractRealm.deserialize(tag);
        return abstractRealm;
    }

    public abstract ResourceLocation getResource();

    public final ResourceLocation creteResource(String name){
        return EternalCore.create(name);
    }

    public abstract HashMap<ElementType, Float> getOpposite();

    @Override
    public String getClassName() {
        return "elemental";
    }

    public final String getElementName(){
        if(elementName == null) elementName = getResource().getPath().intern();
        return elementName;
    }

    public final String getElementId(){
        if(elementId == null) elementId = getResource().toString().intern();
        return elementId;
    }

    @Override
    public CompoundTag toNBT() {
        val nbt = new CompoundTag();
        nbt.putString("id", getResource().toString());
        serialize(nbt);
        return nbt;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {

    }

    public boolean is(TagKey<ElementType> tag) {
        return ElementTypeRegistry.getRegistrySupplier(this).is(tag);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ElementType other = (ElementType) obj;
        ResourceLocation thisId = this.getResource();
        ResourceLocation otherId = other.getResource();
        return thisId != null && thisId.equals(otherId);
    }

    /**
     * Повертає хеш-код для цього Реалму.
     *
     * @return Хеш-код
     */
    @Override
    public int hashCode() {
        ResourceLocation resource = getResource();
        return resource != null ? resource.hashCode() : 0;
    }

    /**
     * Повертає рядкове представлення цього Реалму.
     *
     * @return Рядкове представлення
     */
    @Override
    public String toString() {
        return String.format("%s{id='%s'}", this.getClass().getSimpleName(), getId());
    }


}
