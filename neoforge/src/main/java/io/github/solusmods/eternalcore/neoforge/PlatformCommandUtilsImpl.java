package io.github.solusmods.eternalcore.neoforge;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.command.Permission;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlatformCommandUtilsImpl {
    public static final Map<String, PermissionNode<Boolean>> PERMISSIONS = new HashMap<>();

    public static boolean hasPermission(CommandSourceStack commandSourceStack, Permission permission) {
        if (!commandSourceStack.isPlayer()) return true;
        return PermissionAPI.getPermission(Objects.requireNonNull(commandSourceStack.getPlayer()), PERMISSIONS.get(permission.value()));
    }

    public static void registerPermission(Permission permission) {
        var permissionId = permission.value();
        if (PERMISSIONS.containsKey(permissionId)) {
            EternalCore.LOG.info("Permission with id {} already exists. Skipped registering.", permissionId);
            return;
        }
        var modId = permissionId.substring(0, permissionId.indexOf('.'));
        var nodeName = permissionId.substring(permissionId.indexOf('.') + 1);
        PERMISSIONS.put(permissionId, new PermissionNode<>(modId, nodeName, PermissionTypes.BOOLEAN, (player, playerUUID, context) -> {
            if (player == null) return true;
            return player.hasPermissions(permission.permissionLevel().getLevel());
        }));
    }

    public static void init() {}
}
