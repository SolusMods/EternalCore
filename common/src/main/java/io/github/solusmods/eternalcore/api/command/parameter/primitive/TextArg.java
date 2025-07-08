package io.github.solusmods.eternalcore.api.command.parameter.primitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface TextArg {
    /**
     * Type of the Argument
     */
    Type value() default Type.WORD;

    /**
     * Argument Name in the Command
     */
    String name() default "";

    enum Type {
        /**
         * Single Word Argument
         */
        WORD,
        /**
         * Quoted String Argument
         */
        STRING,
        /**
         * Greedy String Argument
         */
        GREEDY_STRING
    }
}