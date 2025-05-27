package io.github.solusmods.eternalcore.attributes.api;

import io.github.solusmods.eternalcore.attributes.EternalCoreAttributeRegister;
import io.github.solusmods.eternalcore.network.ModuleConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class EternalCoreAttributes {

    /**
     * Determine how much the output damage is multiplied when the attacker does a critical attack.
     */
    public static final Holder<Attribute> CRITICAL_DAMAGE_MULTIPLIER = EternalCoreAttributeRegister.registerGenericAttribute(ModuleConstants.MOD_ID,
            "critical_damage_multiplier", "eternalcore.attribute.critical_damage_multiplier",
            1.5, 0, 1024, true, Attribute.Sentiment.POSITIVE);

    /**
     * Determine the percentage chance for the user to do a critical attack without jumping.
     */
    public static final Holder<Attribute> CRITICAL_ATTACK_CHANCE = EternalCoreAttributeRegister.registerGenericAttribute(ModuleConstants.MOD_ID,
            "critical_attack_chance", "eternalcore.attribute.critical_attack_chance",
            0, 0, 100, true, Attribute.Sentiment.POSITIVE);

    /**
     * Determine how fast the player can elytra glide without wearing an elytra.
     * Sets higher than 0 to allow the player to glide.
     * This also affects the speed of normal Elytra gliding speed.
     */
    public static final Holder<Attribute> GLIDE_SPEED_MULTIPLIER = EternalCoreAttributeRegister.registerPlayerAttribute(ModuleConstants.MOD_ID,
            "glide_speed_multiplier", "eternalcore.attribute.glide_speed_multiplier",
            0, 0, 1024, true, Attribute.Sentiment.POSITIVE);

    /**
     * Determine how fast the player can go inside Lava.
     */
    public static final Holder<Attribute> LAVA_SPEED_MULTIPLIER = EternalCoreAttributeRegister.registerGenericAttribute(ModuleConstants.MOD_ID,
            "lava_speed_multiplier", "manascore.attribute.lava_speed_multiplier",
            1, 0, 1024, true, Attribute.Sentiment.POSITIVE);

    /**
     * Determine how fast the player can swim in Water.
     * Similar to NeoForge/Forge's Swim speed instead of Vanilla's Water Movement Efficiency.
     */
    public static final Holder<Attribute> SWIM_SPEED_MULTIPLIER = EternalCoreAttributeRegister.registerGenericAttribute(ModuleConstants.MOD_ID,
            "swim_speed_multiplier", "manascore.attribute.swim_speed_multiplier",
            1, 0, 1024, true, Attribute.Sentiment.POSITIVE);

    public static ResourceKey<Attribute> getResourceKey(String modID, String path) {
        return ResourceKey.create(Registries.ATTRIBUTE, ResourceLocation.fromNamespaceAndPath(modID, path));
    }

    public static void init() {
    }
}
