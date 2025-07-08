package io.github.solusmods.eternalcore.fabric.integrations;

import io.github.solusmods.eternalcore.api.command.Permission;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.BiFunction;

public class FabricPermissionsApiIntegration {
    public static BiFunction<CommandSourceStack, Permission, Boolean> PERMISSION_CHECK = (commandSourceStack, permission) -> Permissions.check(commandSourceStack, permission.value(), permission.permissionLevel().getLevel());
}
