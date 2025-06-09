package io.github.solusmods.eternalcore.config.api

import com.electronwill.nightconfig.core.CommentedConfig
import com.electronwill.nightconfig.core.Config
import net.minecraft.resources.ResourceLocation

/**
 * Base class for sub-config sections inside a [CoreConfig].
 * Supports nested configurations.
 */
abstract class CoreSubConfig {
    /**
     * Reads config values and applies them to the subconfig fields.
     */
    fun applySubConfigFields(sourceConfig: Config) {
        for (field in this.javaClass.getDeclaredFields()) {
            try {
                field.setAccessible(true)
                val value = sourceConfig.get<Any?>(field.getName())
                if (value is Config && field.get(this) is CoreSubConfig) {
                    val subConfig: CoreSubConfig = field.get(this) as CoreSubConfig;
                    subConfig.applySubConfigFields(value)
                    field.set(this, subConfig)
                } else if (value != null) field.set(this, CoreConfig.getFieldValueConverted(field, value))
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Failed to apply configuration for field: " + field.getName(), e)
            }
        }
    }

    /**
     * Saves field values into the subconfig.
     */
    fun saveSubConfigFields(subConfigInstance: CoreSubConfig?, config: CommentedConfig) {
        for (field in this.javaClass.getDeclaredFields()) {
            try {
                field.setAccessible(true)
                var value = field.get(subConfigInstance)
                if (value is CoreSubConfig) {
                    var subConfig = config.get<CommentedConfig?>(field.getName())
                    if (subConfig == null) subConfig = config.createSubConfig()
                    value.saveSubConfigFields(value, subConfig)
                    config.set<Any?>(field.getName(), subConfig)
                } else {
                    if (field.getType() == ResourceLocation::class.java && value is ResourceLocation) value =
                        value.toString()
                    if (value != null) config.set<Any?>(field.getName(), value)
                }
                val comment = field.getAnnotation<Comment?>(Comment::class.java)
                if (comment != null) config.setComment(field.getName(), comment.value)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Failed to save configuration for field: " + field.getName(), e)
            }
        }
    }
}

