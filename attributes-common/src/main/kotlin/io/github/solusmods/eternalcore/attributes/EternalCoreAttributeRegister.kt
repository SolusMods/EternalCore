package io.github.solusmods.eternalcore.attributes

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.core.Holder
import net.minecraft.world.entity.ai.attributes.Attribute

object EternalCoreAttributeRegister {
    /**
     * Registers a player-specific attribute with the given parameters.
     *
     * @param modID      The mod ID associated with this attribute.
     * @param id         The unique identifier for the attribute.
     * @param name       The display name of the attribute.
     * @param amount     The default base value of the attribute.
     * @param min        The minimum allowed value for the attribute.
     * @param max        The maximum allowed value for the attribute.
     * @param syncable   Whether the attribute should be synchronized between client and server.
     * @param sentiment  The sentiment classification of the attribute (e.g., beneficial or harmful).
     * @return A [Holder] containing the registered player attribute.
     */
    @ExpectPlatform
    @JvmStatic
    fun registerPlayerAttribute(
        modID: String?, id: String?, name: String?, amount: Double,
        min: Double, max: Double, syncable: Boolean, sentiment: Attribute.Sentiment?
    ): Holder<Attribute?> {
        throw AssertionError()
    }

    /**
     * Registers a generic attribute that applies to multiple entity types.
     *
     * @param modID      The mod ID associated with this attribute.
     * @param id         The unique identifier for the attribute.
     * @param name       The display name of the attribute.
     * @param amount     The default base value of the attribute.
     * @param min        The minimum allowed value for the attribute.
     * @param max        The maximum allowed value for the attribute.
     * @param syncable   Whether the attribute should be synchronized between client and server.
     * @param sentiment  The sentiment classification of the attribute (e.g., beneficial or harmful).
     * @return A [Holder] containing the registered generic attribute.
     */
    @ExpectPlatform
    @JvmStatic
    fun registerGenericAttribute(
        modID: String?, id: String?, name: String?, amount: Double,
        min: Double, max: Double, syncable: Boolean, sentiment: Attribute.Sentiment?
    ): Holder<Attribute?> {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun init() {
        throw AssertionError()
    }
}