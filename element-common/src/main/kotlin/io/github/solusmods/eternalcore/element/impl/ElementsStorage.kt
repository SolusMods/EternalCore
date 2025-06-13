package io.github.solusmods.eternalcore.element.impl

import io.github.solusmods.eternalcore.element.EternalCoreElements
import io.github.solusmods.eternalcore.element.api.ElementEvents
import io.github.solusmods.eternalcore.element.api.ElementInstance
import io.github.solusmods.eternalcore.element.api.Elements
import io.github.solusmods.eternalcore.element.impl.network.InternalStorageActions
import io.github.solusmods.eternalcore.network.api.util.Changeable
import io.github.solusmods.eternalcore.network.api.util.StorageType
import io.github.solusmods.eternalcore.storage.EternalCoreStorage
import io.github.solusmods.eternalcore.storage.api.Storage
import io.github.solusmods.eternalcore.storage.api.StorageEvents
import io.github.solusmods.eternalcore.storage.api.StorageHolder
import io.github.solusmods.eternalcore.storage.api.StorageKey
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity

/**
 * Сховище для управління елементами гравця в системі EternalCore.
 *
 * Цей клас відповідає за зберігання, управління та синхронізацію елементів,
 * які гравець здобуває протягом гри. Елементи є важливою частиною системи культивації
 * та визначають спеціальні здібності та атрибути гравця.
 *
 * ElementsStorage реалізує інтерфейс [Elements] та розширює базовий клас [Storage],
 * успадковуючи функціональність збереження та завантаження даних у форматі NBT.
 */
class ElementsStorage(holder: StorageHolder?) : Storage(holder), Elements {

    /** Колекція елементів, якими володіє гравець */
    override val elements: MutableMap<ResourceLocation, ElementInstance> = mutableMapOf()

    override val obtainedElements: MutableCollection<ElementInstance>
        get() = elements.values

    private var hasRemovedElements = false

    /** Отримує власника сховища як живу сутність */
    private val owner: LivingEntity?
        get() = holder as? LivingEntity

    /**
     * Зберігає стан сховища в NBT формат.
     *
     * Зберігає домінуючий елемент та колекцію всіх елементів гравця.
     *
     * @param data Тег, в який буде збережено дані
     */
    override fun save(data: CompoundTag) {
        val elementsTag = ListTag().apply {
            elements.values.forEach { instance ->
                add(instance.toNBT())
                instance.resetDirty()
            }
        }
        data.put(ELEMENTS_KEY, elementsTag)
    }

    /**
     * Завантажує стан сховища з NBT формату.
     *
     * Відновлює колекцію елементів гравця.
     *
     * @param data Тег, з якого будуть завантажені дані
     */
    override fun load(data: CompoundTag) {
        if (data.contains("resetExistingData")) {
            elements.clear()
        }

        data.getList(ELEMENTS_KEY, Tag.TAG_COMPOUND.toInt()).forEach { tag ->
            runCatching {
                val instance = ElementInstance.fromNBT(tag as CompoundTag)
                elements[instance.elementId] = instance
            }.onFailure { exception ->
                EternalCoreStorage.LOG.error("Failed to load element instance from NBT", exception)
            }
        }
    }

    /**
     * Updates a element instance and optionally synchronizes the change across the network.
     *
     * @param updatedInstance The instance to update
     * @param sync            If true, synchronizes the change to all clients/server
     */
    override fun updateElement(updatedInstance: ElementInstance, sync: Boolean) {
        updatedInstance.markDirty()
        elements[updatedInstance.elementId] = updatedInstance
        if (sync) markDirty()
    }

    override fun forEachElement(action: ((ElementsStorage, ElementInstance) -> Unit)) {
        // Create a copy to avoid concurrent modification
        elements.values.toList().forEach { elementInstance ->
            action(this, elementInstance)
        }
        markDirty()
    }

    override fun forgetElement(elementId: ResourceLocation, component: MutableComponent?) {
        val instance = elements[elementId] ?: return

        val forgetMessage = Changeable.of(component)
        val result = ElementEvents.FORGET_ELEMENT.invoker()?.forget(
            instance,
            owner,
            forgetMessage
        )

        if (result?.isFalse == true) return

        forgetMessage.get()?.let { message ->
            owner?.sendSystemMessage(message)
        }

        instance.markDirty()
        elements.remove(elementId)
        hasRemovedElements = true
        markDirty()
    }

    /**
     * Додає новий елемент до колекції гравця.
     *
     * Цей метод викликає подію [ElementEvents.ADD_ELEMENT], яка може бути скасована
     * обробниками подій. Якщо елемент успішно додано, він позначається як змінений
     * і додається до колекції елементів.
     *
     * @param instance Екземпляр елемента для додавання
     * @param breakthrough Чи є це проривом (breakthrough)
     * @param teleportToSpawn Чи повідомляти гравця про додавання
     * @param component Компонент повідомлення (може бути null)
     * @return true, якщо елемент було успішно додано, false в іншому випадку
     */
    override fun addElement(
        instance: ElementInstance,
        breakthrough: Boolean,
        teleportToSpawn: Boolean,
        component: MutableComponent?
    ): Boolean {
        if (elements.containsKey(instance.elementId)) {
            EternalCoreStorage.LOG.debug("Tried to register a duplicate of {}.", instance.elementId)
            return false
        }

        val addMessage = Changeable.of(component)
        val notify = Changeable.of(teleportToSpawn)
        val result = ElementEvents.ADD_ELEMENT.invoker()?.add(
            instance,
            owner,
            breakthrough,
            notify,
            addMessage
        )

        if (result?.isFalse == true) return false

        instance.markDirty()
        elements[instance.elementId] = instance
        markDirty()

        addMessage.get()?.let { message ->
            owner?.sendSystemMessage(message)
        }

        return true
    }

    override fun saveOutdated(data: CompoundTag) {
        if (hasRemovedElements) {
            hasRemovedElements = false
            data.putBoolean("resetExistingData", true)
            super.saveOutdated(data)
        } else {
            val elementsTag = ListTag().apply {
                elements.values.filter { it.dirty }.forEach { instance ->
                    add(instance.toNBT())
                    instance.resetDirty()
                }
            }
            data.put(ELEMENTS_KEY, elementsTag)
        }
    }

    override fun sync() {
        val data = CompoundTag()
        saveOutdated(data)
        InternalStorageActions.sendSyncStoragePayload(StorageType.ELEMENTS, data)
    }

    companion object {
        /** Ключ для зберігання колекції елементів у NBT */
        const val ELEMENTS_KEY = "elements_key"

        /** Унікальний ідентифікатор цього типу сховища */
        val ID: ResourceLocation = EternalCoreElements.create("elements_storage")

        /** Ключ для доступу до цього сховища */
        var key: StorageKey<ElementsStorage>? = null

        /**
         * Ініціалізує систему сховища елементів, реєструючи його в системі сховищ EternalCore.
         *
         * Цей метод повинен бути викликаний один раз під час ініціалізації мода.
         */
        @JvmStatic
        fun init() {
            StorageEvents.REGISTER_ENTITY_STORAGE.register { registry ->
                key = registry.register(
                    ID,
                    ElementsStorage::class.java,
                    { obj -> LivingEntity::class.java.isInstance(obj) },
                    { holder: Entity -> ElementsStorage(holder) }
                )
            }
        }
    }
}