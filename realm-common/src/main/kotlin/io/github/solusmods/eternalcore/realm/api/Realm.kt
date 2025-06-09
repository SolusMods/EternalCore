package io.github.solusmods.eternalcore.realm.api

import io.github.solusmods.eternalcore.realm.ModuleConstants
import io.github.solusmods.eternalcore.stage.api.Stage
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import java.util.function.BiConsumer

/**
 * Абстрактний клас, що представляє Реалм  у системі EternalCore.
 *
 *
 * Реалм  визначає базові атрибути, здібності, механіки прориву (breakthrough)
 * та стадії розвитку для сутностей. Кожен Реалм  має унікальні характеристики,
 * що впливають на ігрову механіку.
 *
 *
 *
 * Конкретні реалізації Реалмів  мають визначати базові атрибути, можливі шляхи
 * прориву та стадії розвитку. Реалми  організовані в ієрархію, де кожен наступний
 * Реалм  досягається через прорив і надає більші здібності.
 *
 */
abstract class Realm(
    /**
     * Повертає тип Реалму.
     *
     *
     * Тип Реалму визначає його рівень у ієрархії Реалмів.
     *
     *
     * @see RealmInstance.getType
     */
    val type: Type
) {
    /**
     * Мапа модифікаторів атрибутів, що застосовуються до сутності в цьому Реалмі
     */
    private val attributeModifiers: MutableMap<Holder<Attribute?>?, AttributeTemplate?> =
        Object2ObjectOpenHashMap<Holder<Attribute?>?, AttributeTemplate?>()

    /**
     * Створює новий екземпляр Реалму з базовими налаштуваннями.
     *
     *
     * Цей метод використовується для створення екземпляра Реалму з його основними
     * характеристиками для призначення сутності.
     *
     *
     * @return Новий екземпляр Реалму
     */
    fun createDefaultInstance(): RealmInstance {
        return RealmInstance(this)
    }

    /**
     * Повертає базове здоров'я для цього Реалму .
     *
     *
     * Це значення визначає базовий максимум здоров'я, що сутність отримує
     * перебуваючи в цьому Реалмі .
     *
     *
     * @return Базове значення здоров'я
     * @see RealmInstance.getBaseHealth
     */
    abstract val baseHealth: Double

    /**
     * Повертає пару значень мінімальної та максимальної кількості Ці для цього Реалму .
     *
     *
     * Ці значення визначають діапазон енергії Ці, доступний для сутностей у цьому Реалмі .
     *
     *
     * @return Пара (мінімум, максимум) значень Ці
     * @see RealmInstance.getBaseQiRange
     */
    abstract val baseQiRange: Pair<Float?, Float?>?

    /**
     * Повертає базову силу атаки для цього Реалму .
     *
     *
     * Це значення визначає базову шкоду, яку сутність завдає при атаці.
     *
     *
     * @return Базове значення сили атаки
     * @see RealmInstance.getBaseAttackDamage
     */
    abstract val baseAttackDamage: Double

    /**
     * Повертає базову швидкість атаки для цього Реалму .
     *
     *
     * Це значення визначає, як швидко сутність може атакувати.
     *
     *
     * @return Базове значення швидкості атаки
     * @see RealmInstance.getBaseAttackSpeed
     */
    abstract val baseAttackSpeed: Double

    /**
     * Повертає опір відкиданню для цього Реалму .
     *
     *
     * Це значення визначає, наскільки сутність стійка до відкидання від атак.
     *
     *
     * @return Значення опору відкиданню
     * @see RealmInstance.getKnockBackResistance
     */
    abstract val knockBackResistance: Double

    /**
     * Повертає висоту стрибка для цього Реалму .
     *
     *
     * Це значення визначає, наскільки високо сутність може стрибати.
     *
     *
     * @return Значення висоти стрибка
     * @see RealmInstance.getJumpHeight
     */
    abstract val jumpHeight: Double

    /**
     * Повертає швидкість руху для цього Реалму .
     *
     *
     * Це значення визначає, як швидко сутність може рухатися.
     *
     *
     * @return Значення швидкості руху
     * @see RealmInstance.getMovementSpeed
     */
    abstract val movementSpeed: Double


    val sprintSpeed: Double
        /**
         * Повертає швидкість бігу для цього Реалму .
         *
         *
         * За замовчуванням це значення розраховується як 130% від швидкості руху.
         *
         *
         * @return Значення швидкості бігу
         * @see RealmInstance.getSprintSpeed
         */
        get() = this.movementSpeed * 1.3

    val minBaseQi: Float
        /**
         * Повертає мінімальне значення Ці для цього Реалму .
         *
         *
         * Це значення витягується з пари значень, що повертаються методом [.getBaseQiRange].
         *
         *
         * @return Мінімальне значення Ці
         * @see RealmInstance.getMinBaseQi
         */
        get() = this.baseQiRange!!.first!!

    val maxBaseQi: Float
        /**
         * Повертає максимальне значення Ці для цього Реалму .
         *
         *
         * Це значення витягується з пари значень, що повертаються методом [.getBaseQiRange].
         *
         *
         * @return Максимальне значення Ці
         * @see RealmInstance.getMaxBaseQi
         */
        get() = this.baseQiRange!!.second!!

    val coefficient: Double
        /**
         * Повертає коефіцієнт для розрахунку витрат Ці при прориві.
         *
         *
         * Показник ступеня визначає зростання витрати Кі при прориві. За замовчуванням 0.2.
         *
         *
         * @return Коефіцієнт витрат Ці
         */
        get() = 0.2

    /**
     * Повертає список Реалмів , у які можливий прорив з цього Реалму .
     *
     *
     * Цей метод визначає можливі шляхи прогресії для сутності в поточному Реалмі .
     *
     *
     * @param instance Екземпляр поточного Реалму
     * @param living   Сутність, для якої визначаються можливі прориви
     * @return Список Реалмів  для прориву
     * @see RealmInstance.getNextBreakthroughs
     */
    abstract fun getNextBreakthroughs(instance: RealmInstance?, living: LivingEntity?): MutableList<Realm?>?

    /**
     * Повертає список Реалмів , з яких можливий прорив у цей Реалм .
     *
     *
     * За замовчуванням повертає порожній список. Перевизначте цей метод для
     * встановлення ієрархії Реалмів .
     *
     *
     * @param instance Екземпляр поточного Реалму
     * @param living   Сутність, для якої визначаються попередні Реалми
     * @return Список попередніх Реалмів
     * @see RealmInstance.getPreviousBreakthroughs
     */
    abstract fun getPreviousBreakthroughs(instance: RealmInstance?, living: LivingEntity?): MutableList<Realm?>?

    /**
     * Повертає Реалм , у який відбувається прорив за замовчуванням з цього Реалму .
     *
     *
     * Визначає основний шлях прогресії для сутності.
     *
     *
     * @param instance Екземпляр поточного Реалму
     * @param living   Сутність, для якої визначається стандартний прорив
     * @return Реалм  для прориву або null, якщо прорив неможливий
     * @see RealmInstance.getDefaultBreakthrough
     */
    abstract fun getDefaultBreakthrough(instance: RealmInstance?, living: LivingEntity?): Realm?

    /**
     * Повертає список стадій, доступних у цьому Реалмі .
     *
     *
     * Стадії представляють проміжні етапи розвитку в межах одного Реалму .
     *
     *
     * @param instance Екземпляр поточного Реалму
     * @param living   Сутність, для якої визначаються стадії
     * @return Список стадій
     * @see RealmInstance.getRealmStages
     */
    abstract fun getRealmStages(instance: RealmInstance?, living: LivingEntity?): MutableList<Stage?>?

    /**
     * Повертає вимір та блок для відродження сутності в цьому Реалмі .
     *
     *
     * Визначає, де сутність відродиться після смерті та який блок буде
     * створено за відсутності валідної точки відродження.
     *
     *
     * @param instance Екземпляр поточного Реалму
     * @param owner    Сутність, для якої визначається точка відродження
     * @return Пара (ключ виміру, стан блоку)
     * @see RealmInstance.getRespawnDimension
     */
    fun getRespawnDimension(
        instance: RealmInstance?,
        owner: LivingEntity?
    ): com.mojang.datafixers.util.Pair<ResourceKey<Level?>?, BlockState?> {
        return com.mojang.datafixers.util.Pair.of<ResourceKey<Level?>?, BlockState?>(
            Level.OVERWORLD,
            Blocks.AIR.defaultBlockState()
        )
    }

    /**
     * Визначає, чи є сутність пасивно дружньою до володаря цього Реалму .
     *
     *
     * За замовчуванням повертає false.
     *
     *
     * @param entity Сутність для перевірки
     * @return true, якщо сутність дружня, false - в іншому випадку
     */
    abstract fun passivelyFriendlyWith(instance: RealmInstance?, entity: LivingEntity?): Boolean

    /**
     * Визначає, чи може сутність в цьому Реалмі  літати.
     *
     *
     * За замовчуванням повертає false.
     *
     *
     * @return true, якщо політ дозволено, false - в іншому випадку
     */
    abstract fun canFly(instance: RealmInstance?, living: LivingEntity?): Boolean

    val registryName: ResourceLocation?
        /**
         * Отримує ідентифікатор цього Реалму  з реєстру.
         *
         * @return Ідентифікатор Реалму  або null, якщо Реалм  не зареєстровано
         */
        get() = RealmAPI.realmRegistry!!.getId(this)

    val name: MutableComponent?
        /**
         * Отримує локалізовану назву цього Реалму .
         *
         * @return Компонент тексту з назвою Реалму  або null, якщо Реалм  не зареєстровано
         */
        get() {
            val id = this.registryName
            return if (id == null) null else Component.translatable(
                String.format(
                    "%s.realm.%s",
                    id.namespace,
                    id.path.replace('/', '.')
                )
            ).withStyle(type.component.getStyle())
        }

    val trackedName: MutableComponent?
        /**
         * Отримує локалізовану назву цього Реалму  з позначкою відстеження.
         *
         *
         * Використовується для відображення в меню, коли Реалм  відстежується гравцем.
         *
         *
         * @return Компонент тексту з назвою Реалму  та позначкою відстеження або null
         */
        get() {
            val id = this.registryName
            val name = Component.translatable(
                String.format(
                    "%s.realm.%s",
                    id!!.namespace,
                    id.path.replace('/', '.')
                )
            )
            val track =
                Component.translatable("%s.realm_menu.track".format(ModuleConstants.MOD_ID))
                    .withStyle(ChatFormatting.YELLOW)
            return name.append(track)
        }

    val nameTranslationKey: String
        /**
         * Отримує ключ перекладу для назви цього Реалму .
         *
         * @return Ключ перекладу
         */
        get() = (this.name!!.contents as TranslatableContents).key

    /**
     * Перевіряє рівність Реалмів  за їх ідентифікаторами.
     *
     * @param o Об'єкт для порівняння
     * @return true, якщо об'єкти рівні, false - в іншому випадку
     */
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        } else if (o != null && this.javaClass == o.javaClass) {
            val realm = o as Realm
            return this.registryName == realm.registryName
        } else {
            return false
        }
    }

    /**
     * Викликається, коли сутність встановлює цей Реалм  як активний.
     *
     *
     * Перевизначте цей метод для додавання власної логіки при встановленні Реалму .
     *
     *
     * @param instance Екземпляр Реалму , що встановлюється
     * @param living   Сутність, яка встановлює Реалм
     * @see RealmInstance.onSet
     */
    fun onSet(instance: RealmInstance?, living: LivingEntity?) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається, коли сутність досягає цього Реалму .
     *
     *
     * Перевизначте цей метод для додавання власної логіки при досягненні Реалму .
     *
     *
     * @param instance Екземпляр Реалму , якого досягають
     * @param living   Сутність, яка досягає Реалму
     * @see RealmInstance.onReach
     */
    fun onReach(instance: RealmInstance?, living: LivingEntity?) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається, коли сутність починає відстежувати цей Реалм .
     *
     *
     * Перевизначте цей метод для додавання власної логіки при відстеженні Реалму .
     *
     *
     * @param instance Екземпляр Реалму , який відстежується
     * @param living   Сутність, яка відстежує Реалм
     * @see RealmInstance.onTrack
     */
    fun onTrack(instance: RealmInstance?, living: LivingEntity?) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається, коли сутність здійснює прорив у цей Реалм.
     *
     *
     * Перевизначте цей метод для додавання власної логіки при прориві.
     *
     *
     * @param instance Екземпляр Реалму , в який відбувається прорив
     * @param living   Сутність, яка здійснює прорив
     * @see RealmInstance.onBreakthrough
     */
    fun onBreakthrough(instance: RealmInstance?, living: LivingEntity?) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається кожен тік для сутності, що має цей Реалм .
     *
     *
     * Перевизначте цей метод для додавання власної логіки, що виконується щотіка.
     *
     *
     * @param instance Екземпляр активного Реалму
     * @param living   Сутність, що має цей Реалм
     */
    fun onTick(instance: RealmInstance?, living: LivingEntity?) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Додає модифікатор атрибуту до цього Реалму .
     *
     *
     * Модифікатори застосовуються до сутності, коли Реалм  встановлюється як активний.
     *
     *
     * @param holder           Тримач атрибуту
     * @param resourceLocation Ідентифікатор модифікатора
     * @param amount           Значення модифікатора
     * @param operation        Операція модифікатора
     */
    fun addAttributeModifier(
        holder: Holder<Attribute?>?,
        resourceLocation: ResourceLocation?,
        amount: Double,
        operation: AttributeModifier.Operation?
    ): Realm {
        this.attributeModifiers.put(holder, AttributeTemplate(resourceLocation, amount, operation))
        return this
    }

    fun createModifiers(
        instance: RealmInstance?,
        i: Int,
        consumer: BiConsumer<Holder<Attribute?>?, AttributeModifier?>
    ) {
        this.attributeModifiers.forEach { (holder: Holder<Attribute?>?, template: AttributeTemplate?) ->
            consumer.accept(
                holder,
                template!!.create(i)
            )
        }
    }

    fun removeAttributeModifiers(instance: RealmInstance?, entity: LivingEntity) {
        val attributeMap = entity.getAttributes()
        for (entry in this.attributeModifiers.entries) {
            val attributeInstance = attributeMap.getInstance(entry.key)
            if (attributeInstance != null) {
                attributeInstance.removeModifier((entry.value)!!.id)
            }
        }
    }

    fun addAttributeModifiers(instance: RealmInstance?, entity: LivingEntity, i: Int) {
        val attributeMap = entity.getAttributes()
        for (entry in this.attributeModifiers.entries) {
            val attributeInstance = attributeMap.getInstance(entry.key)
            if (attributeInstance != null) {
                attributeInstance.removeModifier(entry.value!!.id)
                attributeInstance.addPermanentModifier((entry.value)!!.create(i))
            }
        }
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + attributeModifiers.hashCode()
        result = 31 * result + baseHealth.hashCode()
        result = 31 * result + baseAttackDamage.hashCode()
        result = 31 * result + baseAttackSpeed.hashCode()
        result = 31 * result + knockBackResistance.hashCode()
        result = 31 * result + jumpHeight.hashCode()
        result = 31 * result + movementSpeed.hashCode()
        result = 31 * result + sprintSpeed.hashCode()
        result = 31 * result + minBaseQi.hashCode()
        result = 31 * result + maxBaseQi.hashCode()
        result = 31 * result + coefficient.hashCode()
        result = 31 * result + (baseQiRange?.hashCode() ?: 0)
        result = 31 * result + (registryName?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (trackedName?.hashCode() ?: 0)
        result = 31 * result + nameTranslationKey.hashCode()
        return result
    }
}