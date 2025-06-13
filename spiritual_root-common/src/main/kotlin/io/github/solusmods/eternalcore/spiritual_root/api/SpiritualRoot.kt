package io.github.solusmods.eternalcore.spiritual_root.api

import io.github.solusmods.eternalcore.element.api.Element
import io.github.solusmods.eternalcore.network.api.util.Changeable
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import kotlin.math.min

@Suppress("unchecked_cast")
abstract class SpiritualRoot(
    /**
     * Тип кореня, що визначає його категорію та рідкісність.
     *
     *
     * Можливі типи включають:
     *
     *  * Звичайний - базовий тип духовного кореня
     *  * Рідкісний - покращені характеристики
     *  * Легендарний - унікальні здібності
     *  * Небесний - найвищий рівень потенціалу
     *
     *
     */
    val type: RootType) {
    // region ПОЛЯ
    /**
     * Мапа модифікаторів атрибутів, що застосовуються до сутності з цим Духовним Коренем.
     *
     *
     * Ключ - тримач атрибуту, значення - шаблон модифікатора з налаштуваннями.
     * Модифікатори автоматично застосовуються при активації кореня.
     *
     */
    private val attributeModifiers: MutableMap<Holder<Attribute>, AttributeTemplate?> =
        Object2ObjectOpenHashMap<Holder<Attribute>, AttributeTemplate?>() as MutableMap<Holder<Attribute>, AttributeTemplate?>

    // endregion
    // region КОНСТРУКТОРИ ТА СТВОРЕННЯ ЕКЗЕМПЛЯРІВ
    /**
     * Створює новий екземпляр Духовного Кореня з базовими налаштуваннями.
     *
     *
     * Цей метод використовується для ініціалізації нового Духовного Кореня
     * при його призначенні сутності. Створений екземпляр містить всі базові
     * характеристики та готовий до використання.
     *
     *
     * @return Новий екземпляр [SpiritualRootInstance] з базовими налаштуваннями
     */
    open fun createDefaultInstance(): SpiritualRootInstance {
        return SpiritualRootInstance(this)
    }

    //endregion
    // region ІНФОРМАЦІЙНІ МЕТОДИ
    val registryName: ResourceLocation?
        /**
         * Отримує ідентифікатор цього Духовного Кореня з реєстру EternalCore.
         *
         *
         * Ідентифікатор використовується для серіалізації, локалізації та
         * ідентифікації кореня в системі.
         *
         *
         * @return [ResourceLocation] ідентифікатор Духовного Кореня,
         * або `null` якщо корінь не зареєстровано в системі
         */
        get() = SpiritualRootAPI.spiritualRootRegistry.getId(this)

    val name: MutableComponent?
        /**
         * Отримує локалізовану назву цього Духовного Кореня для відображення гравцю.
         *
         *
         * Назва автоматично генерується на основі ідентифікатора кореня
         * за шаблоном: `namespace.spiritual_root.path`
         *
         *
         * @return [MutableComponent] з локалізованою назвою Духовного Кореня,
         * або `null` якщо корінь не зареєстровано
         */
        get() {
            val id = this.registryName
            return if (id == null) null else Component.translatable(
                String.format("%s.spiritual_root.%s", id.namespace, id.path.replace('/', '.'))
            )
        }

    val nameTranslationKey: String
        /**
         * Отримує ключ перекладу для назви цього Реалму .
         *
         * @return Ключ перекладу
         */
        get() = (this.name!!.contents as TranslatableContents).key

    val maxLevel: RootLevels
        /**
         * Повертає максимальний рівень майстерності для цього Духовного Кореня.
         *
         *
         * Максимальний рівень визначає межу розвитку кореня. Більшість коренів
         * можуть досягти рівня X, але деякі рідкісні корені можуть мати вищі межі.
         *
         *
         * @return [RootLevels] максимальний рівень майстерності (за замовчуванням X)
         */
        get() = RootLevels.X

    // endregion
    // region МЕТОДИ УПРАВЛІННЯ АТРИБУТАМИ
    /**
     * Додає модифікатор атрибуту до цього Духовного Кореня.
     *
     *
     * Модифікатори автоматично застосовуються до сутності при активації кореня
     * та видаляються при його деактивації. Це дозволяє кореням надавати
     * постійні бонуси до характеристик.
     *
     *
     * @param holder           Тримач атрибуту, до якого застосовується модифікатор
     * @param resourceLocation Унікальний ідентифікатор модифікатора
     * @param amount           Значення модифікатора (може бути від'ємним)
     * @param operation        Тип операції модифікатора (додавання, множення тощо)
     */
    open fun addAttributeModifier(
        holder: Holder<Attribute>, resourceLocation: ResourceLocation,
        amount: Double, operation: AttributeModifier.Operation
    ) {
        this.attributeModifiers.put(holder, AttributeTemplate(resourceLocation, amount, operation))
    }

    /**
     * Застосовує всі модифікатори атрибутів цього Духовного Кореня до вказаної сутності.
     *
     *
     * Цей метод викликається автоматично при встановленні Духовного Кореня як активного.
     * Усі існуючі модифікатори з тим же ідентифікатором спочатку видаляються,
     * а потім додаються нові для уникнення дублювання.
     *
     *
     * @param instance Екземпляр Духовного Кореня, що містить поточний стан
     * @param entity   Сутність, до якої застосовуються модифікатори атрибутів
     */
    open fun addAttributeModifiers(instance: SpiritualRootInstance, entity: LivingEntity) {
        if (this.attributeModifiers.isEmpty()) return

        val attributeMap = entity.attributes
        for (entry in this.attributeModifiers.entries) {
            val attributeInstance = attributeMap.getInstance(entry.key)

            if (attributeInstance == null) continue
            attributeInstance.removeModifier(entry.value!!.id()!!)
            attributeInstance.addPermanentModifier(entry.value!!.create())
        }
    }

    /**
     * Видаляє всі модифікатори атрибутів цього Духовного Кореня від вказаної сутності.
     *
     *
     * Цей метод викликається при деактивації Духовного Кореня або його заміні.
     * Для серверних гравців автоматично надсилається пакет оновлення атрибутів.
     *
     *
     * @param instance Екземпляр Духовного Кореня, що деактивується
     * @param entity   Сутність, від якої видаляються модифікатори атрибутів
     */
    open fun removeAttributeModifiers(instance: SpiritualRootInstance, entity: LivingEntity) {
        if (this.attributeModifiers.isEmpty()) return

        val map = entity.attributes
        val dirtyInstances: MutableList<AttributeInstance?> = mutableListOf()

        for (entry in this.attributeModifiers.entries) {
            val attributeInstance = map.getInstance(entry.key)
            if (attributeInstance == null) continue
            attributeInstance.removeModifier(entry.value!!.id())
            dirtyInstances.add(attributeInstance)
        }

        if (!dirtyInstances.isEmpty() && entity is ServerPlayer) {
            val packet = ClientboundUpdateAttributesPacket(entity.id, dirtyInstances)
            entity.connection.send(packet)
        }
    }

    // endregion
    // region МЕТОДИ РОЗВИТКУ ТА ПРОГРЕСІЇ
    /**
     * Збільшує силу (чистоту) Духовного Кореня на вказану величину.
     *
     *
     * Сила кореня впливає на ефективність культивації та потужність технік.
     * Максимальне значення сили обмежене 1.0 (100% чистоти).
     * Вища чистота кореня означає кращу спорідненість з відповідним елементом.
     *
     *
     * @param instance Екземпляр Духовного Кореня, що підлягає покращенню
     * @param living   Сутність, чия сила кореня збільшується
     * @param amount   Величина збільшення сили (від 0.0 до 1.0)
     * @see SpiritualRootInstance.increaseStrength
     */
    open fun increaseStrength(instance: SpiritualRootInstance, living: LivingEntity, amount: Float) {
        instance.strength = (min(1.0f, instance.strength + amount))
    }

    /**
     * Перевіряє, чи може вказаний екземпляр Духовного Кореня просунутися на наступний рівень.
     *
     *
     * Умови просування можуть включати:
     *
     *  * Достатній рівень досвіду
     *  * Необхідні ресурси для прориву
     *  * Відповідне середовище для культивації
     *  * Особливі умови для рідкісних коренів
     *
     *
     *
     * @param spiritualRootInstance Екземпляр Духовного Кореня для перевірки
     * @param entity               Сутність, що бажає просунутися
     * @see SpiritualRootInstance.canAdvance
     * @return `true` якщо просування можливе, `false` в іншому випадку
     */
    open fun canAdvance(spiritualRootInstance: SpiritualRootInstance, entity: LivingEntity): Boolean {
        return false
    }

    // endregion
    // region АБСТРАКТНІ МЕТОДИ ЕЛЕМЕНТІВ ТА ПРОГРЕСІЇ
    /**
     * Повертає елемент, пов'язаний з цим Духовним Коренем.
     *
     *
     * Елемент визначає тип елементальної енергії (Ци), з якою сутність має
     * найбільшу спорідненість. Це впливає на:
     *
     *  * Швидкість поглинання відповідної Ци з навколишнього середовища
     *  * Ефективність елементальних технік
     *  * Взаємодію з іншими елементами (синергія або конфлікти)
     *  * Доступ до специфічних локацій та ресурсів
     *
     *
     *
     * @param instance Екземпляр Духовного Кореня
     * @param entity   Сутність, для якої визначається елемент
     * @see SpiritualRootInstance.getElement
     * @return [Element] пов'язаний з коренем, або `null` якщо елемент не визначено
     */
    abstract fun getElement(instance: SpiritualRootInstance, entity: LivingEntity): Element?

    /**
     * Повертає перший ступінь еволюції цього Духовного Кореня.
     *
     *
     * Система еволюції дозволяє коренями розвиватися в більш потужні форми.
     * Перший ступінь зазвичай покращує базові характеристики та може
     * відкривати доступ до нових здібностей.
     *
     *
     *
     * Приклад еволюції: Звичайний Вогняний Корінь → Пекельний Вогняний Корінь
     *
     *
     * @param instance Екземпляр поточного Духовного Кореня
     * @param living   Сутність, для якої визначається шлях еволюції
     * @return Еволюціонований Духовний Корінь або `null`, якщо еволюція неможлива
     * @see SpiritualRootInstance.getFirstDegree
     */
    abstract fun getFirstDegree(instance: SpiritualRootInstance, living: LivingEntity): SpiritualRoot?

    /**
     * Повертає другий ступінь еволюції цього Духовного Кореня.
     *
     *
     * Другий ступінь еволюції представляє вищий рівень розвитку кореня
     * з додатковими унікальними здібностями та значно покращеними характеристиками.
     * Досягнення другого ступеня зазвичай вимагає виконання особливих умов.
     *
     *
     * @param spiritualRootInstance Екземпляр поточного Духовного Кореня
     * @param living               Сутність, для якої визначається шлях еволюції
     * @return Еволюціонований Духовний Корінь другого ступеня або `null`, якщо еволюція неможлива
     * @see SpiritualRootInstance.getSecondDegree
     */
    open fun getSecondDegree(spiritualRootInstance: SpiritualRootInstance, living: LivingEntity): SpiritualRoot? {
        return null
    }


    /**
     * Повертає протилежний Духовний Корінь для поточного.
     *
     *
     * Протилежні Духовні Корені мають конфліктуючі елементальні властивості
     * та можуть створювати проблеми при одночасному культивуванні.
     *
     *
     *
     * Приклади протилежностей:
     *
     *  * Вогонь ↔ Вода
     *  * Метал ↔ Дерево
     *  * Світло ↔ Темрява
     *  * Інь ↔ Ян
     *
     *
     *
     * @param instance Екземпляр Духовного Кореня
     * @param entity   Сутність, для якої визначається протилежний Корінь
     * @see SpiritualRootInstance.getOpposite
     * @return Протилежний Духовний Корінь або `null` якщо протилежності немає
     */
    open fun getOpposite(instance: SpiritualRootInstance, entity: LivingEntity): SpiritualRoot? {
        return null
    }


    /**
     *
     * @see SpiritualRootInstance.getPreviousDegree
     */
    abstract fun getPreviousDegree(spiritualRootInstance: SpiritualRootInstance, living: LivingEntity): SpiritualRoot?

    // endregion
    // region МЕТОДИ ПОДІЙ
    /**
     * Викликається при першому отриманні цього Духовного Кореня сутністю.
     *
     *
     * Цей метод дозволяє додати спеціальну логіку, що виконується одразу
     * після призначення кореня сутності. Наприклад:
     *
     *  * Надання початкових бонусів
     *  * Активація пасивних здібностей
     *  * Зміна зовнішнього вигляду сутності
     *  * Додавання спеціальних ефектів
     *
     *
     *
     * @param instance Новий екземпляр Духовного Кореня
     * @param living   Сутність, яка отримала Духовний Корінь
     */
    open fun onAdd(instance: SpiritualRootInstance, living: LivingEntity) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається при просуванні сутності на наступний рівень цього Духовного Кореня.
     *
     *
     * Цей метод автоматично запускає подію [SpiritualRootEvents.ADVANCE]
     * та може містити додаткову логіку, специфічну для конкретного типу кореня.
     *
     *
     *
     * Типові дії при просуванні:
     *
     *  * Збільшення потужності модифікаторів атрибутів
     *  * Розблокування нових здібностей
     *  * Покращення ефективності культивації
     *  * Візуальні ефекти прориву
     *
     *
     *
     * @param instance Екземпляр Духовного Кореня, що просувається
     * @param living   Сутність, яка досягла наступного рівня
     * @see SpiritualRootInstance.onAdvance
     */
    open fun onAdvance(instance: SpiritualRootInstance, living: LivingEntity) {
        SpiritualRootEvents.Companion.ADVANCE.invoker()!!.advance(instance, living, false, Changeable.of(false), null)
    }

    /**
     * Викликається при отриманні досвіду для цього Духовного Кореня.
     *
     *
     * Метод автоматично запускає подію [SpiritualRootEvents.EXPERIENCE_GAIN]
     * та може містити додаткову логіку обробки досвіду.
     *
     *
     *
     * Досвід може отримуватися через:
     *
     *  * Медитацію в підходящому середовищі
     *  * Використання технік відповідного елементу
     *  * Поглинання елементальних ресурсів
     *  * Участь у бойових діях
     *
     *
     *
     * @param spiritualRootInstance Екземпляр Духовного Кореня, що отримує досвід
     * @param entity               Сутність, яка культивує корінь
     * @see SpiritualRootInstance.onAddExperience
     */
    open fun onAddExperience(spiritualRootInstance: SpiritualRootInstance, entity: LivingEntity, amount: Float) {
        SpiritualRootEvents.Companion.EXPERIENCE_GAIN.invoker()!!.gainExperience(spiritualRootInstance, entity, amount)
    } //endregion
}