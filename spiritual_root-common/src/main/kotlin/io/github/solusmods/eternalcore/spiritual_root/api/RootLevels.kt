package io.github.solusmods.eternalcore.spiritual_root.api

import lombok.Getter
import lombok.RequiredArgsConstructor
import net.minecraft.util.ByIdMap
import java.util.function.IntFunction

/**
 * Рівні розвитку Духовного Кореня.
 *
 *
 * Кожен рівень має унікальний номер, вимоги з досвіду та ефективність поглинання Ци.
 */
@RequiredArgsConstructor
@Getter
enum class RootLevels(val level: Int, val experience: Int, val absorptionEfficiency: Float) {
    O(0, 0, 0.0f),
    I(1, 100, 0.2f),
    II(2, 200, 0.3f),
    III(3, 300, 0.4f),
    IV(4, 1000, 0.5f),
    V(5, 2000, 1.0f),
    VI(6, 5000, 1.0f),
    VII(7, 10000, 1.0f),
    VIII(8, 20000, 2.0f),
    IX(9, 50000, 2.0f),
    X(10, 100000, 2.0f);

    val next: RootLevels?
        /**
         * Отримує наступний рівень, або null, якщо поточний — максимальний.
         */
        get() {
            val index = this.ordinal
            return if (index < entries.toTypedArray().size - 1) entries[index + 1] else null
        }

    val previous: RootLevels?
        /**
         * Отримує попередній рівень, або null, якщо поточний — мінімальний.
         */
        get() {
            val index = this.ordinal
            return if (index > 0) entries[index - 1] else null
        }

    override fun toString(): String {
        return "RootLevel{" +
                "level=" + level +
                ", experience=" + experience +
                ", absorptionEfficiency=" + absorptionEfficiency +
                '}'
    }

    companion object {
        val BY_ID: IntFunction<RootLevels?> = ByIdMap.continuous<RootLevels?>(
            { obj: RootLevels? -> obj!!.level },
            entries.toTypedArray(),
            ByIdMap.OutOfBoundsStrategy.ZERO
        )

        fun byId(level: Int): RootLevels? {
            return BY_ID.apply(level)
        }

        /**
         * Визначає рівень за кількістю досвіду.
         *
         * @param experience Поточний досвід
         * @return Відповідний рівень
         */
        fun getLevelForExperience(experience: Int): RootLevels {
            var result = RootLevels.O
            for (level in entries) {
                if (experience >= level.experience) {
                    result = level
                } else {
                    break
                }
            }
            return result
        }
    }
}
