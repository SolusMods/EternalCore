package io.github.solusmods.eternalcore.realm.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public interface RealEvents {
    Event<SetRealmEvent> SET_REALM = EventFactory.createEventResult();
    Event<RealmTickEvent> REALM_PRE_TICK = EventFactory.createLoop();
    Event<RealmTickEvent> REALM_POST_TICK = EventFactory.createLoop();
    Event<ReachRealmEvent> REACH_REALM = EventFactory.createEventResult();
    Event<TrackRealmEvent> TRACK_REALM = EventFactory.createEventResult();

    @FunctionalInterface
    interface SetRealmEvent {
        EventResult set(RealmInstance instance, LivingEntity owner, RealmInstance newInstance, boolean breakthrough, Changeable<Boolean> notify, Changeable<MutableComponent> realmMessage);
    }

    @FunctionalInterface
    interface RealmTickEvent {
        void tick(RealmInstance instance, LivingEntity owner);
    }

    @FunctionalInterface
    interface ReachRealmEvent {
        EventResult reach(RealmInstance instance, LivingEntity owner, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> realmMessage);
    }

    @FunctionalInterface
    interface TrackRealmEvent {
        EventResult track(RealmInstance instance, LivingEntity owner, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> realmMessage);
    }
}
