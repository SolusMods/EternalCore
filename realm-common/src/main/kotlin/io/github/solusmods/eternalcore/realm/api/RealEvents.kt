package io.github.solusmods.eternalcore.realm.api

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity

interface RealEvents {
    fun interface SetRealmEvent {
        fun set(
            instance: RealmInstance?,
            owner: LivingEntity?,
            newInstance: RealmInstance?,
            breakthrough: Boolean,
            notify: Changeable<Boolean?>?,
            realmMessage: Changeable<MutableComponent?>?
        ): EventResult?
    }

    fun interface RealmTickEvent {
        fun tick(instance: RealmInstance?, owner: LivingEntity?)
    }

    fun interface ReachRealmEvent {
        fun reach(
            instance: RealmInstance?,
            owner: LivingEntity?,
            advancement: Boolean,
            notifyPlayer: Changeable<Boolean?>?,
            realmMessage: Changeable<MutableComponent?>?
        ): EventResult?
    }

    fun interface TrackRealmEvent {
        fun track(
            instance: RealmInstance?,
            owner: LivingEntity?,
            advancement: Boolean,
            notifyPlayer: Changeable<Boolean?>?,
            realmMessage: Changeable<MutableComponent?>?
        ): EventResult?
    }

    companion object {
        val SET_REALM: Event<SetRealmEvent?> = EventFactory.createEventResult<SetRealmEvent?>()
        val REALM_PRE_TICK: Event<RealmTickEvent?> = EventFactory.createLoop<RealmTickEvent?>()
        val REALM_POST_TICK: Event<RealmTickEvent?> = EventFactory.createLoop<RealmTickEvent?>()
        val REACH_REALM: Event<ReachRealmEvent?> = EventFactory.createEventResult<ReachRealmEvent?>()
        val TRACK_REALM: Event<TrackRealmEvent?> = EventFactory.createEventResult<TrackRealmEvent?>()
    }
}
