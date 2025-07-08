package io.github.solusmods.eternalcore.fabric;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.command.Permission;
import io.github.solusmods.eternalcore.fabric.integrations.FabricPermissionsApiIntegration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.BiFunction;

public class EternalCoreFabric implements ModInitializer {
    public static BiFunction<CommandSourceStack, Permission, Boolean> hasPermission = (commandSourceStack, permission) -> {
        if (!commandSourceStack.isPlayer()) return true;
        return commandSourceStack.hasPermission(permission.permissionLevel().getLevel());
    };

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0")) {
            hasPermission = FabricPermissionsApiIntegration.PERMISSION_CHECK;
        }
        EternalCore.init();
    }
}
