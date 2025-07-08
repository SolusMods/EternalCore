package io.github.solusmods.eternalcore.api.spiritual_root;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

/**
 * Інтерфейс подій системи духовних коренів для Minecraft 1.21.1 з використанням Architectury API.
 *
 * <p>Цей інтерфейс визначає події, які можуть бути зареєстровані та оброблені іншими модами
 * для взаємодії з системою духовних коренів. Всі події працюють з об'єктами {@link AbstractSpiritualRoot}
 * та живими істотами {@link LivingEntity}.</p>
 *
 * <p>Події дозволяють:</p>
 * <ul>
 *   <li>Модифікувати поведінку при додаванні нових духовних коренів</li>
 *   <li>Втрутитися в процес підвищення рівня розвитку</li>
 *   <li>Змінити механіку отримання досвіду</li>
 *   <li>Контролювати чистоту духовного кореня</li>
 * </ul>
 *
 * @version 2.0.0
 * @see AbstractSpiritualRoot
 * @see LivingEntity
 * @since 1.1
 */
public interface SpiritualRootEvents {

    Event<SpiritualRootAddEvent<AbstractSpiritualRoot>> ADD = EventFactory.createCompoundEventResult();

    Event<SpiritualRootAdvanceEvent> ADVANCE = EventFactory.createLoop();

    Event<SpiritualRootExperienceGainEvent> EXPERIENCE_GAIN = EventFactory.createLoop();

    Event<SpiritualRootPurityCalculateEvent<Float>> CALCULATE_PURITY = EventFactory.createCompoundEventResult();

    Event<UpdateSpiritualRootEvent> UPDATE = EventFactory.createEventResult();

    Event<ForgetSpiritualRootEvent> FORGET_SPIRITUAL_ROOT = EventFactory.createEventResult();

    /**
     * Обробка події додавання нового духовного кореня до істоти.
     *
     * @param <T> Тип об'єкта духовного кореня
     */
    @FunctionalInterface
    interface SpiritualRootAddEvent<T> {
        CompoundEventResult<T> add(T root, LivingEntity entity, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> rootMessage);
    }

    /**
     * Обробка події підвищення рівня духовного кореня.
     */
    @FunctionalInterface
    interface SpiritualRootAdvanceEvent {
        void advance(AbstractSpiritualRoot root, LivingEntity entity, boolean advancement, Changeable<Boolean> notifyPlayer, Changeable<MutableComponent> rootMessage);
    }

    /**
     * Обробка події отримання досвіду духовним коренем.
     */
    @FunctionalInterface
    interface SpiritualRootExperienceGainEvent {
        void gainExperience(AbstractSpiritualRoot root, LivingEntity entity);
    }

    /**
     * Обробка події обчислення чистоти духовного кореня.
     */
    @FunctionalInterface
    interface SpiritualRootPurityCalculateEvent<F> {
        CompoundEventResult<F> calculate(AbstractSpiritualRoot root, LivingEntity entity, float purity);
    }

    /**
     * Обробка події зміни чистоти духовного кореня.
     */
    @FunctionalInterface
    interface UpdateSpiritualRootEvent {
        EventResult update(AbstractSpiritualRoot root, LivingEntity entity, Changeable<MutableComponent> message);
    }

    /**
     * Обробка події забування духовного кореня.
     */
    @FunctionalInterface
    interface ForgetSpiritualRootEvent {
        EventResult forget(AbstractSpiritualRoot root, LivingEntity entity, Changeable<MutableComponent> message);
    }
}
