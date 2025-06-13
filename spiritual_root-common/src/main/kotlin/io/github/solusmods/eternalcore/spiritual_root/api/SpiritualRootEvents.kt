package io.github.solusmods.eternalcore.spiritual_root.api

import dev.architectury.event.CompoundEventResult
import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import io.github.solusmods.eternalcore.network.api.util.Changeable
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity

/**
 * Інтерфейс подій системи духовних коренів для Minecraft 1.21.1 з використанням Architectury API.
 *
 *
 * Цей інтерфейс визначає події, які можуть бути зареєстровані та оброблені іншими модами
 * для взаємодії з системою духовних коренів. Всі події працюють з екземплярами [SpiritualRootInstance]
 * та живими істотами [LivingEntity].
 *
 *
 * Події дозволяють:
 *
 *  * Модифікувати поведінку при додаванні нових духовних коренів
 *  * Втрутитися в процес підвищення рівня розвитку
 *  * Змінити механіку отримання досвіду
 *
 *
 * @since 1.0
 * @version 1.0.4.5
 * @see SpiritualRootInstance
 *
 * @see LivingEntity
 *
 * @author EternalCore Team
 */
interface SpiritualRootEvents {
    /**
     * Функціональний інтерфейс для обробки події додавання духовного кореня.
     *
     *
     * Цей обробник викликається щоразу, коли до істоти додається новий духовний корінь.
     * Обробник може змінити поведінку додавання, модифікувати повідомлення або навіть
     * скасувати операцію.
     *
     *
     * Приклад використання:
     * <pre>`SpiritualRootEvents.ADD.register((instance, living, advancement, notifyPlayer, rootMessage) -> {
     * if (someCondition) {
     * return CompoundEventResult.interruptFalse(); // Скасувати додавання
     * }
     *
     * // Модифікувати повідомлення
     * rootMessage.set(Component.literal("Новий духовний корінь отримано!"));
     *
     * return CompoundEventResult.pass(); // Продовжити нормальне виконання
     * });
    `</pre> *
     *
     * @param <T> Тип екземпляра духовного кореня
    </T> */
    fun interface SpiritualRootAddEvent<T> {
        /**
         * Обробляє додавання духовного кореня до істоти.
         *
         * @param instance      Екземпляр духовного кореня, що додається
         * @param living        Істота, до якої додається корінь (гравець, моб тощо)
         * @param advancement   `true`, якщо подія пов'язана з системою прогресу Minecraft
         * @param notifyPlayer  Змінювана змінна, що визначає чи повідомляти гравця про подію
         * @param rootMessage   Змінюване повідомлення, що буде показано гравцю
         * @return              Результат обробки події:
         *
         *  * [CompoundEventResult.pass] - продовжити звичайне виконання
         *  * [CompoundEventResult.interruptTrue] - перервати з успіхом та поверненим значенням
         *  * [CompoundEventResult.interruptFalse] - перервати та скасувати операцію
         *
         */
        fun add(
            instance: T,
            living: LivingEntity,
            advancement: Boolean?,
            notifyPlayer: Changeable<Boolean?>?,
            rootMessage: Changeable<MutableComponent?>?
        ): CompoundEventResult<T>
    }

    /**
     * Функціональний інтерфейс для обробки події підвищення рівня духовного кореня.
     *
     *
     * Цей обробник викликається при кожному підвищенні рівня розвитку духовного кореня.
     * Обробники можуть модифікувати повідомлення, додавати додаткові ефекти або логувати прогрес.
     *
     *
     * Приклад використання:
     * <pre>`SpiritualRootEvents.ADVANCE.register((instance, entity, advancement, notifyPlayer, rootMessage) -> {
     * // Додати спеціальний ефект при підвищенні рівня
     * if (entity instanceof Player player) {
     * player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
     * }
     *
     * // Змінити повідомлення
     * rootMessage.set(Component.literal("Ваш духовний корінь зріс у силі!"));
     * });
    `</pre> *
     */
    fun interface SpiritualRootAdvanceEvent {
        /**
         * Обробляє підвищення рівня духовного кореня.
         *
         * @param instance      Екземпляр духовного кореня, що розвивається
         * @param entity        Істота, у якої розвивається корінь
         * @param advancement   `true`, якщо подія пов'язана з системою прогресу Minecraft
         * @param notifyPlayer  Змінювана змінна, що визначає чи повідомляти гравця про підвищення рівня
         * @param rootMessage   Змінюване повідомлення про підвищення рівня, що буде показано гравцю
         */
        fun advance(
            instance: SpiritualRootInstance,
            entity: LivingEntity,
            advancement: Boolean?,
            notifyPlayer: Changeable<Boolean?>?,
            rootMessage: Changeable<MutableComponent?>?
        )
    }

    /**
     * Функціональний інтерфейс для обробки події отримання досвіду духовним коренем.
     *
     *
     * Цей обробник викликається кожного разу, коли духовний корінь отримує досвід.
     * Обробники можуть модифікувати кількість досвіду, додавати бонуси або застосовувати
     * додаткові ефекти.
     *
     *
     * Приклад використання:
     * <pre>`SpiritualRootEvents.EXPERIENCE_GAIN.register((instance, entity, amount) -> {
     * // Подвоїти досвід вночі
     * if (entity.level().isNight()) {
     * amount.set(amount.get() * 2.0f);
     * }
     *
     * // Додати візуальний ефект
     * if (entity instanceof Player player) {
     * player.level().addParticle(ParticleTypes.EXPERIENCE,
     * player.getX(), player.getY() + 1, player.getZ(), 0, 0.1, 0);
     * }
     * });
    `</pre> *
     *
     *
     * **Примітка:** Параметр `amount` може бути обгорнутий у [Changeable]
     * в майбутніх версіях для можливості модифікації кількості досвіду.
     */
    fun interface SpiritualRootExperienceGainEvent {
        /**
         * Обробляє отримання досвіду духовним коренем.
         *
         * @param instance        Екземпляр духовного кореня, що отримує досвід
         * @param entity          Істота-власник духовного кореня
         * @param amount          Кількість досвіду, що отримується (у майбутніх версіях може бути [Changeable])
         */
        fun gainExperience(instance: SpiritualRootInstance, entity: LivingEntity, amount: Float)
    }

    /**
     * Викликається перед тим, як розрахувати або встановити початкову чистоту духовного кореня.
     * Дозволяє модифікувати або повністю замінити значення чистоти (0.0f–1.0f).
     */
    fun interface SpiritualRootPurityCalculateEvent<F> {
        fun calculate(instance: SpiritualRootInstance, entity: LivingEntity, purity: Float): CompoundEventResult<F>
    }

    /**
     * Викликається при зміні чистоти духовного кореня вручну або в результаті подій.
     * Може модифікувати нове значення перед встановленням.
     */
    fun interface SpiritualRootPurityChangeEvent {
        fun change(
            instance: SpiritualRootInstance,
            entity: LivingEntity,
            oldPurity: Float?,
            newPurity: Changeable<Float?>?
        )
    }

    fun interface ForgetSpiritualRootEvent {
        fun forget(
            instance: SpiritualRootInstance,
            entity: LivingEntity,
            message: Changeable<MutableComponent?>?
        ): EventResult
    }

    companion object {
        /**
         * Подія, що викликається при додаванні нового духовного кореня до істоти.
         *
         *
         * Ця подія дозволяє модам:
         *
         *  * Модифікувати повідомлення, що відображається гравцю
         *  * Вирішити, чи повідомляти гравця про додавання кореня
         *  * Повністю скасувати додавання кореня
         *  * Змінити екземпляр кореня, що додається
         *
         *
         *
         * Використовує [CompoundEventResult] для можливості повернення модифікованого значення.
         *
         * @see SpiritualRootAddEvent
         */
        @JvmField
        val ADD: Event<SpiritualRootAddEvent<SpiritualRootInstance>> =
            EventFactory.createCompoundEventResult<SpiritualRootAddEvent<SpiritualRootInstance>>()

        /**
         * Подія, що викликається при підвищенні рівня розвитку духовного кореня.
         *
         *
         * Ця подія надає можливість:
         *
         *  * Змінити повідомлення про підвищення рівня
         *  * Вирішити, чи показувати повідомлення гравцю
         *  * Додати додаткові ефекти при підвищенні рівня
         *  * Логувати прогрес розвитку
         *
         *
         *
         * Використовує loop event для послідовного виклику всіх обробників.
         *
         * @see SpiritualRootAdvanceEvent
         */
        @JvmField
        val ADVANCE: Event<SpiritualRootAdvanceEvent> = EventFactory.createLoop<SpiritualRootAdvanceEvent>()

        /**
         * Подія, що викликається коли духовний корінь отримує досвід.
         *
         *
         * Ця подія дозволяє:
         *
         *  * Модифікувати кількість отримуваного досвіду
         *  * Додати бонуси або штрафи до досвіду
         *  * Логувати отримання досвіду
         *  * Застосовувати додаткові ефекти при отриманні досвіду
         *
         *
         *
         * Використовує loop event для послідовного виклику всіх обробників.
         *
         * @see SpiritualRootExperienceGainEvent
         */
        @JvmField
        val EXPERIENCE_GAIN: Event<SpiritualRootExperienceGainEvent> =
            EventFactory.createLoop<SpiritualRootExperienceGainEvent>()

        @JvmField
        val CALCULATE_PURITY: Event<SpiritualRootPurityCalculateEvent<Float>> =
            EventFactory.createCompoundEventResult<SpiritualRootPurityCalculateEvent<Float>>()
        @JvmField
        val CHANGE_PURITY: Event<SpiritualRootPurityChangeEvent> =
            EventFactory.createEventResult<SpiritualRootPurityChangeEvent>()
        @JvmField
        val FORGET_SPIRITUAL_ROOT: Event<ForgetSpiritualRootEvent> =
            EventFactory.createEventResult<ForgetSpiritualRootEvent>()
    }
}