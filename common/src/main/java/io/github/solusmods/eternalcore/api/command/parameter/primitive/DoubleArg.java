package io.github.solusmods.eternalcore.api.command.parameter.primitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface DoubleArg {
    /**
     * Argument Name in the Command
     */
    String value() default "";

    /**
     * Minimum value of the number
     */
    double min() default Double.MIN_VALUE;

    /**
     * Maximum value of the number
     */
    double max() default Double.MAX_VALUE;
}