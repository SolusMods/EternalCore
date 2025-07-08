package io.github.solusmods.eternalcore.api.command.parameter.primitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface EnumArg {
    Class<? extends java.lang.Enum> value();
}
