package io.github.solusmods.eternalcore.attributes.neoforge

import net.minecraft.core.Holder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import net.neoforged.fml.ModLoadingContext
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import java.util.function.Supplier

object EternalCoreAttributeRegisterImpl {
    private val GENERIC_REGISTRY: MutableList<Holder<Attribute?>?> = CopyOnWriteArrayList<Holder<Attribute?>?>()
    private val PLAYER_REGISTRY: MutableList<Holder<Attribute?>?> = CopyOnWriteArrayList<Holder<Attribute?>?>()

    fun registerToPlayers(holder: Holder<Attribute?>): Holder<Attribute?> {
        PLAYER_REGISTRY.add(holder)
        return holder
    }

    fun registerToGeneric(holder: Holder<Attribute?>): Holder<Attribute?> {
        GENERIC_REGISTRY.add(holder)
        return holder
    }

    @JvmStatic
    fun registerPlayerAttribute(
        modID: String?, id: String, name: String, amount: Double,
        min: Double, max: Double, syncable: Boolean, sentiment: Attribute.Sentiment
    ): Holder<Attribute?> {
        val attribute = RangedAttribute(name, amount, min, max).setSyncable(syncable).setSentiment(sentiment)
        return registerToPlayers(
            EternalCoreAttributeNeoForge.attributes.register(
                id,
                Supplier { attribute })
        )
    }

    @JvmStatic
    fun registerGenericAttribute(
        modID: String?, id: String, name: String, amount: Double,
        min: Double, max: Double, syncable: Boolean, sentiment: Attribute.Sentiment
    ): Holder<Attribute?> {
        val attribute = RangedAttribute(name, amount, min, max).setSyncable(syncable).setSentiment(sentiment)
        return registerToGeneric(
            EternalCoreAttributeNeoForge.attributes.register<Attribute?>(
                id,
                Supplier { attribute })
        )
    }

    fun registerAttributes(e: EntityAttributeModificationEvent) {
        e.types.forEach(Consumer { type: EntityType<out LivingEntity?>? ->
            if (type == EntityType.PLAYER) PLAYER_REGISTRY.forEach(Consumer { holder: Holder<Attribute?>? ->
                e.add(
                    type,
                    holder
                )
            })
            GENERIC_REGISTRY.forEach(Consumer { holder: Holder<Attribute?>? -> e.add(type, holder) })
        })

        // Clear the registry
        PLAYER_REGISTRY.clear()
        GENERIC_REGISTRY.clear()
    }

    @JvmStatic
    fun init() {
        val modEventBus = ModLoadingContext.get().activeContainer.eventBus
        if (modEventBus == null) return
        modEventBus.addListener { obj: EntityAttributeModificationEvent? -> registerAttributes(obj!!) }
    }
}
