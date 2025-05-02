package io.github.solusmods.eternalcore.stage.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.stage.api.entity.EntityEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.BEFORE))
    void onPreTick(CallbackInfo ci) {
        EntityEvents.LIVING_PRE_TICK.invoker().tick((LivingEntity) (Object) this);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;refreshDirtyAttributes()V", shift = At.Shift.AFTER))
    void onPostTick(CallbackInfo ci) {
        EntityEvents.LIVING_POST_TICK.invoker().tick((LivingEntity) (Object) this);
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "HEAD"), cancellable = true)
    void onEffectAdded(MobEffectInstance mobEffectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalRef<MobEffectInstance> instance) {
        Changeable<MobEffectInstance> instanceChangeable = Changeable.of(mobEffectInstance);
        if (EntityEvents.LIVING_EFFECT_ADDED.invoker().effectAdd((LivingEntity) (Object) this, entity, instanceChangeable).isFalse()) {
            cir.setReturnValue(false);
            cir.cancel();
        } else instance.set(instanceChangeable.get());
    }
}
