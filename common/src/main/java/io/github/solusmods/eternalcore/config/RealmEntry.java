package io.github.solusmods.eternalcore.config;

import java.util.function.Supplier;

public record RealmEntry(Supplier<Double> baseHealth,
                         Supplier<Double> minQi,
                         Supplier<Double> maxQi,
                         Supplier<Double> baseAttackDamage,
                         Supplier<Double> baseAttackSpeed,
                         Supplier<Double> knockBackResistance,
                         Supplier<Double> jumpHeight,
                         Supplier<Double> movementSpeed,
                         Supplier<Double> sprintSpeed,
                         Supplier<Double> coefficient,
                         Supplier<Boolean> canFly) {


    public double getBaseHealth() {
        return baseHealth.get();
    }

    public double getMinQi() {
        return minQi.get();
    }

    public double getMaxQi() {
        return maxQi.get();
    }

    public double getBaseAttackDamage() {
        return baseAttackDamage.get();
    }

    public double getBaseAttackSpeed() {
        return baseAttackSpeed.get();
    }

    public double getKnockBackResistance() {
        return knockBackResistance.get();
    }

    public double getJumpHeight() {
        return jumpHeight.get();
    }

    public double getMovementSpeed() {
        return movementSpeed.get();
    }

    public double getSprintSpeed() {
        return sprintSpeed.get();
    }

    public double getCoefficient() {
        return coefficient.get();
    }

    public boolean getCanFly() {
        return canFly.get();
    }
}
