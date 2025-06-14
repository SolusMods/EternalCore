package io.github.solusmods.eternalcore.realm.api

import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

interface IReachedRealms {
    val reachedRealms: MutableMap<ResourceLocation, RealmInstance>


    fun addRealm(realmId: ResourceLocation, teleportToSpawn: Boolean?, component: MutableComponent? = null): Boolean {
        val realm = RealmAPI.realmRegistry!!.get(realmId)
        if (realm == null) return false
        return addRealm(realm.createDefaultInstance(), false, teleportToSpawn!!, component)
    }

    fun addRealm(realm: Realm, teleportToSpawn: Boolean?, component: MutableComponent? = null): Boolean {
        return addRealm(realm.createDefaultInstance(), false, teleportToSpawn!!, component)
    }

    fun addRealm(instance: RealmInstance, breakthrough: Boolean, teleportToSpawn: Boolean): Boolean {
        return addRealm(instance, breakthrough, teleportToSpawn, null)
    }

    fun addRealm(
        instance: RealmInstance,
        breakthrough: Boolean,
        teleportToSpawn: Boolean,
        component: MutableComponent?
    ): Boolean

    fun markDirty()

    fun sync()
}
