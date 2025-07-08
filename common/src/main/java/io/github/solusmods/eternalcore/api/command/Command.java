package io.github.solusmods.eternalcore.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
    /**
     * The name of the command including aliases
     */
    String[] value();

    /**
     * Subcommands of the command
     */
    Class<?>[] subCommands() default {};
}
