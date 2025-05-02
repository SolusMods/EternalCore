package io.github.solusmods.eternalcore.element.api;

import com.mojang.serialization.Codec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

@Getter
@RequiredArgsConstructor
public enum ElementType implements StringRepresentable {
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

    public static final IntFunction<ElementType> BY_ID = ByIdMap.continuous(
            ElementType::getId,
            ElementType.values(),
            ByIdMap.OutOfBoundsStrategy.ZERO
    );
    // Codec за назвою (String репрезентація)
    public static final Codec<ElementType> CODEC = StringRepresentable.fromEnum(ElementType::values);
    // Також можна додати Codec по ID
    public static final Codec<ElementType> CODEC_BY_ID = Codec.INT.xmap(ElementType::byId, ElementType::getId);
    private final Integer id;
    private final String name;
    private final int color;

    public static ElementType byId(int id) {
        return BY_ID.apply(id);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    // Отримати протилежний тип Ци
    public ElementType getOpposite() {
        switch (this) {
            case FIRE:
                return WATER;
            case WOOD:
                return METAL;
            case WATER:
                return FIRE;
            case EARTH:
                return AIR;
            case METAL:
                return WOOD;
            case AIR:
                return EARTH;
            case THUNDER:
                return EARTH; // Блискавка протилежна землі
            default:
                return NEUTRAL;
        }
    }
}