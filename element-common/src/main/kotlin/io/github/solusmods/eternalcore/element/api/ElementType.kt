package io.github.solusmods.eternalcore.element.api

import com.mojang.serialization.Codec
import lombok.Getter
import lombok.RequiredArgsConstructor
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import java.util.function.Function
import java.util.function.IntFunction
import java.util.function.ToIntFunction

@Getter
@RequiredArgsConstructor
enum class ElementType(val id: Int, val elementName: String, val color: Int) : StringRepresentable {
    NEUTRAL(0, "neutral", 0xCCCCCC),
    FIRE(1, "fire", 0xFF4500),
    WOOD(2, "wood", 0x228B22),
    WATER(3, "water", 0x1E90FF),
    EARTH(4, "earth", 0x8B4513),
    METAL(5, "metal", 0xC0C0C0),
    AIR(6, "air", 0xF0FFFF),
    THUNDER(7, "thunder", 0xFFD700),
    YIN(8, "yin", 0x483D8B),
    DARKNESS(9, "darkness", 0x483D8B);

    override fun getSerializedName(): String {
        return this.elementName
    }

    companion object {
        val BY_ID: IntFunction<ElementType?> = ByIdMap.continuous<ElementType?>(
            ToIntFunction { obj: ElementType? -> obj!!.id },
            ElementType.entries.toTypedArray(),
            ByIdMap.OutOfBoundsStrategy.ZERO
        )

        // Codec за назвою (String репрезентація)
        val CODEC: Codec<ElementType> =
            StringRepresentable.fromEnum { ElementType.entries.toTypedArray() }

        // Також можна додати Codec по ID
        val CODEC_BY_ID: Codec<ElementType?>? = Codec.INT.xmap<ElementType?>(Function { id: Int? ->
            ElementType.Companion.byId(
                id!!
            )
        }, Function { obj: ElementType? -> obj!!.id })

        fun byId(id: Int): ElementType? {
            return BY_ID.apply(id)
        }
    }
}