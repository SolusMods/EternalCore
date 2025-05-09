package io.github.solusmods.eternalcore.element.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Абстрактний базовий клас для всіх елементів у модифікації EternalCore.
 * <p>
 * Елементи є основними компонентами системи, які можуть бути додані до живих сутностей.
 * Кожен елемент має свій тип і може мати протилежний елемент. Елементи реєструються
 * у реєстрі {@link ElementAPI} і мають унікальні ідентифікатори.
 * </p>
 * <p>
 * Цей клас надає базові функціональні можливості для всіх елементів, включаючи
 * отримання імені, створення екземплярів, обробку додавання до сутностей та інше.
 * </p>
 */
@Getter
@AllArgsConstructor
public abstract class Element {
    /** Тип елемента */
    private final ElementType type;

    /**
     * Створює стандартний екземпляр цього елемента.
     *
     * @return Новий екземпляр елемента
     */
    public ElementInstance createDefaultInstance() {
        return new ElementInstance(this);
    }


    /**
     * Використовується для отримання {@link ResourceLocation} ідентифікатора цього {@link Element}.
     *
     * @return Ідентифікатор елемента або null, якщо елемент не зареєстрований
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return ElementAPI.getElementRegistry().getId(this);
    }

    /**
     * Використовується для отримання {@link MutableComponent} імені цього {@link Element} для перекладу.
     * <p>
     * Створює компонент для перекладу на основі ідентифікатора елемента. Формат ключа перекладу:
     * "{namespace}.element.{path}", де path може містити підшляхи, розділені крапками.
     * </p>
     *
     * @return Компонент імені елемента для перекладу або null, якщо елемент не зареєстрований
     */
    @Nullable
    public MutableComponent getName() {
        ResourceLocation id = this.getRegistryName();
        return id == null ? null : Component.translatable(String.format("%s.element.%s", id.getNamespace(), id.getPath().replace('/', '.')));
    }

    /**
     * Викликається, коли елемент додається до сутності.
     * <p>
     * Цей метод дозволяє виконувати спеціальні дії при додаванні елемента до живої сутності.
     * Метод може бути перевизначений у підкласах для реалізації специфічної поведінки.
     * </p>
     *
     * @param instance Екземпляр елемента, який додається
     * @param entity Сутність, до якої додається елемент
     */
    public void onAdd(ElementInstance instance, LivingEntity entity) {}

    /**
     * Отримує протилежний елемент для даного екземпляра.
     * <p>
     * Деякі елементи можуть мати протилежні елементи, які можуть взаємодіяти або скасовувати один одного.
     * Цей метод дозволяє визначити такий протилежний елемент для конкретного екземпляра та сутності.
     * </p>
     *
     * @param instance Екземпляр елемента, для якого шукається протилежний
     * @param entity Сутність, до якої застосовано елемент
     * @return Протилежний елемент або null, якщо протилежного елемента немає
     */
    public @Nullable Element getOpposite(ElementInstance instance, LivingEntity entity){return null;}
}