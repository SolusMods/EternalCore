package io.github.solusmods.eternalcore.config;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@NoArgsConstructor
public class RealmConfig {
    public String type;
    public double baseHealth;
    public double minQi;
    public double maxQi;
    public double baseAttackDamage;
    public double baseAttackSpeed;
    public double knockBackResistance;
    public double jumpHeight;
    public double movementSpeed;
    public double sprintSpeed;
    public double coefficient;
    public boolean canFly = false;

    public RealmConfig(Consumer<RealmConfig> initialize) throws RuntimeException {
        initialize.accept(this);
        build();
    }

    public RealmConfig type(String type) {
        this.type = type;
        return this;
    }

    public RealmConfig baseHealth(double baseHealth) {
        this.baseHealth = baseHealth;
        return this;
    }

    public RealmConfig minQi(double minQi) {
        this.minQi = minQi;
        return this;
    }

    public RealmConfig maxQi(double maxQi) {
        this.maxQi = maxQi;
        return this;
    }

    public RealmConfig baseAttackDamage(double baseAttackDamage) {
        this.baseAttackDamage = baseAttackDamage;
        return this;
    }

    public RealmConfig baseAttackSpeed(double baseAttackSpeed) {
        this.baseAttackSpeed = baseAttackSpeed;
        return this;
    }

    public RealmConfig knockBackResistance(double knockBackResistance) {
        this.knockBackResistance = knockBackResistance;
        return this;
    }

    public RealmConfig jumpHeight(double jumpHeight) {
        this.jumpHeight = jumpHeight;
        return this;
    }

    public RealmConfig movementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
        return this;
    }

    public RealmConfig sprintSpeed(double sprintSpeed) {
        this.sprintSpeed = sprintSpeed;
        return this;
    }

    public RealmConfig coefficient(double coefficient) {
        this.coefficient = coefficient;
        return this;
    }

    public RealmConfig canFly(boolean canFly) {
        this.canFly = canFly;
        return this;
    }

    public RealmConfig build() throws RuntimeException {
        List<String> errors = new ArrayList<>();

        if (baseHealth <= 0.0F) {
            errors.add("baseHealth must be greater than 0 (current: " + baseHealth + ")");
        }
        if (minQi <= 0.0F) {
            errors.add("minQi must be greater than 0 (current: " + minQi + ")");
        }
        if (maxQi <= minQi) {
            errors.add("maxQi must be greater than minQi (current minQi: " + minQi + ", maxQi: " + maxQi + ")");
        }
        if (baseAttackDamage <= 0.0F) {
            errors.add("baseAttackDamage must be greater than 0 (current: " + baseAttackDamage + ")");
        }
        if (baseAttackSpeed <= 0.0F) {
            errors.add("baseAttackSpeed must be greater than 0 (current: " + baseAttackSpeed + ")");
        }
        if (knockBackResistance <= 0.0F) {
            errors.add("knockBackResistance must be between 0.0 and 1.0 exclusive (current: " + knockBackResistance + ")");
        }
        if (jumpHeight <= 0.0F) {
            errors.add("jumpHeight must be greater than 0 (current: " + jumpHeight + ")");
        }
        if (movementSpeed <= 0.0F) {
            errors.add("movementSpeed must be greater than 0 (current: " + movementSpeed + ")");
        }
        if (sprintSpeed <= 0.0F) {
            errors.add("sprintSpeed must be greater than 0 (current: " + sprintSpeed + ")");
        }
        if (coefficient <= 0.0F) {
            errors.add("coefficient must be greater than 0 (current: " + coefficient + ")");
        }

        if (!validate()) {
            throw new RuntimeException("Invalid RealmConfig:\n" + String.join("\n", errors));
        }

        return this;
    }

    private boolean validate() {
        return baseHealth > 0.0F
                && minQi > 0.0F
                && maxQi > minQi
                && baseAttackDamage > 0.0F
                && baseAttackSpeed > 0.0F
                && knockBackResistance > 0.0F
                && jumpHeight > 0.0F
                && movementSpeed > 0.0F
                && sprintSpeed > 0.0F
                && coefficient > 0.0F;
    }

}
