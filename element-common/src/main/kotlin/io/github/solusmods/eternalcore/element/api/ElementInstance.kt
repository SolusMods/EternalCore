package io.github.solusmods.eternalcore.element.api

import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.LivingEntity
import org.jetbrains.annotations.ApiStatus
import java.util.*

/**
 * Представляє екземпляр елемента, який може бути застосований до сутностей.
 *
 *
 * Класс ElementInstance інкапсулює конкретний елемент з додатковими даними, такими як кількість елемента
 * та будь-які інші специфічні дані, які зберігаються в NBT тезі. Цей клас також забезпечує
 * можливість серіалізації/десеріалізації в NBT формат для збереження та синхронізації.
 *
 */
class ElementInstance(element: Element?) : Cloneable {
    /** Постачальник зареєстрованого елемента  */
    protected val elementRegistrySupplier: RegistrySupplier<Element?> = ElementAPI.elementRegistry.delegate(ElementAPI.elementRegistry.getId(element))

    /** Додаткові дані елемента, збережені у форматі NBT  */
    var tag: CompoundTag? = null

    /** Прапорець, що вказує, чи було змінено стан екземпляра після останньої синхронізації  */
     var dirty = false

    /** Кількість елемента  */
     var amount = 0f
        set(value) {
            field = value
            markDirty()
        }

    /**
     * Викликається, коли цей екземпляр елемента додається до сутності.
     * Делегує виклик до відповідного елемента.
     *
     * @param entity Сутність, до якої додається елемент
     */
    fun onAdd(entity: LivingEntity) {
        this.element!!.onAdd(this, entity)
    }

    val element: Element?
        /**
         * Використовується для отримання типу [Element] цього екземпляра.
         *
         * @return Елемент, пов'язаний з цим екземпляром
         */
        get() = elementRegistrySupplier.get()

    val elementId: ResourceLocation
        /**
         * Отримує ідентифікатор елемента.
         *
         * @return ResourceLocation ідентифікатор елемента
         */
        get() = this.elementRegistrySupplier.id

    /**
     * Отримує протилежний елемент для цього екземпляра.
     *
     * @param entity Сутність, до якої застосовано елемент
     * @return Протилежний елемент або null, якщо протилежного елемента немає
     */
    fun getOpposite(entity: LivingEntity): Element? {
        return this.element!!.getOpposite(this, entity)
    }

    /**
     * Використовується для створення точної копії поточного екземпляра.
     *
     * @return Копія цього екземпляра
     */
    fun copy(): ElementInstance {
        val clone = ElementInstance(this.element)
        clone.dirty = this.dirty
        clone.amount = this.amount
        if (this.tag != null) clone.tag = this.tag!!.copy()
        return clone
    }

    /**
     * Цей метод використовується для забезпечення того, що вся необхідна інформація збережена.
     *
     *
     * Перевизначте [ElementInstance.serialize] для збереження власних даних.
     *
     *
     * @return CompoundTag з усіма даними екземпляра
     */
    fun toNBT(): CompoundTag {
        val nbt = CompoundTag()
        nbt.putString(ELEMENT_KEY, this.elementId.toString())
        serialize(nbt)
        return nbt
    }

    /**
     * Може бути використаний для збереження користувацьких даних.
     *
     * @param nbt Тег, в який будуть збережені дані
     * @return Тег з серіалізованими даними
     */
    fun serialize(nbt: CompoundTag): CompoundTag {
        if (this.tag != null) nbt.put("tag", this.tag!!.copy())
        nbt.putFloat(AMOUNT_KEY, amount)
        return nbt
    }

    /**
     * Може бути використаний для завантаження користувацьких даних.
     *
     * @param tag Тег, з якого будуть завантажені дані
     */
    fun deserialize(tag: CompoundTag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag")
        amount = tag.getFloat(AMOUNT_KEY)
    }

    /**
     * Позначає поточний екземпляр як змінений.
     */
    fun markDirty() {
        this.dirty = true
    }

    /**
     * Цей метод викликається для позначення, що [ElementInstance] був синхронізований з клієнтами.
     *
     *
     * **НЕ** використовуйте цей метод самостійно!
     *
     */
    @ApiStatus.Internal
    fun resetDirty() {
        this.dirty = false
    }

    /**
     * Порівнює цей екземпляр з іншим об'єктом.
     *
     * @param other Об'єкт для порівняння
     * @return true, якщо об'єкти рівні, false - інакше
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val instance = other as ElementInstance
        return this.elementId == instance.elementId &&
                elementRegistrySupplier.getRegistryKey() == instance.elementRegistrySupplier.getRegistryKey()
    }

    /**
     * Обчислює хеш-код для цього екземпляра.
     *
     * @return Хеш-код
     */
    override fun hashCode(): Int {
        return Objects.hash(this.elementId, elementRegistrySupplier.getRegistryKey())
    }

    /**
     * Перевіряє, чи належить елемент до вказаного тегу.
     *
     * @param tag Тег для перевірки
     * @return true, якщо елемент належить до тегу, false - інакше
     */
    fun `is`(tag: TagKey<Element?>): Boolean {
        return this.elementRegistrySupplier.`is`(tag)
    }

    val displayName: MutableComponent?
        /**
         * Використовується для отримання [MutableComponent] імені цього духовного кореня для перекладу.
         *
         * @return Компонент для відображення імені
         */
        get() = this.element!!.name

    /**
     * Створює клон цього екземпляра.
     *
     * @return Клон цього екземпляра
     */
    public override fun clone(): ElementInstance {
        try {
            val clone = super.clone() as ElementInstance
            clone.dirty = this.dirty
            clone.amount = this.amount
            if (this.tag != null) clone.tag = this.tag!!.copy()
            return clone
        } catch (e: CloneNotSupportedException) {
            throw AssertionError()
        }
    }

    companion object {
        /** Ключ для ідентифікатора елемента в NBT даних  */
        const val ELEMENT_KEY: String = "element"

        /** Ключ для кількості елемента в NBT даних  */
        const val AMOUNT_KEY: String = "amount"

        /**
         * Може бути використаний для завантаження [ElementInstance] з [CompoundTag].
         *
         *
         * [CompoundTag] повинен бути створений через [ElementInstance.toNBT]
         *
         *
         * @param tag NBT тег, що містить дані елемента
         * @return Новий екземпляр елемента, завантажений з NBT
         */
        @JvmStatic
        fun fromNBT(tag: CompoundTag): ElementInstance {
            val location = ResourceLocation.tryParse(tag.getString(ELEMENT_KEY))
            if (location!!.namespace != "minecraft") {
                val element = ElementAPI.elementRegistry.get(location)
                val instance = element!!.createDefaultInstance()
                instance.deserialize(tag)
                return instance
            } else {
                val element: Element = object : Element(ElementType.NEUTRAL) {

                }
                return element.createDefaultInstance()
            }
        }
    }
}