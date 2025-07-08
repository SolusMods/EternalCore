package io.github.solusmods.eternalcore.api.command;

import io.github.solusmods.eternalcore.command.CommandAnnotationHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class CommandRegistry {
    /**
     * Register a command class including all subcommands
     */
    public static <T> void registerCommand(Class<T> commandClass) {
        registerCommand(commandClass, () -> {
            try {
                return commandClass.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Register a command class including all subcommands
     */
    public static <T> void registerCommand(Class<T> commandClass, Supplier<T> factory) {
        CommandAnnotationHandler.registerCommand(commandClass, factory);
    }
}
