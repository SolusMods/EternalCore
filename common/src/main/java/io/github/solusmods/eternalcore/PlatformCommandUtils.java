package io.github.solusmods.eternalcore;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.solusmods.eternalcore.api.command.Permission;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;

@UtilityClass
public class PlatformCommandUtils {
    @ExpectPlatform
    public static boolean hasPermission(CommandSourceStack commandSourceStack, Permission permission) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerPermission(Permission permission) {
        throw new AssertionError();
    }


    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }
}
