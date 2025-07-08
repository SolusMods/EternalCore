package io.github.solusmods.eternalcore.api.qi_energy;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Абстрактний базовий клас для всіх елементів у модифікації EternalCore.
 * <p>
 * Елементи є основними компонентами системи, які можуть бути додані до живих сутностей.
 * Кожен елемент має свій тип і може мати протилежний елемент. Елементи реєструються
 * у реєстрі ElementAPI і мають унікальні ідентифікатори.
 */
@ToString
public abstract class AbstractQiEnergy {

    @Getter
    @Setter
    protected double amount;


    protected AbstractQiEnergy(double amount) {
        this.amount = amount;
    }

    public abstract ElementType getElement();

    public void add(double amount) {
        this.amount += amount;
    }

    public void subtract(double amount) {
        this.amount = Math.max(0, this.amount - amount);
    }
}
