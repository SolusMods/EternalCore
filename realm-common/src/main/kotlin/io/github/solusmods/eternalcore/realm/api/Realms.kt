package io.github.solusmods.eternalcore.realm.api

import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import java.util.*

interface Realms {
    val realm: Optional<RealmInstance?>?

    fun setRealm(realmId: ResourceLocation, notify: Boolean): Boolean {
        return setRealm(realmId, notify, null)
    }

    fun setRealm(realmId: ResourceLocation, notify: Boolean?, component: MutableComponent?): Boolean {
        val realm = RealmAPI.realmRegistry!!.get(realmId)
        if (realm == null) return false
        return setRealm(realm.createDefaultInstance(), false, notify!!, component)
    }

    fun setRealm(realm: Realm, notify: Boolean): Boolean {
        return setRealm(realm, notify, null)
    }

    fun setRealm(realm: Realm, notify: Boolean?, component: MutableComponent?): Boolean {
        return setRealm(realm.createDefaultInstance(), false, notify!!, component)
    }

    fun setRealm(instance: RealmInstance, breakthrough: Boolean?, notify: Boolean): Boolean {
        return setRealm(instance, breakthrough!!, notify, null)
    }

    fun setRealm(
        instance: RealmInstance,
        breakthrough: Boolean?,
        notify: Boolean?,
        component: MutableComponent?
    ): Boolean


    
    fun breakthroughRealm(realmId: ResourceLocation, component: MutableComponent? = null): Boolean {
        val realm = RealmAPI.realmRegistry!!.get(realmId)
        if (realm == null) return false
        return setRealm(realm.createDefaultInstance(), true, false)
    }

    
    fun breakthroughRealm(realm: Realm, component: MutableComponent? = null): Boolean {
        return setRealm(realm.createDefaultInstance(), true, false, component)
    }

    
    fun breakthroughRealm(breakthrough: RealmInstance, component: MutableComponent? = null): Boolean {
        return setRealm(breakthrough, true, false, component)
    }

    fun markDirty()

    fun sync()
}
