package io.github.solusmods.eternalcore.config;

import java.util.function.Supplier;

public record StageEntry(
                         Supplier<Double> minQi,
                         Supplier<Double> maxQi,
                         Supplier<Double> coefficient,
                         Supplier<Boolean> canBreakthrough) {

    public double getMinQi() {
        return minQi.get();
    }

    public double getMaxQi() {
        return maxQi.get();
    }

    public double getCoefficient() {
        return coefficient.get();
    }

    public boolean getCanFly() {
        return canBreakthrough.get();
    }
}
