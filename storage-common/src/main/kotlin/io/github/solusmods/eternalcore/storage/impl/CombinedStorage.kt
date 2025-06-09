package io.github.solusmods.eternalcore.storage.impl

import io.github.solusmods.eternalcore.storage.EternalCoreStorage
import io.github.solusmods.eternalcore.storage.api.Storage
import io.github.solusmods.eternalcore.storage.api.StorageHolder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation

/**
 * Комбіноване сховище, яке містить і керує кількома окремими сховищами ([Storage]).
 */
class CombinedStorage(val holder: StorageHolder) {

    private val storages = mutableMapOf<ResourceLocation, Storage>()

    fun toNBT(): CompoundTag = CompoundTag().apply {
        val entriesTag = ListTag()
        storages.forEach { (id, storage) ->
            val entryTag = CompoundTag().apply {
                putString(STORAGE_ID_KEY, id.toString())
                storage.save(this)
            }
            entriesTag.add(entryTag)
        }
        put(STORAGE_LIST_KEY, entriesTag)
    }

    fun load(tag: CompoundTag) {
        val entriesTag = tag.getList(STORAGE_LIST_KEY, Tag.TAG_COMPOUND.toInt())
        entriesTag.forEach { tagElement ->
            val entryTag = tagElement as CompoundTag
            val id = ResourceLocation.parse(entryTag.getString(STORAGE_ID_KEY))
            val storage = StorageManager.constructStorageFor(holder.getStorageType(), id, holder)

            if (storage == null) {
                EternalCoreStorage.LOG.warn(
                    "Failed to construct storage for id {}. All information about this storage will be dropped!", id
                )
                return@forEach
            }

            storage.load(entryTag)
            storages[id] = storage
        }
    }

    fun handleUpdatePacket(tag: CompoundTag) {
        val entriesTag = tag.getList(STORAGE_LIST_KEY, Tag.TAG_COMPOUND.toInt())
        entriesTag.forEach { tagElement ->
            val entryTag = tagElement as CompoundTag
            val id = ResourceLocation.tryParse(entryTag.getString(STORAGE_ID_KEY)) ?: return@forEach
            val storage = storages[id]

            if (storage == null) {
                EternalCoreStorage.LOG.warn(
                    "Failed to find storage for id {}. All information about this storage will be dropped!", id
                )
                return@forEach
            }

            storage.loadUpdate(entryTag)
        }
    }

    fun add(id: ResourceLocation, storage: Storage) {
        storages[id] = storage
    }

    operator fun get(id: ResourceLocation): Storage? = storages[id]

    fun createUpdatePacket(clean: Boolean): CompoundTag = CompoundTag().apply {
        val entriesTag = ListTag()
        storages.forEach { (id, storage) ->
            if (!storage.isDirty) return@forEach

            val entryTag = CompoundTag().apply {
                putString(STORAGE_ID_KEY, id.toString())
                storage.saveOutdated(this)
            }

            entriesTag.add(entryTag)
            if (clean) storage.clearDirty()
        }
        put(STORAGE_LIST_KEY, entriesTag)
    }

    val isDirty: Boolean
        get() = storages.values.any { it.isDirty }

    companion object {
        private const val STORAGE_LIST_KEY = "eternalCore_registry_storage"
        private const val STORAGE_ID_KEY = "eternalCore_registry_storage_id"
    }
}
