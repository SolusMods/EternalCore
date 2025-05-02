package io.github.solusmods.eternalcore.config.api;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;

/**
 * Base class for sub-config sections inside a {@link CoreConfig}.
 * Supports nested configurations.
 */
public abstract class CoreSubConfig {

    /**
     * Reads config values and applies them to the subconfig fields.
     */
    public void applySubConfigFields(Config sourceConfig) {
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = sourceConfig.get(field.getName());
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
     * Saves field values into the subconfig.
     */
    public void saveSubConfigFields(CoreSubConfig subConfigInstance, CommentedConfig config) {
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(subConfigInstance);
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

