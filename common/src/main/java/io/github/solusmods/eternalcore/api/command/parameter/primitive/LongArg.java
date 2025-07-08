package io.github.solusmods.eternalcore.api.command.parameter.primitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface LongArg {
    /**
     * Argument Name in the Command
     */
    String value() default "";

    /**
     * Minimum value of the number
     */
    long min() default Long.MIN_VALUE;

    /**
     * Maximum value of the number
     */
    long max() default Long.MAX_VALUE;
}