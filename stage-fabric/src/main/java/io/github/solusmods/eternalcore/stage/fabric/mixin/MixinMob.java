package io.github.solusmods.eternalcore.stage.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.stage.api.entity.EntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MixinMob {
    @WrapOperation(method = "setTarget", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Mob;target:Lnet/minecraft/world/entity/LivingEntity;"))
    private void onSetTarget(Mob instance, LivingEntity value, Operation<LivingEntity> original) {
        Changeable<LivingEntity> target = Changeable.of(value);
        if (EntityEvents.LIVING_CHANGE_TARGET.invoker().changeTarget(instance, target).isFalse()) {
            original.call(instance, value);
        } else original.call(instance, target.get());
    }
}