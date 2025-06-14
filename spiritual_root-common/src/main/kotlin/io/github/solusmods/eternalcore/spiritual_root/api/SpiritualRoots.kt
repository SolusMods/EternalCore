package io.github.solusmods.eternalcore.spiritual_root.api

import io.github.solusmods.eternalcore.spiritual_root.impl.SpiritualRootStorage
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import java.util.function.BiConsumer

/**
 * Інтерфейс для управління духовними коренями (spiritual roots) сутності.
 *
 *
 * Духовні корені є основним елементом системи культивації в Eternal Core.
 * Вони визначають здатність сутності до культивації та спеціальні здібності.
 * Цей інтерфейс надає методи для додавання, отримання та генерації духовних коренів.
 *
 */
interface SpiritualRoots {
    /**
     * Позначає стан духовних коренів як змінений.
     *
     *
     * Це використовується для синхронізації даних між сервером і клієнтом.
     *
     */
    fun markDirty()

    fun sync()

    val gainedRoots: MutableCollection<SpiritualRootInstance>

    /**
     * Отримує колекцію всіх духовних коренів, якими володіє сутність.
     *
     * @return Колекція екземплярів духовних коренів
     */
    val spiritualRoots: MutableMap<ResourceLocation, SpiritualRootInstance>

    /**
     * Додає духовний корінь до сутності за ідентифікатором.
     *
     * @param spiritualRootId Ідентифікатор духовного кореня
     * @param notify Чи слід сповіщати про додавання
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */

    fun addSpiritualRoot(spiritualRootId: ResourceLocation, notify: Boolean, component: MutableComponent? = null): Boolean {
        val spiritualRoot = SpiritualRootAPI.spiritualRootRegistry.get(spiritualRootId) ?: return false
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(), false, notify, component)
    }

    /**
     * Додає духовний корінь до сутності за об'єктом духовного кореня.
     *
     * @param spiritualRoot Об'єкт духовного кореня
     * @param notify Чи слід сповіщати про додавання
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */
    fun addSpiritualRoot(spiritualRoot: SpiritualRoot, notify: Boolean, component: MutableComponent? = null): Boolean {
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(), false, notify, component)
    }

    /**
     * Додає екземпляр духовного кореня до сутності.
     *
     * @param spiritualRootInstance Екземпляр духовного кореня
     * @param advance Чи є це просуванням (advancement)
     * @param notify Чи слід сповіщати про додавання
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */
    fun addSpiritualRoot(spiritualRootInstance: SpiritualRootInstance, advance: Boolean, notify: Boolean): Boolean {
        return addSpiritualRoot(spiritualRootInstance, advance, notify, null)
    }

    /**
     * Додає екземпляр духовного кореня до сутності з повними параметрами.
     *
     *
     * Це базовий метод, який викликається всіма іншими перевантаженими методами addSpiritualRoot.
     *
     *
     * @param instance Екземпляр духовного кореня
     * @param advance Чи є це просуванням (advancement)
     * @param notify Чи слід сповіщати про додавання
     * @param component Компонент повідомлення для відображення (може бути null)
     * @return true, якщо духовний корінь був успішно доданий, false - в іншому випадку
     */
    fun addSpiritualRoot(
        instance: SpiritualRootInstance,
        advance: Boolean,
        notify: Boolean,
        component: MutableComponent?
    ): Boolean


    /**
     * Updates a element instance and optionally synchronizes the change across the network.
     *
     *
     * @param updatedInstance The instance to update
     * @param sync If true, synchronizes the change to all clients/server
     */
    fun updateRoot(updatedInstance: SpiritualRootInstance, sync: Boolean)

    fun forEachRoot(biConsumer: BiConsumer<SpiritualRootStorage, SpiritualRootInstance>)

    fun forgetRoot(spiritualRootId: ResourceLocation, component: MutableComponent?)

    fun forgetRoot(spiritualRootId: ResourceLocation) {
        forgetRoot(spiritualRootId, null)
    }

    fun forgetRoot(spiritualRoot: SpiritualRoot, component: MutableComponent?) {
        forgetRoot(spiritualRoot.registryName!!, component)
    }

    fun forgetRoot(spiritualRoot: SpiritualRoot) {
        forgetRoot(spiritualRoot.registryName!!)
    }

    fun forgetRoot(spiritualRootInstance: SpiritualRootInstance, component: MutableComponent?) {
        forgetRoot(spiritualRootInstance.spiritualRootId, component)
    }

    fun forgetRoot(spiritualRootInstance: SpiritualRootInstance) {
        forgetRoot(spiritualRootInstance.spiritualRootId)
    }


    fun advanceSpiritualRoot(spiritualRootId: ResourceLocation, component: MutableComponent? = null): Boolean {
        val spiritualRoot = SpiritualRootAPI.spiritualRootRegistry.get(spiritualRootId) ?: return false
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(), advance = true, notify = false)
    }


    fun advanceSpiritualRoot(spiritualRoot: SpiritualRoot, component: MutableComponent? = null): Boolean {
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(),
            advance = true,
            notify = false,
            component = component
        )
    }


    fun advanceSpiritualRoot(advance: SpiritualRootInstance, component: MutableComponent? = null): Boolean {
        return addSpiritualRoot(advance, advance = true, notify = false, component = component)
    }
}