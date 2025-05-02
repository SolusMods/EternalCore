package io.github.solusmods.eternalcore.config.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to add comments to fields inside a {@link CoreConfig}.
 * These comments will be included in the generated TOML config files.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {
    String value(); // The comment text to be added to the config file.
}