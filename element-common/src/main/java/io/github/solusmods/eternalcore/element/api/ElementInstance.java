package io.github.solusmods.eternalcore.element.api;

import dev.architectury.registry.registries.RegistrySupplier;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ElementInstance implements Cloneable{
    public static final String ELEMENT_KEY = "element";
    public static final String AMOUNT_KEY = "amount";
    protected final RegistrySupplier<Element> elementRegistrySupplier;
    @Nullable
    private CompoundTag tag = null;
    @Getter
    private boolean dirty = false;
    @Getter
    private float amount;

    protected ElementInstance(Element element) {
        this.elementRegistrySupplier = ElementAPI.getElementRegistry().delegate(ElementAPI.getElementRegistry().getId(element));
    }

    /**
     * Can be used to load a {@link ElementInstance} from a {@link CompoundTag}.
     * <p>
     * The {@link CompoundTag} has to be created though {@link ElementInstance#toNBT()}
     */
    public static ElementInstance fromNBT(CompoundTag tag) throws NullPointerException {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString(ELEMENT_KEY));
        Element element = ElementAPI.getElementRegistry().get(location);
        if (element == null) throw new NullPointerException("No spiritual_root found for location: " + location);
        ElementInstance instance = element.createDefaultInstance();
        instance.deserialize(tag);
        return instance;
    }

    /**
     * Used to get the {@link Element} type of this Instance.
     */
    public Element getElement() {
        return elementRegistrySupplier.get();
    }

    public ResourceLocation getElementId() {
        return this.elementRegistrySupplier.getId();
    }

    public void setAmount(float amount) {
        this.amount = amount;
        markDirty();
    }

    public @Nullable Element getOpposite(LivingEntity entity){
        return getElement().getOpposite(this, entity);
    }

    /**
     * Used to create an exact copy of the current instance.
     */
    public ElementInstance copy() {
        ElementInstance clone = new ElementInstance(getElement());
        clone.dirty = this.dirty;
        clone.amount = this.amount;
        if (this.tag != null) clone.tag = this.tag.copy();
        return clone;
    }

    /**
     * This method is used to ensure that all required information are stored.
     * <p>
     * Override {@link ElementInstance#serialize(CompoundTag)} to store your custom Data.
     */
    public final CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(ELEMENT_KEY, this.getElementId().toString());
        serialize(nbt);
        return nbt;
    }

    /**
     * Can be used to save custom data.
     *
     * @param nbt Tag with data from {@link ElementInstance#fromNBT(CompoundTag)}
     */
    public CompoundTag serialize(CompoundTag nbt) {
        if (this.tag != null) nbt.put("tag", this.tag.copy());
        nbt.putFloat(AMOUNT_KEY, getAmount());
        return nbt;
    }

    /**
     * Can be used to load custom data.
     */
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
        amount = tag.getFloat(AMOUNT_KEY);
    }

    /**
     * Marks the current instance as dirty.
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * This Method is invoked to indicate that a {@link ElementInstance} has been synced with the clients.
     * <p>
     * Do <strong>NOT</strong> use that method on our own!
     */
    @ApiStatus.Internal
    public void resetDirty() {
        this.dirty = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementInstance instance = (ElementInstance) o;
        return this.getElementId().equals(instance.getElementId()) &&
                elementRegistrySupplier.getRegistryKey().equals(instance.elementRegistrySupplier.getRegistryKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getElementId(), elementRegistrySupplier.getRegistryKey());
    }

    public boolean is(TagKey<Element> tag) {
        return this.elementRegistrySupplier.is(tag);
    }

    /**
     * Used to get the {@link MutableComponent} name of this spiritual_root for translation.
     */
    public MutableComponent getDisplayName() {
        return this.getElement().getName();
    }

    @Override
    public ElementInstance clone() {
        try {
            ElementInstance clone = (ElementInstance) super.clone();
            clone.dirty = this.dirty;
            clone.amount = this.amount;
            if (this.tag != null) clone.tag = this.tag.copy();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
