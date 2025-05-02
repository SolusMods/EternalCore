package io.github.solusmods.eternalcore.config.api;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import io.github.solusmods.eternalcore.config.EternalCoreConfig;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;


@Getter
public abstract class CoreConfig {
    private CommentedFileConfig config;

    /**
     * Converts field values to the correct type when reading from the config.
     */
    public static Object getFieldValueConverted(Field field, Object value) {
        if (field.getType() == float.class && value instanceof Double d) return d.floatValue();
        if (field.getType() == ResourceLocation.class && value instanceof String s) return ResourceLocation.tryParse(s);
        if (field.getType().isEnum() && value instanceof String s)
            return Enum.valueOf((Class<Enum>) field.getType(), s);
        return value;
    }

    /**
     * Define the file name for the config (without extension).
     */
    public abstract String getFileName();

    /**
     * Returns the path where the config file is stored.
     */
    public Path getConfigPath() {
        return Paths.get("config", this.getFileName() + ".toml");
    }

    /**
     * Loads the config file from disk or creates a new one if it doesn't exist.
     */
    public void load() {
        Path path = getConfigPath();
        File file = path.toFile();
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                EternalCoreConfig.LOG.error("Error creating new config file at " + path + ": " + e.getMessage(), e);
            }
        }

        config = CommentedFileConfig.builder(path).sync().build();
        config.load();
        applyToFields();
        save();
    }

    /**
     * Loads config values from a TOML-formatted string.
     */
    public void loadFromString(String tomlData) {
        if (tomlData == null || tomlData.isEmpty()) return;
        TomlParser parser = new TomlParser();
        try {
            Config parsedConfig = parser.parse(new StringReader(tomlData));
            config.putAll(parsedConfig);
            applyToFields();
        } catch (Exception e) {
            EternalCoreConfig.LOG.error("Error parsing TOML data: " + e.getMessage(), e);
        }
    }

    /**
     * Saves the config values from the class fields into the file.
     */
    public void save() {
        saveFromFields();
        config.save();
    }

    /**
     * Reads values from the TOML config and applies them to fields.
     */
    private void applyToFields() {
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = config.getOrElse(field.getName(), field.get(this));
                if (value instanceof Config configSub && field.get(this) instanceof CoreSubConfig sub) {
                    sub.applySubConfigFields(configSub);
                    field.set(this, sub);
                } else if (value != null) field.set(this, CoreConfig.getFieldValueConverted(field, value));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to apply configuration for field: " + field.getName(), e);
            }
        }
    }

    /**
     * Saves field values into the config file.
     */
    private void saveFromFields() {
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value instanceof CoreSubConfig sub) {
                    CommentedConfig subConfig = config.get(field.getName());
                    if (subConfig == null) subConfig = config.createSubConfig();
                    sub.saveSubConfigFields(sub, subConfig);
                    config.set(field.getName(), subConfig);
                } else {
                    if (field.getType() == ResourceLocation.class && value instanceof ResourceLocation rl)
                        value = rl.toString();
                    if (value != null) config.set(field.getName(), value);
                }
                Comment comment = field.getAnnotation(Comment.class);
                if (comment != null) config.setComment(field.getName(), comment.value());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to save configuration for field: " + field.getName(), e);
            }
        }
    }
}
