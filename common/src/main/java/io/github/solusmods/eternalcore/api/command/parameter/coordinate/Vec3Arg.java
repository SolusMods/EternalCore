package io.github.solusmods.eternalcore.api.command.parameter.coordinate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Vec3Arg {
    /**
     * Argument Name in the Command
     */
    String name() default "";

    /**
     * Argument Name in the Command
     */
    Type value() default Type.NORMAL;

    enum Type {
        NORMAL,
        CENTER
    }
}