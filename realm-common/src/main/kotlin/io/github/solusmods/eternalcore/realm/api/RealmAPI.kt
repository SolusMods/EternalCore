package io.github.solusmods.eternalcore.realm.api

import dev.architectury.platform.Platform
import dev.architectury.registry.registries.Registrar
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.realm.impl.RealmRegistry
import io.github.solusmods.eternalcore.realm.impl.RealmStorage
import io.github.solusmods.eternalcore.realm.impl.network.InternalRealmPacketActions
import io.github.solusmods.eternalcore.storage.api.Storage
import lombok.AccessLevel
import lombok.NoArgsConstructor
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import java.util.Optional

object RealmAPI {

    /**
     * This Method returns the [Realm] Registry.
     * It can be used to load [Realm]s from the Registry.
     */
    @JvmField
    var realmRegistry: Registrar<Realm> = RealmRegistry.REALMS

    /**
     * This Method returns the Registry Key of the [RealmRegistry].
     * It can be used to create [dev.architectury.registry.registries.DeferredRegister] instances
     */
    @JvmField
    val realmRegistryKey: ResourceKey<Registry<Realm>> = RealmRegistry.KEY


    /**
     * Can be used to load the [RealmStorage] from an [LivingEntity].
     */
    @JvmStatic
    fun getRealmFrom(entity: LivingEntity): Realms? {
        return entity.getStorage(RealmStorage.key)
    }

    /**
     * Can be used to load the [RealmStorage] from an [LivingEntity].
     */
    @JvmStatic
    fun getReachedRealmsFrom(entity: LivingEntity): IReachedRealms? {
        return entity.getStorage(RealmStorage.key)
    }

    @JvmStatic
    fun getStorageOptional(entity: LivingEntity): Optional<RealmStorage> {
        return entity.getStorageOptional(RealmStorage.key)
    }

    /**
     * Send [InternalRealmPacketActions.sendRealmBreakthroughPacket] with a DistExecutor on client side.
     * Used when player break into a stage.
     *
     * @see InternalRealmPacketActions.sendRealmBreakthroughPacket
     */
    @JvmStatic
    fun realmBreakthroughPacket(location: ResourceLocation?) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalRealmPacketActions.sendRealmBreakthroughPacket(location)
        }
    }
}
