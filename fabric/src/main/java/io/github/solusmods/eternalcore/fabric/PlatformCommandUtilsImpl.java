package io.github.solusmods.eternalcore.fabric;

import io.github.solusmods.eternalcore.api.command.Permission;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;

@UtilityClass
public class PlatformCommandUtilsImpl {
    public static boolean hasPermission(CommandSourceStack commandSourceStack, Permission permission) {
        return EternalCoreFabric.hasPermission.apply(commandSourceStack, permission);
    }

    public static void registerPermission(Permission permission) {
        // No need to register permissions on Fabric
    }

    public static void init() {
    }
}


