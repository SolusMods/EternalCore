package io.github.solusmods.eternalcore.element.api

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity

/**
 * Абстрактний базовий клас для всіх елементів у модифікації EternalCore.
 *
 * Елементи є основними компонентами системи, які можуть бути додані до живих сутностей.
 * Кожен елемент має свій тип і може мати протилежний елемент. Елементи реєструються
 * у реєстрі [ElementAPI] і мають унікальні ідентифікатори.
 *
 * Цей клас надає базові функціональні можливості для всіх елементів, включаючи
 * отримання імені, створення екземплярів, обробку додавання до сутностей та інше.
 */
abstract class Element(
    /** Тип елемента */
    val type: ElementType
) {

    /**
     * Створює стандартний екземпляр цього елемента.
     *
     * @return Новий екземпляр елемента
     */
    fun createDefaultInstance(): ElementInstance = ElementInstance(this)

    /**
     * Використовується для отримання [ResourceLocation] ідентифікатора цього [Element].
     *
     * @return Ідентифікатор елемента або null, якщо елемент не зареєстрований
     */
    val registryName: ResourceLocation?
        get() = ElementAPI.elementRegistry.getId(this)

    /**
     * Використовується для отримання [MutableComponent] імені цього [Element] для перекладу.
     *
     * Створює компонент для перекладу на основі ідентифікатора елемента. Формат ключа перекладу:
     * "{namespace}.element.{path}", де path може містити підшляхи, розділені крапками.
     *
     * @return Компонент імені елемента для перекладу або null, якщо елемент не зареєстрований
     */
    val name: MutableComponent?
        get() = registryName?.let { id ->
            Component.translatable("${id.namespace}.element.${id.path.replace('/', '.')}")
        }

    /**
     * Отримує ключ перекладу для назви цього Елементу.
     *
     * @return Ключ перекладу або null, якщо ім'я недоступне
     */
    val nameTranslationKey: String?
        get() = name?.let { component ->
            (component.contents as? TranslatableContents)?.key
        }

    /**
     * Викликається, коли елемент додається до сутності.
     *
     * Цей метод дозволяє виконувати спеціальні дії при додаванні елемента до живої сутності.
     * Метод може бути перевизначений у підкласах для реалізації специфічної поведінки.
     *
     * @param instance Екземпляр елемента, який додається
     * @param entity Сутність, до якої додається елемент
     */
    open fun onAdd(instance: ElementInstance, entity: LivingEntity) {}

    /**
     * Отримує протилежний елемент для даного екземпляра.
     *
     * Деякі елементи можуть мати протилежні елементи, які можуть взаємодіяти або скасовувати один одного.
     * Цей метод дозволяє визначити такий протилежний елемент для конкретного екземпляра та сутності.
     *
     * @param instance Екземпляр елемента, для якого шукається протилежний
     * @param entity Сутність, до якої застосовано елемент
     * @return Протилежний елемент або null, якщо протилежного елемента немає
     */
    open fun getOpposite(instance: ElementInstance, entity: LivingEntity): Element? = null
}