package io.github.solusmods.eternalcore.attributes.neoforge

import io.github.solusmods.eternalcore.attributes.api.AttributeEvents
import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributeUtils.triggerCriticalAttackEffect
import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributes
import io.github.solusmods.eternalcore.network.api.util.Changeable.Companion.of
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent

@EventBusSubscriber
object NeoForgeCommonEventInvoker {
    @SubscribeEvent(priority = EventPriority.HIGH)
    fun applyEntityCrit(e: LivingIncomingDamageEvent) {
        if (e.source.directEntity !is LivingEntity) return  // Direct attack
        val attacker = e.source.directEntity as LivingEntity
        if (attacker is Player) return  // Players have their own Critical Event
        val target = e.entity

        val multiplier =
            of<Float?>(attacker.getAttributeValue(EternalCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER).toFloat())
        val chance =
            of<Double?>(attacker.getAttributeValue(EternalCoreAttributes.CRITICAL_ATTACK_CHANCE) / 100)
        if (AttributeEvents.CRITICAL_ATTACK_CHANCE_EVENT.invoker().applyCrit(attacker, target, 1f, multiplier, chance)
                .isFalse
        ) return

        if (target.getRandom().nextFloat() > chance.get()!!) return
        triggerCriticalAttackEffect(target, attacker)
        e.amount = e.amount * multiplier.get()!!
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    fun onCriticalHit(e: CriticalHitEvent) {
        if (e.isVanillaCritical) {
            val multiplier = e.entity.getAttributeValue(EternalCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER).toFloat()
            e.damageMultiplier = e.damageMultiplier / e.vanillaMultiplier * multiplier
            return
        }

        val critMultiplier = e.entity.getAttributeValue(EternalCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER).toFloat()
        val multiplier = of<Float?>(e.damageMultiplier * critMultiplier)
        val chance = of<Double?>(e.entity.getAttributeValue(EternalCoreAttributes.CRITICAL_ATTACK_CHANCE) / 100)
        if (AttributeEvents.CRITICAL_ATTACK_CHANCE_EVENT.invoker().applyCrit(
                e.entity, e.target,
                e.damageMultiplier, multiplier, chance
            ).isFalse
        ) return

        if (e.entity.getRandom().nextFloat() > chance.get()!!) return
        e.damageMultiplier = multiplier.get()!!
        e.isCriticalHit = true
    }
}
