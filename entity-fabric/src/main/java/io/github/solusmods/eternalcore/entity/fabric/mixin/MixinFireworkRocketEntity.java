package io.github.solusmods.eternalcore.entity.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.solusmods.eternalcore.entity.api.EntityEvents;
import io.github.solusmods.eternalcore.entity.api.ProjectileHitResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireworkRocketEntity.class)
public abstract class MixinFireworkRocketEntity extends Projectile {
    public MixinFireworkRocketEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    @Nullable
    private ProjectileHitResult onHitEventResult = null;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    ProjectileDeflection onHit(FireworkRocketEntity instance, HitResult result, Operation<ProjectileDeflection> original) {
        Changeable<ProjectileHitResult> resultChangeable = Changeable.of(ProjectileHitResult.DEFAULT);
        Changeable<ProjectileDeflection> deflectionChangeable = Changeable.of(ProjectileDeflection.NONE);
        EntityEvents.PROJECTILE_HIT.invoker().hit(result, instance, deflectionChangeable, resultChangeable);
        this.onHitEventResult = resultChangeable.get();
        if (this.onHitEventResult == null) return original.call(instance, result);

        switch (this.onHitEventResult) {
            case DEFAULT, HIT -> {
                original.call(instance, result);
                this.onHitEventResult = null;
                return deflectionChangeable.get();
            }
            case HIT_NO_DAMAGE -> this.discard();
            case PASS -> {
                if (result.getType() != HitResult.Type.ENTITY) {
                    original.call(instance, result);
                    this.onHitEventResult = null;
                    return deflectionChangeable.get();
                }
            }
        }
        return deflectionChangeable.get();
    }

    @WrapOperation(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;hasImpulse:Z"))
    void onImpulseSet(FireworkRocketEntity instance, boolean value, Operation<Boolean> original) {
        if (this.onHitEventResult == null) original.call(instance, value);
        this.onHitEventResult = null;
    }
}
