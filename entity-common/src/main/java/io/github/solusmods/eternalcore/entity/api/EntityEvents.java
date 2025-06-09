package io.github.solusmods.eternalcore.entity.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;

public interface EntityEvents {
    Event<LivingTickEvent> LIVING_PRE_TICK = EventFactory.createLoop();
    Event<LivingTickEvent> LIVING_POST_TICK = EventFactory.createLoop();
    Event<LivingEffectAddedEvent> LIVING_EFFECT_ADDED = EventFactory.createEventResult();
    Event<LivingChangeTargetEvent> LIVING_CHANGE_TARGET = EventFactory.createEventResult();
    Event<LivingHurtEvent> LIVING_HURT = EventFactory.createEventResult();
    Event<LivingDamageEvent> LIVING_DAMAGE = EventFactory.createEventResult();
    Event<ProjectileHitEvent> PROJECTILE_HIT = EventFactory.createLoop();




    @FunctionalInterface
    interface LivingTickEvent {
        void tick(LivingEntity entity);
    }

    @FunctionalInterface
    interface LivingEffectAddedEvent {
        EventResult effectAdd(LivingEntity entity, Entity source, Changeable<MobEffectInstance> effect);
    }

    @FunctionalInterface
    interface LivingChangeTargetEvent {
        EventResult changeTarget(LivingEntity entity, Changeable<LivingEntity> target);
    }

    @FunctionalInterface
    interface LivingHurtEvent {
        EventResult hurt(LivingEntity living, DamageSource source, Changeable<Float> amount);
    }

    @FunctionalInterface
    interface LivingDamageEvent {
        EventResult damage(LivingEntity entity, DamageSource source, Changeable<Float> amount);
    }

    @FunctionalInterface
    interface ProjectileHitEvent {
        void hit(HitResult hitResult, Projectile projectile, Changeable<ProjectileDeflection> deflection, Changeable<ProjectileHitResult> result);
    }
}