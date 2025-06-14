package io.github.solusmods.eternalcore.realm.api

import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import java.util.Optional

interface Realms {
    val realm: RealmInstance?

    fun getRealmOptional(): Optional<RealmInstance>

    fun setRealm(realmId: ResourceLocation, notify: Boolean): Boolean {
        return setRealm(realmId, notify, null)
    }

    fun setRealm(realmId: ResourceLocation, notify: Boolean, component: MutableComponent?): Boolean {
        val realm = RealmAPI.realmRegistry.get(realmId) ?: return false
        return setRealm(realm.createDefaultInstance(), false, notify, component)
    }

    fun setRealm(realm: Realm, notify: Boolean): Boolean {
        return setRealm(realm, notify, null)
    }

    fun setRealm(realm: Realm, notify: Boolean, component: MutableComponent?): Boolean {
        return setRealm(realm.createDefaultInstance(), false, notify, component)
    }

    fun setRealm(realmInstance: RealmInstance, breakthrough: Boolean, notify: Boolean): Boolean {
        return setRealm(realmInstance, breakthrough, notify, null)
    }

    fun setRealm(
        realmInstance: RealmInstance,
        breakthrough: Boolean,
        notify: Boolean,
        component: MutableComponent?
    ): Boolean


    
    fun breakthroughRealm(realmId: ResourceLocation, component: MutableComponent? = null): Boolean {
        val realm = RealmAPI.realmRegistry.get(realmId) ?: return false
        return setRealm(realm.createDefaultInstance(), breakthrough = true, notify = false)
    }

    
    fun breakthroughRealm(realm: Realm, component: MutableComponent? = null): Boolean {
        return setRealm(realm.createDefaultInstance(), breakthrough = true, notify = false, component = component)
    }

    
    fun breakthroughRealm(realmInstance: RealmInstance, component: MutableComponent? = null): Boolean {
        return setRealm(realmInstance, breakthrough = true, notify = false, component = component)
    }

    fun markDirty()

    fun sync()
}
