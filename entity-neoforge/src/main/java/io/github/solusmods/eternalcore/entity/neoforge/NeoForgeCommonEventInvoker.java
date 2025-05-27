package io.github.solusmods.eternalcore.entity.neoforge;

import io.github.solusmods.eternalcore.entity.api.EntityEvents;
import io.github.solusmods.eternalcore.entity.api.ProjectileHitResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class NeoForgeCommonEventInvoker {
    private NeoForgeCommonEventInvoker() {
    }

    @SubscribeEvent
    static void onLivingChangeTarget(final LivingChangeTargetEvent e) {
        if (!e.getTargetType().equals(LivingChangeTargetEvent.LivingTargetType.MOB_TARGET)) return;
        Changeable<LivingEntity> changeableTarget = Changeable.of(e.getNewAboutToBeSetTarget());
        if (EntityEvents.LIVING_CHANGE_TARGET.invoker().changeTarget(e.getEntity(), changeableTarget).isFalse()) {
            e.setCanceled(true);
        } else {
            e.setNewAboutToBeSetTarget(changeableTarget.get());
        }
    }

    @SubscribeEvent
    static void onLivingDamage(final LivingDamageEvent.Pre e) {
        Changeable<Float> changeableDamage = Changeable.of(e.getNewDamage());
        if (EntityEvents.LIVING_DAMAGE.invoker().damage(e.getEntity(), e.getSource(), changeableDamage).isFalse()) {
            e.setNewDamage(0);
        } else {
            e.setNewDamage(changeableDamage.get());
        }
    }

    @SubscribeEvent
    static void onProjectileHit(final ProjectileImpactEvent e) {
        Changeable<ProjectileHitResult> result = Changeable.of(ProjectileHitResult.DEFAULT);
        Changeable<ProjectileDeflection> deflection = Changeable.of(ProjectileDeflection.NONE);
        EntityEvents.PROJECTILE_HIT.invoker().hit(e.getRayTraceResult(), e.getProjectile(), deflection, result);
        if (result.get() != ProjectileHitResult.DEFAULT) e.setCanceled(true);
    }
}
