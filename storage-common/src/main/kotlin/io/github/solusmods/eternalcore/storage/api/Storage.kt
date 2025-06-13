package io.github.solusmods.eternalcore.storage.api

import net.minecraft.nbt.CompoundTag

/**
 * Abstract base class for storage implementations that handle data persistence
 * and synchronization with support for dirty state tracking.
 */
abstract class Storage protected constructor(
    protected val holder: StorageHolder?
) {
    /**
     * Indicates whether the storage has been modified and needs synchronization.
     * Automatically set to true when storage is created or modified.
     */
    var isDirty: Boolean = true
        private set

    /**
     * Saves data to the entity by writing all information to the given tag.
     *
     * @param data The compound tag to write data to
     * @see load
     */
    abstract fun save(data: CompoundTag)

    /**
     * Loads data from the entity by reading all information from the given tag.
     *
     * @param data The compound tag to read data from
     * @see save
     */
    abstract fun load(data: CompoundTag)

    /**
     * Creates update packets with optimized data.
     * Override this method to customize packet data optimization.
     * Default implementation delegates to [save].
     *
     * @param data The compound tag to write update data to
     * @see loadUpdate
     */
    open fun saveOutdated(data: CompoundTag) {
        save(data)
    }

    /**
     * Applies update packets by loading the provided data.
     *
     * @param data The compound tag containing update data
     * @see saveOutdated
     */
    open fun loadUpdate(data: CompoundTag) {
        load(data)
    }

    /**
     * Marks the storage as dirty, indicating it needs synchronization.
     */
    open fun markDirty() {
        isDirty = true
    }

    /**
     * Clears the dirty flag, indicating the storage is synchronized.
     */
    fun clearDirty() {
        isDirty = false
    }
}