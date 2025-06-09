package io.github.solusmods.eternalcore.config.api

import com.electronwill.nightconfig.core.CommentedConfig
import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.CommentedFileConfig
import com.electronwill.nightconfig.toml.TomlParser
import io.github.solusmods.eternalcore.config.EternalCoreConfig
import net.minecraft.resources.ResourceLocation
import java.io.File
import java.io.StringReader
import java.lang.reflect.Field
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

abstract class CoreConfig {
    lateinit var config: CommentedFileConfig

    companion object {
        /**
         * Converts field values to the correct type when reading from the config.
         */
        fun getFieldValueConverted(field: Field, value: Any?): Any? {
            return when {
                field.type == Float::class.javaPrimitiveType && value is Double -> value.toFloat()
                field.type == ResourceLocation::class.java && value is String -> ResourceLocation.tryParse(value)
                field.type.isEnum && value is String -> {
                    // Use a more explicit approach to avoid type inference issues
                    val enumClass = field.type
                    val enumConstants = enumClass.enumConstants
                    enumConstants?.find { (it as Enum<*>).name == value }
                }
                else -> value
            }
        }
    }

    /**
     * Define the file name for the config (without extension).
     */
    abstract fun getFileName(): String

    /**
     * Returns the path where the config file is stored.
     */
    fun getConfigPath(): Path {
        return Paths.get("config", "${getFileName()}.toml")
    }

    /**
     * Loads the config file from disk or creates a new one if it doesn't exist.
     */
    fun load() {
        val path = getConfigPath()
        val file = path.toFile()

        if (!file.exists()) {
            try {
                file.parentFile?.mkdirs()
                file.createNewFile()
            } catch (e: Exception) {
                EternalCoreConfig.LOG!!.error("Error creating new config file at $path: ${e.message}", e)
            }
        }

        config = CommentedFileConfig.builder(path).sync().build()
        config.load()
        applyToFields()
        save()
    }

    /**
     * Loads config values from a TOML-formatted string.
     */
    fun loadFromString(tomlData: String?) {
        if (tomlData.isNullOrEmpty()) return

                val parser = TomlParser()
        try {
            val parsedConfig = parser.parse(StringReader(tomlData))
            config.putAll(parsedConfig)
            applyToFields()
        } catch (e: Exception) {
            EternalCoreConfig.LOG!!.error("Error parsing TOML data: ${e.message}", e)
        }
    }

    /**
     * Saves the config values from the class fields into the file.
     */
    fun save() {
        saveFromFields()
        config.save()
    }

    /**
     * Reads values from the TOML config and applies them to fields.
     */
    private fun applyToFields() {
        for (field in this::class.java.declaredFields) {
            try {
                field.isAccessible = true
                val value = config.getOrElse<Any>(field.name) { field.get(this) }

                when {
                    value is Config && field.get(this) is CoreSubConfig -> {
                        val sub = field.get(this) as CoreSubConfig
                        sub.applySubConfigFields(value)
                        field.set(this, sub)
                    }
                    value != null -> {
                        field.set(this, getFieldValueConverted(field, value))
                    }
                }
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Failed to apply configuration for field: ${field.name}", e)
            }
        }
    }

    /**
     * Saves field values into the config file.
     */
    private fun saveFromFields() {
        for (field in this::class.java.declaredFields) {
            try {
                field.isAccessible = true
                var value = field.get(this)

                when (value) {
                    is CoreSubConfig -> {
                        var subConfig = config.get<CommentedConfig>(field.name)
                        if (subConfig == null) {
                            subConfig = config.createSubConfig()
                        }
                        value.saveSubConfigFields(value, subConfig)
                        config.set<Any>(field.name, subConfig)
                    }
                    else -> {
                        // Convert ResourceLocation to string
                        if (field.type == ResourceLocation::class.java && value is ResourceLocation) {
                            value = value.toString()
                        }
                        if (value != null) {
                            config.set<Any>(field.name, value)
                        }
                    }
                }

                // Handle comments
                val comment = field.getAnnotation(Comment::class.java)
                if (comment != null) {
                    config.setComment(field.name, comment.value)
                }
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Failed to save configuration for field: ${field.name}", e)
            }
        }
    }
}