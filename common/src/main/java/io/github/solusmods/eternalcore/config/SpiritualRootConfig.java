package io.github.solusmods.eternalcore.config;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Конфігурація для духовного кореня.
 * Містить базові параметри: досвід для підвищення рівня, рідкість, максимальний рівень.
 */
@NoArgsConstructor
public class SpiritualRootConfig {

    public float experiencePerLevel = 100.0f;
    public float rarity = 0.5f;
    public int maxLevel = 10;
    public double absorptionBonus = 0.0;

    public SpiritualRootConfig(Consumer<SpiritualRootConfig> initialize) throws RuntimeException {
        initialize.accept(this);
        build();
    }

    public SpiritualRootConfig experiencePerLevel(float experiencePerLevel) {
        this.experiencePerLevel = experiencePerLevel;
        return this;
    }

    public SpiritualRootConfig rarity(float rarity) {
        this.rarity = rarity;
        return this;
    }

    public SpiritualRootConfig maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public SpiritualRootConfig absorptionBonus(double absorptionBonus) {
        this.absorptionBonus = absorptionBonus;
        return this;
    }

    public SpiritualRootConfig build() throws RuntimeException {
        List<String> errors = new ArrayList<>();

        if (experiencePerLevel <= 0) {
            errors.add("experiencePerLevel must be greater than 0 (current: " + experiencePerLevel + ")");
        }
        if (rarity <= 0.0f || rarity >= 1.0f) {
            errors.add("rarity must be between 0.0 and 1.0 exclusive (current: " + rarity + ")");
        }
        if (maxLevel <= 0) {
            errors.add("maxLevel must be greater than 0 (current: " + maxLevel + ")");
        }
        if (absorptionBonus > 0.0 && absorptionBonus < 0.3) {
            errors.add("absorptionBonus must be between 0.0 and 0.3 exclusive (current: " + absorptionBonus + ")");
        }

        if (!validate()) {
            throw new RuntimeException("Invalid SpiritualRootConfig: \t" + String.join("\t", errors));
        }

        return this;
    }

    private boolean validate() {
        return experiencePerLevel > 0.0f
                && rarity > 0.0f
                && maxLevel > 0 && absorptionBonus > 0.0 && absorptionBonus < 0.3;
    }
}
