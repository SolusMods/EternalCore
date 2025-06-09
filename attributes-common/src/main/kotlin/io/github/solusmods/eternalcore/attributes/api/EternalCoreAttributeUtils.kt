package io.github.solusmods.eternalcore.attributes.api

import io.github.solusmods.eternalcore.network.api.util.Changeable.Companion.of
import net.minecraft.network.protocol.game.ClientboundAnimatePacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.EnchantmentHelper

object EternalCoreAttributeUtils {
    fun getAttackDamage(player: Player): Float {
        var f = player.getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
        val h = player.getAttackStrengthScale(0.5f)
        f *= 0.2f + h * h * 0.8f
        return f
    }

    fun getWeaponDamage(attacker: LivingEntity, target: Entity?, source: DamageSource?): Float {
        val attack = attacker.getAttribute(Attributes.ATTACK_DAMAGE)
        if (attack == null) return 0f

        var damage = 1f
        val modifier = attack.getModifier(Item.BASE_ATTACK_DAMAGE_ID)
        if (modifier != null) damage += modifier.amount().toFloat()
        val serverLevel = attacker.level() as ServerLevel
        if (target != null && source != null && attacker.level() is ServerLevel) damage =
            EnchantmentHelper.modifyDamage(serverLevel, attacker.weaponItem, target, source, damage)
        return damage
    }

    @JvmStatic
    fun triggerCriticalAttackEffect(target: Entity, attacker: Entity) {
        target.level().playSound(
            null, target.x, target.y, target.z,
            SoundEvents.PLAYER_ATTACK_CRIT, attacker.soundSource, 1.0f, 1.0f
        )

        if (target.level() is ServerLevel) {
            val level = target.level() as ServerLevel
            level.chunkSource
                .broadcastAndSend(target, ClientboundAnimatePacket(target, 4))
        }
    }

    @JvmStatic
    fun canElytraGlide(entity: LivingEntity, additionalCheck: Boolean): Boolean {
        val glide = of<Boolean?>(
            additionalCheck && !entity.onGround() && !entity.isPassenger() && !entity.hasEffect(MobEffects.LEVITATION) && entity.getAttributeValue(
                EternalCoreAttributes.GLIDE_SPEED_MULTIPLIER
            ) > 0
        )
        if (AttributeEvents.START_GLIDE_EVENT.invoker().glide(entity, glide).isFalse) return false
        return glide.get()!!
    }
}