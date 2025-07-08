package io.github.solusmods.eternalcore.config;


import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@NoArgsConstructor
public class StageConfig {
    public String type;
    public float minQi;
    public float maxQi;
    public float coefficient;
    public boolean canBreakthrough = true;

    public StageConfig(Consumer<StageConfig> initialize) throws RuntimeException {
        initialize.accept(this);
        build();
    }

    public StageConfig type(String type) {
        this.type = type;
        return this;
    }

    public StageConfig minQi(float minQi) {
        this.minQi = minQi;
        return this;
    }

    public StageConfig maxQi(float maxQi) {
        this.maxQi = maxQi;
        return this;
    }

    public StageConfig coefficient(float coefficient) {
        this.coefficient = coefficient;
        return this;
    }

    public StageConfig canBreakthrough(boolean canBreakthrough) {
        this.canBreakthrough = canBreakthrough;
        return this;
    }

    public StageConfig build() throws RuntimeException {
        List<String> errors = new ArrayList<>();

        if (minQi <= 0) {
            errors.add("minQi must be greater than 0 (current: " + minQi + ")");
        }
        if (maxQi <= minQi) {
            errors.add("maxQi must be greater than minQi (current minQi: " + minQi + ", maxQi: " + maxQi + ")");
        }
        if (coefficient <= 0) {
            errors.add("coefficient must be greater than 0 (current: " + coefficient + ")");
        }

        if (!validate()) {
            throw new RuntimeException("Invalid StageConfig:\n" + String.join("\n", errors));
        }

        return this;
    }

    private boolean validate() {
        return minQi > 0 && maxQi > minQi;
    }
}
