package io.github.solusmods.eternalcore.element.api

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity

interface ElementEvents {
    fun interface SetElementEvent {
        fun set(
            instance: ElementInstance,
            owner: LivingEntity,
            newInstance: ElementInstance,
            breakthrough: Boolean,
            notify: Changeable<Boolean?>?,
            elementMessage: Changeable<MutableComponent?>?
        ): EventResult
    }

    fun interface ElementTickEvent {
        fun tick(instance: ElementInstance, owner: LivingEntity)
    }

    fun interface AddElementEvent {
        fun add(
            instance: ElementInstance,
            owner: LivingEntity,
            advancement: Boolean,
            notifyPlayer: Changeable<Boolean?>?,
            elementMessage: Changeable<MutableComponent?>?
        ): EventResult
    }

    fun interface ForgetElementEvent {
        fun forget(
            instance: ElementInstance,
            owner: LivingEntity,
            elementMessage: Changeable<MutableComponent?>?
        ): EventResult
    }

    companion object {
        @JvmField
        val SET_ELEMENT: Event<SetElementEvent> = EventFactory.createEventResult<SetElementEvent>()
        @JvmField
        val ELEMENT_PRE_TICK: Event<ElementTickEvent> = EventFactory.createLoop<ElementTickEvent>()
        @JvmField
        val ELEMENT_POST_TICK: Event<ElementTickEvent> = EventFactory.createLoop<ElementTickEvent>()
        @JvmField
        val ADD_ELEMENT: Event<AddElementEvent> = EventFactory.createEventResult<AddElementEvent>()
        @JvmField
        val FORGET_ELEMENT: Event<ForgetElementEvent?> = EventFactory.createEventResult<ForgetElementEvent?>()
    }

}
