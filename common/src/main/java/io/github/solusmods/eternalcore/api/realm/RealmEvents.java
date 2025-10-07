package io.github.solusmods.eternalcore.api.realm;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public interface RealmEvents {
    Event<SetRealmEvent> SET_REALM = EventFactory.createEventResult();
    Event<RealmTickEvent> REALM_PRE_TICK = EventFactory.createLoop();
    Event<RealmTickEvent> REALM_POST_TICK = EventFactory.createLoop();
    Event<ReachRealmEvent> REACH_REALM = EventFactory.createEventResult();

    @FunctionalInterface
    interface SetRealmEvent {
        EventResult set(AbstractRealm realm, LivingEntity owner, AbstractRealm newRealm, boolean breakthrough, Changeable<Boolean> notify, Changeable<MutableComponent> realmMessage);
    }

    @FunctionalInterface
    interface RealmTickEvent {
        void tick(AbstractRealm realm, LivingEntity owner);
    }

    @FunctionalInterface
    interface ReachRealmEvent {
        EventResult reach(AbstractRealm realm, LivingEntity owner, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> realmMessage);
    }
}
