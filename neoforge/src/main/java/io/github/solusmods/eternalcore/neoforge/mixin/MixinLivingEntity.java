package io.github.solusmods.eternalcore.neoforge.mixin;

import io.github.solusmods.eternalcore.api.entity.EntityEvents;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Stack;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow
    public Stack<DamageContainer> damageContainers;
    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z", shift = At.Shift.AFTER), cancellable = true)
    void onHurt(DamageSource source, float amount, CallbackInfo ci) {
        Changeable<Float> changeable = Changeable.of(amount);
        if (EntityEvents.LIVING_HURT.invoker().hurt((LivingEntity) (Object) this, source, changeable).isFalse()) ci.cancel();
        else damageContainers.peek().setNewDamage(changeable.get());
    }
}
