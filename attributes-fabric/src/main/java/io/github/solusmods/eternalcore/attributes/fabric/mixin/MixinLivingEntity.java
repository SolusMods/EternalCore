package io.github.solusmods.eternalcore.attributes.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.github.solusmods.eternalcore.attributes.api.AttributeEvents;
import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributeUtils;
import io.github.solusmods.eternalcore.attributes.api.EternalCoreAttributes;
import io.github.solusmods.eternalcore.attributes.fabric.EternalCoreAttributeRegisterImpl;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 200)
public class MixinLivingEntity {

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void createLivingAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        EternalCoreAttributeRegisterImpl.addLivingEntityAttributes(cir.getReturnValue());
    }

    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"))
    void applyCriticalDamage(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalFloatRef newAmount) {
        if (damageSource.getDirectEntity() instanceof LivingEntity attacker) { // Direct attack
            if (attacker instanceof Player) return; // Players have their own Critical Event
            LivingEntity target = (LivingEntity) (Object) this;

            Changeable<Float> multiplier = Changeable.of((float) attacker.getAttributeValue(EternalCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER));
            Changeable<Double> chance = Changeable.of(attacker.getAttributeValue(EternalCoreAttributes.CRITICAL_ATTACK_CHANCE) / 100);
            if (AttributeEvents.CRITICAL_ATTACK_CHANCE_EVENT.invoker().applyCrit(attacker, target, 1, multiplier, chance).isFalse()) return;

            if (target.getRandom().nextFloat() > chance.get()) return;
            EternalCoreAttributeUtils.triggerCriticalAttackEffect(target, attacker);
            newAmount.set(amount * multiplier.get());
        }
    }
}
