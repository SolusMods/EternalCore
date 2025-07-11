package io.github.solusmods.eternalcore.config;

import lombok.Builder;

import java.util.function.Supplier;

@Builder
public class SpiritualRootEntry {

    Supplier<Float> experiencePerLevel;
    Supplier<Integer> maxLevel;
    Supplier<Double> absorptionBonus;

    public Float getExperiencePerLevel() {
        return experiencePerLevel.get();
    }

    public Integer getMaxLevel() {
        return maxLevel.get();
    }

    public Double getAbsorptionBonus() {return absorptionBonus.get();}
}
