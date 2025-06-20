package io.github.solusmods.eternalcore.stage.impl

import dev.architectury.event.EventResult
import io.github.solusmods.eternalcore.entity.api.EntityEvents
import io.github.solusmods.eternalcore.network.api.util.Changeable
import io.github.solusmods.eternalcore.stage.EternalCoreStage
import io.github.solusmods.eternalcore.stage.api.*
import io.github.solusmods.eternalcore.stage.impl.network.InternalStagePacketActions
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
import net.minecraft.world.entity.player.Player
import java.util.*

/**
 * Система зберігання та управління стадіями гравців.
 *
 *
 * StageStorage відповідає за зберігання поточної стадії гравця та всіх досягнутих стадій.
 * Реалізує інтерфейси [Stages] та [IReachedStages] для забезпечення функціональності
 * роботи зі стадіями.
 *
 *
 *
 * Клас підтримує серіалізацію/десеріалізацію даних через NBT, а також подієву систему
 * для повідомлення про зміни в стадіях.
 *
 */
@Suppress("UNCHECKED_CAST")
open class StageStorage
protected constructor(holder: StorageHolder) : Storage(holder), Stages, IReachedStages {
    /** Поточна активна стадія гравця  */
    override var stage: StageInstance? = null

    override fun getStageOptional(): Optional<StageInstance> {
        return Optional.ofNullable(stage)
    }

    /** Колекція всіх досягнутих гравцем стадій  */
    override val reachedStages: MutableMap<ResourceLocation?, StageInstance> =
        HashMap<ResourceLocation?, StageInstance>()


    /**
     * {@inheritDoc}
     *
     * Додає нову досягнуту стадію та викликає відповідні події.
     *
     * @param instance Стадія для додавання
     * @param breakthrough Чи є ця стадія проривом
     * @param notify Чи потрібно сповіщати гравця
     * @param component Компонент повідомлення для гравця (може бути null)
     * @return true, якщо стадія була успішно додана, false - якщо була скасована подією
     */
    override fun addStage(
        instance: StageInstance,
        breakthrough: Boolean?,
        notify: Boolean?,
        component: MutableComponent?
    ): Boolean {
        val stageMessage = Changeable.of(component)
        val notify: Changeable<Boolean?>? = Changeable.of(notify)
        val result: EventResult? = StageEvents.Companion.REACH_STAGE.invoker().reach(
            instance,
            this.owner, breakthrough, notify, stageMessage
        )
        if (result!!.isFalse) return false

        val owner = this.owner
        if (stageMessage.isPresent) this.owner.sendSystemMessage(stageMessage.get()!!)
        instance.markDirty()
        instance.onReach(owner)
        reachedStages.put(instance.stageId, instance)
        markDirty()
        return true
    }


    /**
     * {@inheritDoc}
     *
     * Встановлює нову активну стадію та викликає відповідні події.
     *
     * @param stageInstance Нова стадія для встановлення
     * @param advancement Чи є це просуванням вперед
     * @param notify Чи потрібно сповіщати гравця
     * @param component Компонент повідомлення для гравця (може бути null)
     * @return true, якщо стадія була успішно встановлена, false - якщо була скасована подією
     */
    override fun setStage(
        stageInstance: StageInstance,
        advancement: Boolean?,
        notify: Boolean?,
        component: MutableComponent?
    ): Boolean {
        val instance = this.stage
        val realmMessage = Changeable.of(component)
        val notifyPlayer: Changeable<Boolean?>? = Changeable.of(notify)
        val result: EventResult? = StageEvents.Companion.SET_STAGE.invoker().set(
            instance,
            this.owner, stageInstance, advancement, notifyPlayer, realmMessage
        )
        if (result!!.isFalse) return false

        val owner = this.owner

        if (realmMessage.isPresent) this.owner.sendSystemMessage(realmMessage.get()!!)
        stageInstance.markDirty()
        stageInstance.onSet(owner)
        this.stage = stageInstance
        markDirty()
        return true
    }

    /**
     * {@inheritDoc}
     *
     * Зберігає дані стадій у NBT.
     *
     * @param data NBT тег для зберігання даних
     */
    override fun save(data: CompoundTag) {
        if (!getStageOptional().isEmpty)
            data.put(STAGE_KEY, this.stage!!.toNBT())
        val stageTag = ListTag()
        reachedStages.values.forEach { instance: StageInstance ->
            stageTag.add(instance.toNBT())
            instance.resetDirty()
        }
        data.put(REACHED_STAGES_KEY, stageTag)
    }

    /**
     * {@inheritDoc}
     *
     * Завантажує дані стадій з NBT.
     *
     * @param data NBT тег для завантаження даних
     */
    override fun load(data: CompoundTag) {
        if (data.contains(STAGE_KEY, 10)) {
            stage = StageInstance.fromNBT(data.getCompound(STAGE_KEY))
        }
        for (tag in data.getList(REACHED_STAGES_KEY, Tag.TAG_COMPOUND.toInt())) {
            try {
                val instance: StageInstance = StageInstance.fromNBT(tag as CompoundTag)
                this.reachedStages.put(instance.stageId, instance)
            } catch (e: Exception) {
                EternalCoreStorage.LOG.error("Failed to load stage from NBT", e)
            }
        }
    }

    protected val owner: LivingEntity
        /**
         * Отримує власника сховища стадій.
         *
         * @return Живу сутність, що володіє цим сховищем стадій
         */
        get() = this.holder as LivingEntity


    override fun sync() {
        val data = CompoundTag()
        saveOutdated(data)
        InternalStagePacketActions.sendSyncStoragePayload(data)
    }

    companion object {
        /** Ключ для зберігання поточної стадії у NBT  */
        private const val STAGE_KEY = "stage_key"

        /** Ключ для зберігання колекції досягнутих стадій у NBT  */
        private const val REACHED_STAGES_KEY = "reached_stages_key"

        /** Ідентифікатор сховища стадій  */
        val ID: ResourceLocation = EternalCoreStage.create("stage_storage")

        /** Ключ сховища для доступу до даних StageStorage  */
        var key: StorageKey<StageStorage>? = null

        /**
         * Ініціалізує систему сховища стадій.
         *
         *
         * Реєструє сховище в системі, налаштовує обробники подій для:
         *
         *  * Тікових оновлень стадій
         *  * Оновлення ефектів стадій
         *
         *
         */
        fun init() {
            StorageEvents.REGISTER_ENTITY_STORAGE.register { registry ->
                key = registry.register(
                    ID,
                    StageStorage::class.java, { obj -> LivingEntity::class.java.isInstance(obj) },
                    { holder: Entity -> StageStorage(holder) })
            }
        }
    }
}