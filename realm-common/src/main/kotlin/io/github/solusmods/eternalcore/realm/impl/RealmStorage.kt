package io.github.solusmods.eternalcore.realm.impl

import dev.architectury.event.EventResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import io.github.solusmods.eternalcore.realm.EternalCoreRealm
import io.github.solusmods.eternalcore.realm.api.IReachedRealms
import io.github.solusmods.eternalcore.realm.api.RealEvents
import io.github.solusmods.eternalcore.realm.api.RealmInstance
import io.github.solusmods.eternalcore.realm.api.Realms
import io.github.solusmods.eternalcore.realm.impl.network.InternalRealmPacketActions
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
import java.util.*
import java.util.function.Consumer

/**
 * Сховище, що керує світами (realms) для гравців.
 *
 *
 * Цей клас реалізує інтерфейси [Realms] та [IReachedRealms], дозволяючи
 * зберігати, керувати та синхронізувати інформацію про досягнуті світи гравця
 * та його поточний активний світ. Сховище зберігає дані у NBT форматі та може
 * бути серіалізовано для збереження або мережевої синхронізації.
 *
 *
 *
 * Клас також обробляє події зміни та досягнення світів через систему подій EternalCore.
 *
 */
@Suppress("unchecked_cast")
open class RealmStorage protected constructor(holder: StorageHolder) : Storage(holder), Realms, IReachedRealms {
/**
 * Створює нове сховище світів для вказаного власника.
 *
 * @param holder Власник цього сховища
 */
    /** Колекція світів, які досягнув гравець  */
    override val reachedRealms: MutableMap<ResourceLocation?, RealmInstance> = HashMap<ResourceLocation?, RealmInstance>()

    /** Поточний активний світ гравця  */
    override var realm: RealmInstance? = null

    override fun getRealm(): Optional<RealmInstance>{
        return Optional.ofNullable(realm)
    }
    

    /**
     * Додає новий досягнутий світ до колекції гравця.
     *
     *
     * Цей метод викликає подію [RealEvents.REACH_REALM], яка може бути скасована
     * обробниками подій. Якщо світ успішно додано, викликається [RealmInstance.onReach]
     * для ініціалізації ефектів світу.
     *
     *
     * @param instance Екземпляр світу для додавання
     * @param breakthrough Чи є це проривом (breakthrough)
     * @param teleportToSpawn Чи повідомляти гравця про досягнення
     * @param component Компонент повідомлення (може бути null)
     * @return true, якщо світ було успішно додано, false в іншому випадку
     */
    override fun addRealm(
        instance: RealmInstance,
        breakthrough: Boolean,
        teleportToSpawn: Boolean,
        component: MutableComponent?
    ): Boolean {
        val realmMessage: Changeable<MutableComponent>? = Changeable.of(component) as Changeable<MutableComponent>?
        val notify: Changeable<Boolean>? = Changeable.of(teleportToSpawn) as Changeable<Boolean>?
        val result: EventResult = RealEvents.Companion.REACH_REALM.invoker().reach(
            instance,
            this.owner, breakthrough, notify, realmMessage
        )
        if (result.isFalse) return false

        val owner = this.owner
        if (realmMessage!!.isPresent) this.owner.sendSystemMessage(realmMessage.get())
        instance.markDirty()
        instance.onReach(owner)
        reachedRealms.put(instance.realmId, instance)
        markDirty()
        return true
    }

    /**
     * Встановлює активний світ для гравця.
     *
     *
     * Цей метод викликає подію [RealEvents.SET_REALM], яка може бути скасована
     * обробниками подій. Якщо світ змінюється, попередній світ видаляє свої
     * модифікатори атрибутів, а новий світ ініціалізується через [RealmInstance.onSet].
     *
     *
     * @param realmInstance Екземпляр світу для встановлення
     * @param breakthrough Чи є це проривом (breakthrough)
     * @param notify Чи повідомляти гравця про зміну
     * @param component Компонент повідомлення (може бути null)
     * @return true, якщо світ було успішно встановлено, false в іншому випадку
     */
    override fun setRealm(
        realmInstance: RealmInstance,
        breakthrough: Boolean?,
        notify: Boolean?,
        component: MutableComponent?
    ): Boolean {
        val instance = this.realm
        val realmMessage: Changeable<MutableComponent>? = Changeable.of(component) as Changeable<MutableComponent>?
        val notify: Changeable<Boolean>? = Changeable.of(notify) as Changeable<Boolean>?
        val result: EventResult = RealEvents.Companion.SET_REALM.invoker().set(
            instance,
            this.owner, realmInstance, breakthrough!!, notify, realmMessage
        )
        if (result.isFalse) return false

        val owner = this.owner


        if (realmMessage!!.isPresent) this.owner.sendSystemMessage(realmMessage.get()!!)
        realmInstance.markDirty()
        realmInstance.onSet(owner)
        this.realm = realmInstance
        markDirty()
        return true
    }

    /**
     * Зберігає стан сховища в NBT формат.
     *
     * @param data Тег, в який буде збережено дані
     */
    override fun save(data: CompoundTag) {
        val elementsTag = ListTag()
        reachedRealms.values.forEach(Consumer { instance: RealmInstance ->
            elementsTag.add(instance.toNBT())
            instance.resetDirty()
        })
        data.put(REACHED_REALMS_KEY, elementsTag)
        if (!getRealm().isEmpty)
            data.put(REALM_KEY, realm!!.toNBT())
    }

    /**
     * Завантажує стан сховища з NBT формату.
     *
     * @param data Тег, з якого будуть завантажені дані
     */
    override fun load(data: CompoundTag) {
        if (data.contains(REALM_KEY)) {
            realm = RealmInstance.Companion.fromNBT(data.getCompound(REALM_KEY))
        }
        for (tag in data.getList(REACHED_REALMS_KEY, Tag.TAG_COMPOUND.toInt())) {
            try {
                val instance: RealmInstance = RealmInstance.Companion.fromNBT(tag as CompoundTag?)
                this.reachedRealms.put(instance.realmId, instance)
            } catch (e: Exception) {
                EternalCoreStorage.LOG.error("Failed to load realm from NBT", e)
            }
        }
    }

    protected val owner: LivingEntity
        /**
         * Отримує власника сховища як живу сутність.
         *
         * @return Власник сховища як LivingEntity
         */
        get() = this.holder as LivingEntity

    override fun sync() {
        val data = CompoundTag()
        saveOutdated(data)
        InternalRealmPacketActions.sendSyncStoragePayload(data)
    }

    companion object {
        /** Ключ для поточного активного світу в NBT  */
        private const val REALM_KEY = "realm_key"

        /** Ключ для колекції досягнутих світів у NBT  */
        private const val REACHED_REALMS_KEY = "reached_realms_key"

        /** Унікальний ідентифікатор цього типу сховища  */
        val ID: ResourceLocation = EternalCoreRealm.create("realm_storage")

        /** Ключ для доступу до цього сховища  */

        var key: StorageKey<RealmStorage>? = null

        /**
         * Ініціалізує систему сховища світів, реєструючи його в системі сховищ EternalCore.
         *
         *
         * Цей метод повинен бути викликаний один раз під час ініціалізації мода.
         *
         */
        fun init() {
            StorageEvents.REGISTER_ENTITY_STORAGE.register(StorageEvents.RegisterStorage { registry ->
                key = registry.register(
                    ID,
                    RealmStorage::class.java, { obj -> LivingEntity::class.java.isInstance(obj) },
                    { holder: Entity -> RealmStorage(holder) })
            })
        }
    }
}