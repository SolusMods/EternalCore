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

/**
 * Представляє екземпляр елемента, який може бути застосований до сутностей.
 * <p>
 * Класс ElementInstance інкапсулює конкретний елемент з додатковими даними, такими як кількість елемента
 * та будь-які інші специфічні дані, які зберігаються в NBT тезі. Цей клас також забезпечує
 * можливість серіалізації/десеріалізації в NBT формат для збереження та синхронізації.
 * </p>
 */
public class ElementInstance implements Cloneable{
    /** Ключ для ідентифікатора елемента в NBT даних */
    public static final String ELEMENT_KEY = "element";
    
    /** Ключ для кількості елемента в NBT даних */
    public static final String AMOUNT_KEY = "amount";
    
    /** Постачальник зареєстрованого елемента */
    protected final RegistrySupplier<Element> elementRegistrySupplier;
    
    /** Додаткові дані елемента, збережені у форматі NBT */
    @Nullable
    private CompoundTag tag = null;
    
    /** Прапорець, що вказує, чи було змінено стан екземпляра після останньої синхронізації */
    @Getter
    private boolean dirty = false;
    
    /** Кількість елемента */
    @Getter
    private float amount;

    /**
     * Створює новий екземпляр елемента.
     *
     * @param element Тип елемента
     */
    protected ElementInstance(Element element) {
        this.elementRegistrySupplier = ElementAPI.getElementRegistry().delegate(ElementAPI.getElementRegistry().getId(element));
    }

    /**
     * Може бути використаний для завантаження {@link ElementInstance} з {@link CompoundTag}.
     * <p>
     * {@link CompoundTag} повинен бути створений через {@link ElementInstance#toNBT()}
     * </p>
     *
     * @param tag NBT тег, що містить дані елемента
     * @return Новий екземпляр елемента, завантажений з NBT
     */
    public static ElementInstance fromNBT(CompoundTag tag) {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString(ELEMENT_KEY));
        if (!location.getNamespace().equals("minecraft")){
            Element element = ElementAPI.getElementRegistry().get(location);
            ElementInstance instance = element.createDefaultInstance();
            instance.deserialize(tag);
            return instance;
        } else {
            Element element = new Element(ElementType.NEUTRAL) {
                @Override
                public ElementType getType() {
                    return super.getType();
                }
            };
            return element.createDefaultInstance();
        }
    }

    /**
     * Викликається, коли цей екземпляр елемента додається до сутності.
     * Делегує виклик до відповідного елемента.
     *
     * @param entity Сутність, до якої додається елемент
     */
    public void onAdd(LivingEntity entity) {
        this.getElement().onAdd(this, entity);
    }

    /**
     * Використовується для отримання типу {@link Element} цього екземпляра.
     *
     * @return Елемент, пов'язаний з цим екземпляром
     */
    public Element getElement() {
        return elementRegistrySupplier.get();
    }

    /**
     * Отримує ідентифікатор елемента.
     *
     * @return ResourceLocation ідентифікатор елемента
     */
    public ResourceLocation getElementId() {
        return this.elementRegistrySupplier.getId();
    }

    /**
     * Встановлює кількість елемента та позначає екземпляр як змінений.
     *
     * @param amount Нова кількість елемента
     */
    public void setAmount(float amount) {
        this.amount = amount;
        markDirty();
    }

    /**
     * Отримує протилежний елемент для цього екземпляра.
     *
     * @param entity Сутність, до якої застосовано елемент
     * @return Протилежний елемент або null, якщо протилежного елемента немає
     */
    public @Nullable Element getOpposite(LivingEntity entity){
        return getElement().getOpposite(this, entity);
    }

    /**
     * Використовується для створення точної копії поточного екземпляра.
     *
     * @return Копія цього екземпляра
     */
    public ElementInstance copy() {
        ElementInstance clone = new ElementInstance(getElement());
        clone.dirty = this.dirty;
        clone.amount = this.amount;
        if (this.tag != null) clone.tag = this.tag.copy();
        return clone;
    }

    /**
     * Цей метод використовується для забезпечення того, що вся необхідна інформація збережена.
     * <p>
     * Перевизначте {@link ElementInstance#serialize(CompoundTag)} для збереження власних даних.
     * </p>
     *
     * @return CompoundTag з усіма даними екземпляра
     */
    public final CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(ELEMENT_KEY, this.getElementId().toString());
        serialize(nbt);
        return nbt;
    }

    /**
     * Може бути використаний для збереження користувацьких даних.
     *
     * @param nbt Тег, в який будуть збережені дані
     * @return Тег з серіалізованими даними
     */
    public CompoundTag serialize(CompoundTag nbt) {
        if (this.tag != null) nbt.put("tag", this.tag.copy());
        nbt.putFloat(AMOUNT_KEY, getAmount());
        return nbt;
    }

    /**
     * Може бути використаний для завантаження користувацьких даних.
     *
     * @param tag Тег, з якого будуть завантажені дані
     */
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
        amount = tag.getFloat(AMOUNT_KEY);
    }

    /**
     * Позначає поточний екземпляр як змінений.
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * Цей метод викликається для позначення, що {@link ElementInstance} був синхронізований з клієнтами.
     * <p>
     * <strong>НЕ</strong> використовуйте цей метод самостійно!
     * </p>
     */
    @ApiStatus.Internal
    public void resetDirty() {
        this.dirty = false;
    }

    /**
     * Порівнює цей екземпляр з іншим об'єктом.
     *
     * @param o Об'єкт для порівняння
     * @return true, якщо об'єкти рівні, false - інакше
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementInstance instance = (ElementInstance) o;
        return this.getElementId().equals(instance.getElementId()) &&
                elementRegistrySupplier.getRegistryKey().equals(instance.elementRegistrySupplier.getRegistryKey());
    }

    /**
     * Обчислює хеш-код для цього екземпляра.
     *
     * @return Хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getElementId(), elementRegistrySupplier.getRegistryKey());
    }

    /**
     * Перевіряє, чи належить елемент до вказаного тегу.
     *
     * @param tag Тег для перевірки
     * @return true, якщо елемент належить до тегу, false - інакше
     */
    public boolean is(TagKey<Element> tag) {
        return this.elementRegistrySupplier.is(tag);
    }

    /**
     * Використовується для отримання {@link MutableComponent} імені цього духовного кореня для перекладу.
     *
     * @return Компонент для відображення імені
     */
    public MutableComponent getDisplayName() {
        return this.getElement().getName();
    }

    /**
     * Створює клон цього екземпляра.
     *
     * @return Клон цього екземпляра
     */
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