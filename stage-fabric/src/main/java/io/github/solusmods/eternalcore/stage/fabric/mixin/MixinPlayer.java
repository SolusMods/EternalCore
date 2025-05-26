package io.github.solusmods.eternalcore.stage.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.stage.api.entity.EntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer {
    @ModifyVariable(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", shift = At.Shift.BEFORE), argsOnly = true)
    float modifyDamage(float amount, @Local(argsOnly = true) DamageSource damageSource) {
        Changeable<Float> changeable = Changeable.of(amount);
        if (EntityEvents.LIVING_HURT.invoker().hurt((LivingEntity) (Object) this, damageSource, changeable).isFalse()) return 0.0F;
        return changeable.get();
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", shift = At.Shift.BEFORE), cancellable = true)
    void cancelActuallyHurt(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
        if (damageAmount <= 0F) ci.cancel();
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 6), argsOnly = true)
    float modifyTotalDamage(float amount, @Local(argsOnly = true) DamageSource damageSource) {
        Changeable<Float> changeable = Changeable.of(amount);
        if (EntityEvents.LIVING_DAMAGE.invoker().damage((LivingEntity) (Object) this, damageSource, changeable).isFalse()) return 0.0F;
        return changeable.get();
    }
}
