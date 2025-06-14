package io.github.solusmods.eternalcore.realm.impl.network.c2s

import io.github.solusmods.eternalcore.realm.api.RealmAPI
import io.github.solusmods.eternalcore.realm.impl.RealmStorage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object ClientAccess {

    fun handle(packet: SyncRealmStoragePayload, player: ServerPlayer) {
        player.let { serverPlayer ->
            serverPlayer.`eternalCore$getStorageOptional`(RealmStorage.key!!)
                .ifPresent { storage ->
                    packet.data?.let { data ->
                        storage.load(data)
                    }
                }
        }
    }

    fun handle(packet: RequestRealmBreakthroughPacket, player: Player?) {
        player ?: return

        val storage = RealmAPI.getRealmFrom(player) ?: return
        val realmOptional = storage.getRealmOptional()

        if (realmOptional.isEmpty) return

        val realm = RealmAPI.realmRegistry?.get(packet.realm) ?: return
        val realmInstance = realmOptional.get()

        val nextBreakthroughs = realmInstance.getNextBreakthroughs(player)
        if (nextBreakthroughs?.contains(realm) != true) {
            return
        }

        storage.breakthroughRealm(realm)
    }
}