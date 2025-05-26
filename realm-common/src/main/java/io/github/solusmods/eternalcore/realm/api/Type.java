package io.github.solusmods.eternalcore.realm.api;

import io.github.solusmods.eternalcore.realm.ModuleConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Перелік типів Реалмів  у системі культивації.
 * <p>
 * Типи представляють рівні Реалмів  у ієрархії, від I (найнижчий) до XI (найвищий).
 * </p>
 */
@RequiredArgsConstructor
public enum Type {
    I(Component.translatable("%s.realm.type.1".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
    II(Component.translatable("%s.realm.type.2".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
    III(Component.translatable("%s.realm.type.3".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),

    IV(Component.translatable("%s.realm.type.4".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.YELLOW)),
    V(Component.translatable("%s.realm.type.5".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.YELLOW)),
    VI(Component.translatable("%s.realm.type.6".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.YELLOW)),

    VII(Component.translatable("%s.realm.type.7".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GOLD)),
    VIII(Component.translatable("%s.realm.type.8".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GOLD)),
    IX(Component.translatable("%s.realm.type.9".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GOLD)),

    X(Component.translatable("%s.realm.type.10".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.RED)),
    XI(Component.translatable("%s.realm.type.11".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.RED));


    /** Локалізована назва типу Реалму  */
    @Getter
    private final MutableComponent name;
}
