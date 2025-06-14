package io.github.solusmods.eternalcore.abilities.impl.network.c2s

import io.github.solusmods.eternalcore.abilities.api.AbilityAPI
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage
import io.github.solusmods.eternalcore.abilities.impl.TickingAbility
import io.github.solusmods.eternalcore.network.api.util.Changeable
import io.github.solusmods.eternalcore.storage.api.StorageHolder
import io.github.solusmods.eternalcore.storage.api.StorageKey
import io.github.solusmods.eternalcore.storage.impl.StorageManager
import net.minecraft.world.entity.player.Player
import java.util.function.Consumer

object ClientAccess {
    fun handle(packet: RequestAbilityActivationPacket, player: Player?) {
        if (player == null) return
        val storage = AbilityAPI.getAbilitiesFrom(player)
        storage!!.getAbility(packet.abilityId!!)?.ifPresent { instance: AbilityInstance ->
            val changeable = Changeable.of(instance)
            if (AbilityEvents.Companion.ACTIVATE_ABILITY.invoker()
                !!.activateAbility(changeable, player, packet.keyNumber, packet.mode)!!.isFalse
            ) return@ifPresent

            val abilityInstance = changeable.get()
            if (abilityInstance == null) return@ifPresent
            if (!abilityInstance.canInteractAbility(player)) return@ifPresent

            if (packet.mode < 0 || packet.mode >= abilityInstance.modes) return@ifPresent
            if (abilityInstance.onCoolDown(packet.mode) && !abilityInstance.canIgnoreCoolDown(
                    player,
                    packet.mode
                )
            ) return@ifPresent

            abilityInstance.onPressed(player, packet.keyNumber, packet.mode)
            abilityInstance.addHeldAttributeModifiers(player, packet.mode)
            AbilityStorage.Companion.tickingAbilities.put(
                player.getUUID(),
                TickingAbility(abilityInstance.ability!!, packet.mode)
            )
            storage.markDirty()
        }
    }

    fun handle(packet: RequestAbilityReleasePacket, player: Player?) {
        if (player == null) return
        val storage = StorageManager.getStorage(player as StorageHolder,
            AbilityStorage.key as StorageKey<AbilityStorage>
        )
        if (storage == null) return
        storage.handleAbilityRelease(packet.abilityId!!, packet.heldTick, packet.keyNumber, packet.mode)
    }

    fun handle(packet: RequestAbilityScrollPacket, player: Player?) {
        if (player == null) return

        val storage = AbilityAPI.getAbilitiesFrom(player)
        for (skillId in packet.abilityList!!) {
            storage!!.getAbility(skillId!!)?.ifPresent { abilityInstance: AbilityInstance ->
                val skillChangeable = Changeable.of<AbilityInstance>(abilityInstance)
                val deltaChangeable = Changeable.of<Double>(packet.delta)
                if (AbilityEvents.Companion.ABILITY_SCROLL.invoker()!!.scroll(skillChangeable, player, deltaChangeable).isFalse
                ) return@ifPresent

                val abilityInstance1 = skillChangeable.get()
                if (abilityInstance1 == null || deltaChangeable.isEmpty) return@ifPresent
                if (!abilityInstance1.canScroll(player)) return@ifPresent
                if (!abilityInstance1.canInteractAbility(player)) return@ifPresent

                abilityInstance1.onScroll(player, deltaChangeable.get()!!, 0)
                storage.markDirty()
            }
        }
    }

    fun handle(packet: RequestAbilityTogglePacket, player: Player?) {
        if (player == null) return
        val storage = AbilityAPI.getAbilitiesFrom(player)
        storage!!.getAbility(packet.abilityId!!)?.ifPresent { abilityInstance: AbilityInstance ->
            val changeable = Changeable.of(abilityInstance)
            if (AbilityEvents.Companion.TOGGLE_ABILITY.invoker()!!.toggleAbility(changeable, player)
                !!.isFalse
            ) return@ifPresent

            val skill = changeable.get()
            if (skill == null) return@ifPresent
            if (!skill.canInteractAbility(player)) return@ifPresent

            if (skill.isToggled()) {
                skill.setToggled(false)
                skill.onToggleOff(player)
            } else {
                skill.setToggled(true)
                skill.onToggleOn(player)
            }
            storage.markDirty()
        }
    }
}
