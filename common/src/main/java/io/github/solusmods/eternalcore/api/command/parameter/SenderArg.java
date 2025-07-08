package io.github.solusmods.eternalcore.api.command.parameter;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a parameter as the sender of the command<br><br>
 * Supported Types:<br>
 * - {@link CommandSourceStack}<br>
 * - {@link ServerPlayer}<br>
 * - {@link CommandSource}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface SenderArg {

}
