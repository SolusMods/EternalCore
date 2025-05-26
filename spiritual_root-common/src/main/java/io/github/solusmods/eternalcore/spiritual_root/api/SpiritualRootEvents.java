package io.github.solusmods.eternalcore.spiritual_root.api;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

/**
 * Інтерфейс подій системи духовних коренів для Minecraft 1.21.1 з використанням Architectury API.
 *
 * <p>Цей інтерфейс визначає події, які можуть бути зареєстровані та оброблені іншими модами
 * для взаємодії з системою духовних коренів. Всі події працюють з екземплярами {@link SpiritualRootInstance}
 * та живими істотами {@link LivingEntity}.</p>
 *
 * <p>Події дозволяють:</p>
 * <ul>
 *   <li>Модифікувати поведінку при додаванні нових духовних коренів</li>
 *   <li>Втрутитися в процес підвищення рівня розвитку</li>
 *   <li>Змінити механіку отримання досвіду</li>
 * </ul>
 *
 * @since 1.0
 * @version 1.0.4.5
 * @see SpiritualRootInstance
 * @see LivingEntity
 * @author EternalCore Team
 */
public interface SpiritualRootEvents {

    /**
     * Подія, що викликається при додаванні нового духовного кореня до істоти.
     *
     * <p>Ця подія дозволяє модам:</p>
     * <ul>
     *   <li>Модифікувати повідомлення, що відображається гравцю</li>
     *   <li>Вирішити, чи повідомляти гравця про додавання кореня</li>
     *   <li>Повністю скасувати додавання кореня</li>
     *   <li>Змінити екземпляр кореня, що додається</li>
     * </ul>
     *
     * <p>Використовує {@link CompoundEventResult} для можливості повернення модифікованого значення.</p>
     *
     * @see SpiritualRootAddEvent
     */
    Event<SpiritualRootAddEvent<SpiritualRootInstance>> ADD = EventFactory.createCompoundEventResult();

    /**
     * Подія, що викликається при підвищенні рівня розвитку духовного кореня.
     *
     * <p>Ця подія надає можливість:</p>
     * <ul>
     *   <li>Змінити повідомлення про підвищення рівня</li>
     *   <li>Вирішити, чи показувати повідомлення гравцю</li>
     *   <li>Додати додаткові ефекти при підвищенні рівня</li>
     *   <li>Логувати прогрес розвитку</li>
     * </ul>
     *
     * <p>Використовує loop event для послідовного виклику всіх обробників.</p>
     *
     * @see SpiritualRootAdvanceEvent
     */
    Event<SpiritualRootAdvanceEvent> ADVANCE = EventFactory.createLoop();

    /**
     * Подія, що викликається коли духовний корінь отримує досвід.
     *
     * <p>Ця подія дозволяє:</p>
     * <ul>
     *   <li>Модифікувати кількість отримуваного досвіду</li>
     *   <li>Додати бонуси або штрафи до досвіду</li>
     *   <li>Логувати отримання досвіду</li>
     *   <li>Застосовувати додаткові ефекти при отриманні досвіду</li>
     * </ul>
     *
     * <p>Використовує loop event для послідовного виклику всіх обробників.</p>
     *
     * @see SpiritualRootExperienceGainEvent
     */
    Event<SpiritualRootExperienceGainEvent> EXPERIENCE_GAIN = EventFactory.createLoop();

    Event<SpiritualRootPurityCalculateEvent<Float>> CALCULATE_PURITY = EventFactory.createCompoundEventResult();
    Event<SpiritualRootPurityChangeEvent> CHANGE_PURITY = EventFactory.createEventResult();
    Event<ForgetSpiritualRootEvent> FORGET_SPIRITUAL_ROOT = EventFactory.createEventResult();
    /**
     * Функціональний інтерфейс для обробки події додавання духовного кореня.
     *
     * <p>Цей обробник викликається щоразу, коли до істоти додається новий духовний корінь.
     * Обробник може змінити поведінку додавання, модифікувати повідомлення або навіть
     * скасувати операцію.</p>
     *
     * <p>Приклад використання:</p>
     * <pre>{@code
     * SpiritualRootEvents.ADD.register((instance, living, advancement, notifyPlayer, rootMessage) -> {
     *     if (someCondition) {
     *         return CompoundEventResult.interruptFalse(); // Скасувати додавання
     *     }
     *
     *     // Модифікувати повідомлення
     *     rootMessage.set(Component.literal("Новий духовний корінь отримано!"));
     *
     *     return CompoundEventResult.pass(); // Продовжити нормальне виконання
     * });
     * }</pre>
     *
     * @param <T> Тип екземпляра духовного кореня
     */
    @FunctionalInterface
    interface SpiritualRootAddEvent<T> {
        /**
         * Обробляє додавання духовного кореня до істоти.
         *
         * @param instance      Екземпляр духовного кореня, що додається
         * @param living        Істота, до якої додається корінь (гравець, моб тощо)
         * @param advancement   {@code true}, якщо подія пов'язана з системою прогресу Minecraft
         * @param notifyPlayer  Змінювана змінна, що визначає чи повідомляти гравця про подію
         * @param rootMessage   Змінюване повідомлення, що буде показано гравцю
         * @return              Результат обробки події:
         *                      <ul>
         *                        <li>{@link CompoundEventResult#pass()} - продовжити звичайне виконання</li>
         *                        <li>{@link CompoundEventResult#interruptTrue(Object)} - перервати з успіхом та поверненим значенням</li>
         *                        <li>{@link CompoundEventResult#interruptFalse(Object)} - перервати та скасувати операцію</li>
         *                      </ul>
         */
        CompoundEventResult<T> add(T instance, LivingEntity living, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> rootMessage);
    }

    /**
     * Функціональний інтерфейс для обробки події підвищення рівня духовного кореня.
     *
     * <p>Цей обробник викликається при кожному підвищенні рівня розвитку духовного кореня.
     * Обробники можуть модифікувати повідомлення, додавати додаткові ефекти або логувати прогрес.</p>
     *
     * <p>Приклад використання:</p>
     * <pre>{@code
     * SpiritualRootEvents.ADVANCE.register((instance, entity, advancement, notifyPlayer, rootMessage) -> {
     *     // Додати спеціальний ефект при підвищенні рівня
     *     if (entity instanceof Player player) {
     *         player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
     *     }
     *
     *     // Змінити повідомлення
     *     rootMessage.set(Component.literal("Ваш духовний корінь зріс у силі!"));
     * });
     * }</pre>
     */
    @FunctionalInterface
    interface SpiritualRootAdvanceEvent {
        /**
         * Обробляє підвищення рівня духовного кореня.
         *
         * @param instance      Екземпляр духовного кореня, що розвивається
         * @param entity        Істота, у якої розвивається корінь
         * @param advancement   {@code true}, якщо подія пов'язана з системою прогресу Minecraft
         * @param notifyPlayer  Змінювана змінна, що визначає чи повідомляти гравця про підвищення рівня
         * @param rootMessage   Змінюване повідомлення про підвищення рівня, що буде показано гравцю
         */
        void advance(SpiritualRootInstance instance, LivingEntity entity, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> rootMessage);
    }

    /**
     * Функціональний інтерфейс для обробки події отримання досвіду духовним коренем.
     *
     * <p>Цей обробник викликається кожного разу, коли духовний корінь отримує досвід.
     * Обробники можуть модифікувати кількість досвіду, додавати бонуси або застосовувати
     * додаткові ефекти.</p>
     *
     * <p>Приклад використання:</p>
     * <pre>{@code
     * SpiritualRootEvents.EXPERIENCE_GAIN.register((instance, entity, amount) -> {
     *     // Подвоїти досвід вночі
     *     if (entity.level().isNight()) {
     *         amount.set(amount.get() * 2.0f);
     *     }
     *
     *     // Додати візуальний ефект
     *     if (entity instanceof Player player) {
     *         player.level().addParticle(ParticleTypes.EXPERIENCE,
     *             player.getX(), player.getY() + 1, player.getZ(), 0, 0.1, 0);
     *     }
     * });
     * }</pre>
     *
     * <p><strong>Примітка:</strong> Параметр {@code amount} може бути обгорнутий у {@link Changeable}
     * в майбутніх версіях для можливості модифікації кількості досвіду.</p>
     */
    @FunctionalInterface
    interface SpiritualRootExperienceGainEvent {
        /**
         * Обробляє отримання досвіду духовним коренем.
         *
         * @param instance        Екземпляр духовного кореня, що отримує досвід
         * @param entity          Істота-власник духовного кореня
         * @param amount          Кількість досвіду, що отримується (у майбутніх версіях може бути {@link Changeable})
         */
        void gainExperience(SpiritualRootInstance instance, LivingEntity entity, float amount);
    }

    /**
     * Викликається перед тим, як розрахувати або встановити початкову чистоту духовного кореня.
     * Дозволяє модифікувати або повністю замінити значення чистоти (0.0f–1.0f).
     */
    @FunctionalInterface
    interface SpiritualRootPurityCalculateEvent<F> {
        CompoundEventResult<F> calculate(SpiritualRootInstance instance, LivingEntity entity, float purity);
    }

    /**
     * Викликається при зміні чистоти духовного кореня вручну або в результаті подій.
     * Може модифікувати нове значення перед встановленням.
     */
    @FunctionalInterface
    interface SpiritualRootPurityChangeEvent {
        void change(SpiritualRootInstance instance, LivingEntity entity, float oldPurity, Changeable<Float> newPurity);
    }

    @FunctionalInterface
    interface ForgetSpiritualRootEvent {
        EventResult forget(SpiritualRootInstance instance, LivingEntity entity, Changeable<MutableComponent> message);
    }
}