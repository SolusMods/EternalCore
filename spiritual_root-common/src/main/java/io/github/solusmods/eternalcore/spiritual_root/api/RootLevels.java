package io.github.solusmods.eternalcore.spiritual_root.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

/**
 * Рівні розвитку Духовного Кореня.
 * <p>
 * Кожен рівень має унікальний номер, вимоги з досвіду та ефективність поглинання Ци.
 */
@RequiredArgsConstructor
@Getter
public enum RootLevels {
    O(0, 0, 0.0F),
    I(1, 100, 0.2F),
    II(2, 200, 0.3F),
    III(3, 300, 0.4F),
    IV(4, 1000, 0.5F),
    V(5, 2000, 1.0F),
    VI(6, 5000, 1.0F),
    VII(7, 10000, 1.0F),
    VIII(8, 20000, 2.0F),
    IX(9, 50000, 2.0F),
    X(10, 100000, 2.0F);

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

    /**
     * Отримує наступний рівень, або null, якщо поточний — максимальний.
     */
    public RootLevels getNext() {
        int index = this.ordinal();
        return index < values().length - 1 ? values()[index + 1] : null;
    }

    /**
     * Отримує попередній рівень, або null, якщо поточний — мінімальний.
     */
    public RootLevels getPrevious() {
        int index = this.ordinal();
        return index > 0 ? values()[index - 1] : null;
    }

    /**
     * Визначає рівень за кількістю досвіду.
     *
     * @param experience Поточний досвід
     * @return Відповідний рівень
     */
    public static RootLevels getLevelForExperience(int experience) {
        RootLevels result = O;
        for (RootLevels level : values()) {
            if (experience >= level.experience) {
                result = level;
            } else {
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "RootLevel{" +
                "level=" + level +
                ", experience=" + experience +
                ", absorptionEfficiency=" + absorptionEfficiency +
                '}';
    }
}
