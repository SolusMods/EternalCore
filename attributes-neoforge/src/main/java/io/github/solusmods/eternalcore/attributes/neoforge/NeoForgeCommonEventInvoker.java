package io.github.solusmods.eternalcore.attributes.neoforge;

import io.github.solusmods.eternalcore.attributes.api.AttributeEvents;
import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributeUtils;
import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributes;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

@EventBusSubscriber
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NeoForgeCommonEventInvoker {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void applyEntityCrit(final LivingIncomingDamageEvent e) {
        if (!(e.getSource().getDirectEntity() instanceof LivingEntity attacker)) return; // Direct attack
        if (attacker instanceof Player) return; // Players have their own Critical Event
        LivingEntity target = e.getEntity();

        Changeable<Float> multiplier = Changeable.of((float) attacker.getAttributeValue(EternalCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER));
        Changeable<Double> chance = Changeable.of(attacker.getAttributeValue(EternalCoreAttributes.CRITICAL_ATTACK_CHANCE) / 100);
        if (AttributeEvents.CRITICAL_ATTACK_CHANCE_EVENT.invoker().applyCrit(attacker, target, 1, multiplier, chance).isFalse()) return;

        if (target.getRandom().nextFloat() > chance.get()) return;
        EternalCoreAttributeUtils.triggerCriticalAttackEffect(target, attacker);
        e.setAmount(e.getAmount() * multiplier.get());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onCriticalHit(final CriticalHitEvent e) {
        if (e.isVanillaCritical()) {
            float multiplier = (float) e.getEntity().getAttributeValue(EternalCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER);
            e.setDamageMultiplier(e.getDamageMultiplier() / e.getVanillaMultiplier() * multiplier);
            return;
        }

        float critMultiplier = (float) e.getEntity().getAttributeValue(EternalCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER);
        Changeable<Float> multiplier = Changeable.of(e.getDamageMultiplier() * critMultiplier);
        Changeable<Double> chance = Changeable.of(e.getEntity().getAttributeValue(EternalCoreAttributes.CRITICAL_ATTACK_CHANCE) / 100);
        if (AttributeEvents.CRITICAL_ATTACK_CHANCE_EVENT.invoker().applyCrit(e.getEntity(), e.getTarget(),
                e.getDamageMultiplier(), multiplier, chance).isFalse()) return;

        if (e.getEntity().getRandom().nextFloat() > chance.get()) return;
        e.setDamageMultiplier(multiplier.get());
        e.setCriticalHit(true);
    }
}
