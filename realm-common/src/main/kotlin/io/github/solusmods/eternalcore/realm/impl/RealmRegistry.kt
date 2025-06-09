package io.github.solusmods.eternalcore.realm.impl

import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import io.github.solusmods.eternalcore.realm.EternalCoreRealm
import io.github.solusmods.eternalcore.realm.ModuleConstants
import io.github.solusmods.eternalcore.realm.api.Realm
import io.github.solusmods.eternalcore.realm.api.RealmAPI
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity


object RealmRegistry {
    private val registryId: ResourceLocation = EternalCoreRealm.create("realms")
    val REALMS: Registrar<Realm?> = RegistrarManager.get(ModuleConstants.MOD_ID).builder<Realm?>(registryId)
        .syncToClients().build()
    val KEY: ResourceKey<Registry<Realm?>?>? = REALMS.key() as ResourceKey<Registry<Realm?>?>?


    fun init() {
        PlayerEvent.PLAYER_RESPAWN.register(PlayerEvent.PlayerRespawn { newPlayer: ServerPlayer?, conqueredEnd: Boolean, removalReason: Entity.RemovalReason? ->
            val optional = RealmAPI.getRealmFrom(
                newPlayer!!
            )!!.realm
            if (optional!!.isEmpty) return@PlayerRespawn

            val instance = optional.get()
            if (!conqueredEnd) {
                instance.addAttributeModifiers(newPlayer, 0)
            }
        })
    }
}
