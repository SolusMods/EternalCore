package io.github.solusmods.eternalcore.config.api

/**
 * Annotation to add comments to fields inside a [CoreConfig].
 * These comments will be included in the generated TOML config files.
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class Comment(
    val value: String // The comment text to be added to the config file.
)