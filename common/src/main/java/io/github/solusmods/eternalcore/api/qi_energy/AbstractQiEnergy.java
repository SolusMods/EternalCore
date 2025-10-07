package io.github.solusmods.eternalcore.api.qi_energy;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Базова модель енергії Ці.
 * <p>
 * Реалізації обов'язково реєструються у {@link QiEnergyAPI#getElementRegistry()} перед використанням.
 * Кожна енергія повинна надавати стабільний ідентифікатор через {@link #getElement()} та підтримувати
 * серіалізацію у відповідному класі-реалізації (див. {@link ElementalQiEnergy}). Значення {@link #amount}
 * синхронізується через сховище {@link io.github.solusmods.eternalcore.impl.qi_energy.QiEnergyStorage}.
 * </p>
 */
@ToString
public abstract class AbstractQiEnergy {

    @Getter
    @Setter
    /**
     * Поточна кількість енергії Ці.
     */
    protected double amount;


    /**
     * Створює енергію із заданою кількістю.
     *
     * @param amount Початковий обсяг
     */
    protected AbstractQiEnergy(double amount) {
        this.amount = amount;
    }

    /**
     * Повертає тип енергії, зареєстрований у реєстрі.
     *
     * @return {@link ElementType} енергії
     */
    public abstract ElementType getElement();

    /**
     * Збільшує кількість енергії.
     *
     * @param amount Кількість для додавання (може бути від'ємною для корекції)
     */
    public void add(double amount) {
        this.amount += amount;
    }

    /**
     * Зменшує кількість енергії, не дозволяючи значенню стати від'ємним.
     *
     * @param amount Кількість для віднімання
     */
    public void subtract(double amount) {
        this.amount = Math.max(0, this.amount - amount);
    }
}
