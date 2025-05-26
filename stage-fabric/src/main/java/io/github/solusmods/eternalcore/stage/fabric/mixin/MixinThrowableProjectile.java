package io.github.solusmods.eternalcore.stage.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.stage.api.entity.*;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(ThrowableProjectile.class)
public abstract class MixinThrowableProjectile{
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    ProjectileDeflection onHit(ThrowableProjectile instance, HitResult result, Operation<ProjectileDeflection> original) {
        Changeable<ProjectileHitResult> resultChangeable = Changeable.of(ProjectileHitResult.DEFAULT);
        Changeable<ProjectileDeflection> deflectionChangeable = Changeable.of(ProjectileDeflection.NONE);
        EntityEvents.PROJECTILE_HIT.invoker().hit(result, instance, deflectionChangeable, resultChangeable);
        if (!Objects.equals(resultChangeable.get(), ProjectileHitResult.DEFAULT)) return deflectionChangeable.get();
        original.call(instance, result);
        return deflectionChangeable.get();
    }
}
