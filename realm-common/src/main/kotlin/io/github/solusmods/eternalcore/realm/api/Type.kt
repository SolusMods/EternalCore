package io.github.solusmods.eternalcore.realm.api

import io.github.solusmods.eternalcore.realm.ModuleConstants
import lombok.RequiredArgsConstructor
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

/**
 * Перелік типів Реалмів  у системі культивації.
 *
 *
 * Типи представляють рівні Реалмів  у ієрархії, від I (найнижчий) до XI (найвищий).
 *
 */
@RequiredArgsConstructor
enum class Type(var component: MutableComponent) {
    I(Component.translatable("%s.stage.type.1".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
    II(Component.translatable("%s.stage.type.2".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
    III(Component.translatable("%s.stage.type.3".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),

    IV(Component.translatable("%s.stage.type.4".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.YELLOW)),
    V(Component.translatable("%s.stage.type.5".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.YELLOW)),
    VI(Component.translatable("%s.stage.type.6".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.YELLOW)),

    VII(Component.translatable("%s.stage.type.7".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GOLD)),
    VIII(Component.translatable("%s.stage.type.8".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GOLD)),
    IX(Component.translatable("%s.stage.type.9".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GOLD)),

    X(Component.translatable("%s.stage.type.10".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.RED)),
    XI(Component.translatable("%s.stage.type.11".format(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.RED));

}
