package io.github.solusmods.eternalcore.spiritual_root.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

@RequiredArgsConstructor
@Getter
public enum RootLevels {
    O(0, 0, 0.0F),
    I(1, 100, 0.2F),
    II(2, 200, 0.3F),
    III(3, 300, 0.4F),
    IV(4, 1000, 0.5F),
    V(5, 2000, 1F),
    VI(6, 5000, 1F),
    VII(7, 10000, 1F),
    VIII(8, 20000, 2F),
    IX(9, 50000, 2F),
    X(10, 10000, 2F);
    // Gets Id -> Enum
    public static final IntFunction<RootLevels> BY_ID =
            ByIdMap.continuous(
                    RootLevels::getLevel,
                    RootLevels.values(),
                    ByIdMap.OutOfBoundsStrategy.ZERO
            );
    private final int level;
    private final int experience;
    private final float absorptionEfficiency;

    public static RootLevels byId(int level) {
        return RootLevels.BY_ID.apply(level);
    }
}
