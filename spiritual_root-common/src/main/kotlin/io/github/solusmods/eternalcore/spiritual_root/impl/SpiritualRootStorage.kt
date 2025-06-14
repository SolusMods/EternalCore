package io.github.solusmods.eternalcore.spiritual_root.impl

import dev.architectury.event.CompoundEventResult
import dev.architectury.event.EventResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootEvents
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRootInstance
import io.github.solusmods.eternalcore.spiritual_root.api.SpiritualRoots
import io.github.solusmods.eternalcore.spiritual_root.impl.network.InternalSpiritualRootPacketActions
import io.github.solusmods.eternalcore.storage.EternalCoreStorage
import io.github.solusmods.eternalcore.storage.api.Storage
import io.github.solusmods.eternalcore.storage.api.StorageEvents
import io.github.solusmods.eternalcore.storage.api.StorageHolder
import io.github.solusmods.eternalcore.storage.api.StorageKey
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import java.util.function.BiConsumer

/**
 * Клас для зберігання та управління духовними коренями сутності.
 * Реалізує інтерфейс [SpiritualRoots] та розширює [Storage].
 */
@Suppress("unchecked_cast")
open class SpiritualRootStorage(holder: StorageHolder) : Storage(holder), SpiritualRoots {
    override val spiritualRoots: MutableMap<ResourceLocation, SpiritualRootInstance> =
        mutableMapOf()

    private var hasRemovedRoots = false

    /**
     * Зберігає стан духовних коренів у NBT.
     *
     * @param data CompoundTag для збереження даних
     */
    override fun save(data: CompoundTag) {
        val elementsTag = ListTag()
        spiritualRoots.values.forEach { instance: SpiritualRootInstance ->
            elementsTag.add(instance.toNBT())
            instance.resetDirty()
        }
        data.put(SPIRITUAL_ROOTS_KEY, elementsTag)
    }

    /**
     * Завантажує стан духовних коренів з NBT.
     *
     * @param data CompoundTag з даними для завантаження
     */
    override fun load(data: CompoundTag) {
        if (data.contains("resetExistingData")) {
            this.spiritualRoots.clear()
        }
        for (tag in data.getList(SPIRITUAL_ROOTS_KEY, Tag.TAG_COMPOUND.toInt())) {
            try {
                val instance: SpiritualRootInstance = SpiritualRootInstance.fromNBT(tag as CompoundTag)
                this.spiritualRoots.put(instance.spiritualRootId, instance)
            } catch (e: Exception) {
                EternalCoreStorage.LOG.error("Failed to load root instance from NBT", e)
            }
        }
    }

    override val gainedRoots: MutableCollection<SpiritualRootInstance>
        get() {return this.spiritualRoots.values }


    override fun saveOutdated(data: CompoundTag) {
        if (this.hasRemovedRoots) {
            this.hasRemovedRoots = false
            data.putBoolean("resetExistingData", true)
            super.saveOutdated(data)
        } else {
            val skillList = ListTag()
            for (instance in this.spiritualRoots.values) {
                if (!instance.dirty) continue
                skillList.add(instance.toNBT())
                instance.resetDirty()
            }
            data.put(SPIRITUAL_ROOTS_KEY, skillList)
        }
    }

    protected val owner: LivingEntity
        /**
         * Отримує власника цього сховища.
         *
         * @return Жива сутність-власник сховища
         */
        get() = this.holder as LivingEntity


    /**
     * Додає новий духовний корінь до сутності.
     *
     * @param instance Екземпляр духовного кореня для додавання
     * @param advance Чи потрібно застосовувати просунуті ефекти
     * @param notify Чи потрібно сповіщати гравця
     * @param component Компонент повідомлення для сповіщення (може бути null)
     * @return true якщо корінь успішно додано, false якщо додавання скасовано
     */
    override fun addSpiritualRoot(
        instance: SpiritualRootInstance,
        advance: Boolean,
        notify: Boolean,
        component: MutableComponent?
    ): Boolean {
        if (this.spiritualRoots.containsKey(instance.spiritualRootId)) {
            EternalCoreStorage.LOG.debug("Tried to register a deduplicate of {}.", instance.spiritualRoot)
            return false
        }


        val rootMessage = Changeable.of(component)
        val notifyPlayer: Changeable<Boolean?>? = Changeable.of(notify)
        val result: CompoundEventResult<SpiritualRootInstance> = SpiritualRootEvents.Companion.ADD.invoker().add(
            instance,
            this.owner, advance, notifyPlayer, rootMessage
        )
        if (result.isFalse) return false
        val owner = this.owner
        if (rootMessage.isPresent) this.owner.sendSystemMessage(rootMessage.get()!!)
        val newInstance = result.`object`()
        newInstance.markDirty()
        newInstance.onAdd(owner)
        spiritualRoots.put(newInstance.spiritualRootId, newInstance)
        markDirty()
        return true
    }

    /**
     * Updates a root instance and optionally synchronizes the change across the network.
     *
     *
     *
     * @param updatedInstance The instance to update
     * @param sync            If true, synchronizes the change to all clients/server
     */
    override fun updateRoot(updatedInstance: SpiritualRootInstance, sync: Boolean) {
        updatedInstance.markDirty()
        spiritualRoots.put(updatedInstance.spiritualRootId, updatedInstance)
        if (sync) markDirty()
    }

    override fun forEachRoot(biConsumer: BiConsumer<SpiritualRootStorage, SpiritualRootInstance>) {
        gainedRoots.forEach { instance -> biConsumer.accept(this, instance) }
        markDirty()
    }

    override fun forgetRoot(spiritualRootId: ResourceLocation, component: MutableComponent?) {
        if (!this.spiritualRoots.containsKey(spiritualRootId)) return
        val instance: SpiritualRootInstance = this.spiritualRoots[spiritualRootId]!!

        val forgetMessage = Changeable.of(component)
        val result: EventResult? = SpiritualRootEvents.Companion.FORGET_SPIRITUAL_ROOT.invoker().forget(
            instance,
            this.owner, forgetMessage
        )
        if (result!!.isFalse) return

        if (forgetMessage.isPresent) this.owner.sendSystemMessage(forgetMessage.get()!!)
        instance.markDirty()

        this.gainedRoots.remove(instance)
        this.hasRemovedRoots = true
        markDirty()
    }

    override fun sync() {
        val data = CompoundTag()
        saveOutdated(data)
        InternalSpiritualRootPacketActions.sendSyncStoragePayload(data)
    }

    companion object {
        private const val SPIRITUAL_ROOTS_KEY = "spiritual_roots_key"
        val ID: ResourceLocation = EternalCoreSpiritualRoot.create("spiritual_root_storage")

        var key: StorageKey<SpiritualRootStorage>? = null

        /**
         * Ініціалізує систему зберігання духовних коренів.
         * Реєструє сховище для гравців.
         */
        fun init() {
            StorageEvents.REGISTER_ENTITY_STORAGE.register { registry ->
                key = registry.register(
                    ID,
                    SpiritualRootStorage::class.java, { obj -> LivingEntity::class.java.isInstance(obj) },
                    { holder: Entity -> SpiritualRootStorage(holder) })
            }
            SpiritualRootEvents.ADD.register { instance: SpiritualRootInstance, living: LivingEntity, advancement: Boolean?, notifyPlayer: Changeable<Boolean?>?, rootMessage: Changeable<MutableComponent?>? ->
                CompoundEventResult.interruptTrue(
                    instance
                )
            }
        }
    }
}
