package io.github.solusmods.eternalcore.storage.impl

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.saveddata.SavedData

class StoragePersistentState(private val storage: CombinedStorage) : SavedData() {

    override fun isDirty(): Boolean = true

    override fun save(tag: CompoundTag, provider: HolderLookup.Provider): CompoundTag {
        return storage.toNBT()
    }

    companion object {
        @JvmField
        val LOADING: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }

        @JvmStatic
        fun getFactory(storage: CombinedStorage): Factory<StoragePersistentState> {
            return Factory(
                { StoragePersistentState(storage) },
                { tag, _ -> fromNBT(storage, tag!!) },
                DataFixTypes.LEVEL
            )
        }

        fun fromNBT(storage: CombinedStorage, tag: CompoundTag): StoragePersistentState {
            return StoragePersistentState(storage).apply {
                storage.handleUpdatePacket(tag)
            }
        }
    }
}
