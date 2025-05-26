package io.github.solusmods.eternalcore.spiritual_root.api;

import io.github.solusmods.eternalcore.element.api.Element;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Абстрактний клас, що представляє Духовний Корінь у системі культивації EternalCore.
 * <p>
 * Духовний Корінь є основою культиваційного потенціалу сутності та визначає:
 * <ul>
 *     <li>Природні здібності та схильності до певного типу елементальної енергії</li>
 *     <li>Швидкість поглинання і культивації відповідної Ци</li>
 *     <li>Ефективність використання технік певного елементу</li>
 *     <li>Можливості прориву та розвитку по культивації</li>
 *     <li>Доступ до спеціальних шкіл, технік та артефактів</li>
 * </ul>
 * </p>
 * <p>
 * Система підтримує класичні п'ять стихій (Метал, Дерево, Вода, Вогонь, Земля)
 * та додаткові елементи (Повітря, Інь, Громовий, Темрява, Світло тощо).
 * </p>
 * <p>
 * Ефективність Духовного Кореня залежить від кількості активованих стихій:
 * <ul>
 *     <li>1 корінь - найвища ефективність, мінімальна гнучкість</li>
 *     <li>2-3 корені - збалансована ефективність та гнучкість</li>
 *     <li>4-5 коренів - висока гнучкість, низька ефективність</li>
 * </ul>
 * </p>
 *
 * @author EternalCore Team
 * @version 1.0.4.5
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public abstract class SpiritualRoot {

    // ==================== ПОЛЯ ====================

    /**
     * Тип кореня, що визначає його категорію та рідкісність.
     * <p>
     * Можливі типи включають:
     * <ul>
     *     <li>Звичайний - базовий тип духовного кореня</li>
     *     <li>Рідкісний - покращені характеристики</li>
     *     <li>Легендарний - унікальні здібності</li>
     *     <li>Небесний - найвищий рівень потенціалу</li>
     * </ul>
     * </p>
     */
    public final RootType type;

    /**
     * Мапа модифікаторів атрибутів, що застосовуються до сутності з цим Духовним Коренем.
     * <p>
     * Ключ - тримач атрибуту, значення - шаблон модифікатора з налаштуваннями.
     * Модифікатори автоматично застосовуються при активації кореня.
     * </p>
     */
    private final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();

    // ==================== КОНСТРУКТОРИ ТА СТВОРЕННЯ ЕКЗЕМПЛЯРІВ ====================

    /**
     * Створює новий екземпляр Духовного Кореня з базовими налаштуваннями.
     * <p>
     * Цей метод використовується для ініціалізації нового Духовного Кореня
     * при його призначенні сутності. Створений екземпляр містить всі базові
     * характеристики та готовий до використання.
     * </p>
     *
     * @return Новий екземпляр {@link SpiritualRootInstance} з базовими налаштуваннями
     */
    public SpiritualRootInstance createDefaultInstance() {
        return new SpiritualRootInstance(this);
    }

    // ==================== ІНФОРМАЦІЙНІ МЕТОДИ ====================

    /**
     * Отримує ідентифікатор цього Духовного Кореня з реєстру EternalCore.
     * <p>
     * Ідентифікатор використовується для серіалізації, локалізації та
     * ідентифікації кореня в системі.
     * </p>
     *
     * @return {@link ResourceLocation} ідентифікатор Духовного Кореня,
     *         або {@code null} якщо корінь не зареєстровано в системі
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return SpiritualRootAPI.getSpiritualRootRegistry().getId(this);
    }

    /**
     * Отримує локалізовану назву цього Духовного Кореня для відображення гравцю.
     * <p>
     * Назва автоматично генерується на основі ідентифікатора кореня
     * за шаблоном: {@code namespace.spiritual_root.path}
     * </p>
     *
     * @return {@link MutableComponent} з локалізованою назвою Духовного Кореня,
     *         або {@code null} якщо корінь не зареєстровано
     */
    @Nullable
    public MutableComponent getName() {
        ResourceLocation id = this.getRegistryName();
        return id == null ? null : Component.translatable(
                String.format("%s.spiritual_root.%s", id.getNamespace(), id.getPath().replace('/', '.'))
        );
    }

    /**
     * Повертає максимальний рівень майстерності для цього Духовного Кореня.
     * <p>
     * Максимальний рівень визначає межу розвитку кореня. Більшість коренів
     * можуть досягти рівня X, але деякі рідкісні корені можуть мати вищі межі.
     * </p>
     *
     * @return {@link RootLevels} максимальний рівень майстерності (за замовчуванням X)
     */
    public RootLevels getMaxLevel() {
        return RootLevels.X;
    }

    // ==================== МЕТОДИ УПРАВЛІННЯ АТРИБУТАМИ ====================

    /**
     * Додає модифікатор атрибуту до цього Духовного Кореня.
     * <p>
     * Модифікатори автоматично застосовуються до сутності при активації кореня
     * та видаляються при його деактивації. Це дозволяє кореням надавати
     * постійні бонуси до характеристик.
     * </p>
     *
     * @param holder           Тримач атрибуту, до якого застосовується модифікатор
     * @param resourceLocation Унікальний ідентифікатор модифікатора
     * @param amount           Значення модифікатора (може бути від'ємним)
     * @param operation        Тип операції модифікатора (додавання, множення тощо)
     */
    public void addAttributeModifier(Holder<Attribute> holder, ResourceLocation resourceLocation,
                                     double amount, AttributeModifier.Operation operation) {
        this.attributeModifiers.put(holder, new AttributeTemplate(resourceLocation, amount, operation));
    }

    /**
     * Застосовує всі модифікатори атрибутів цього Духовного Кореня до вказаної сутності.
     * <p>
     * Цей метод викликається автоматично при встановленні Духовного Кореня як активного.
     * Усі існуючі модифікатори з тим же ідентифікатором спочатку видаляються,
     * а потім додаються нові для уникнення дублювання.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня, що містить поточний стан
     * @param entity   Сутність, до якої застосовуються модифікатори атрибутів
     */
    public void addAttributeModifiers(SpiritualRootInstance instance, LivingEntity entity) {
        if (this.attributeModifiers.isEmpty()) return;

        AttributeMap attributeMap = entity.getAttributes();
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());

            if (attributeInstance == null) continue;
            attributeInstance.removeModifier(entry.getValue().id());
            attributeInstance.addPermanentModifier(entry.getValue().create());
        }
    }

    /**
     * Видаляє всі модифікатори атрибутів цього Духовного Кореня від вказаної сутності.
     * <p>
     * Цей метод викликається при деактивації Духовного Кореня або його заміні.
     * Для серверних гравців автоматично надсилається пакет оновлення атрибутів.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня, що деактивується
     * @param entity   Сутність, від якої видаляються модифікатори атрибутів
     */
    public void removeAttributeModifiers(SpiritualRootInstance instance, LivingEntity entity) {
        if (this.attributeModifiers.isEmpty()) return;

        AttributeMap map = entity.getAttributes();
        List<AttributeInstance> dirtyInstances = new ArrayList<>();

        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = map.getInstance(entry.getKey());
            if (attributeInstance == null) continue;
            attributeInstance.removeModifier(entry.getValue().id());
            dirtyInstances.add(attributeInstance);
        }

        if (!dirtyInstances.isEmpty() && entity instanceof ServerPlayer player) {
            ClientboundUpdateAttributesPacket packet = new ClientboundUpdateAttributesPacket(player.getId(), dirtyInstances);
            player.connection.send(packet);
        }
    }

    // ==================== МЕТОДИ РОЗВИТКУ ТА ПРОГРЕСІЇ ====================

    /**
     * Збільшує силу (чистоту) Духовного Кореня на вказану величину.
     * <p>
     * Сила кореня впливає на ефективність культивації та потужність технік.
     * Максимальне значення сили обмежене 1.0 (100% чистоти).
     * Вища чистота кореня означає кращу спорідненість з відповідним елементом.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня, що підлягає покращенню
     * @param living   Сутність, чия сила кореня збільшується
     * @param amount   Величина збільшення сили (від 0.0 до 1.0)
     */
    public void increaseStrength(SpiritualRootInstance instance, LivingEntity living, float amount) {
        instance.setStrength(Math.min(1.0f, instance.getStrength() + amount));
    }

    /**
     * Перевіряє, чи може вказаний екземпляр Духовного Кореня просунутися на наступний рівень.
     * <p>
     * Умови просування можуть включати:
     * <ul>
     *     <li>Достатній рівень досвіду</li>
     *     <li>Необхідні ресурси для прориву</li>
     *     <li>Відповідне середовище для культивації</li>
     *     <li>Особливі умови для рідкісних коренів</li>
     * </ul>
     * </p>
     *
     * @param spiritualRootInstance Екземпляр Духовного Кореня для перевірки
     * @param entity               Сутність, що бажає просунутися
     * @return {@code true} якщо просування можливе, {@code false} в іншому випадку
     */
    public boolean canAdvance(SpiritualRootInstance spiritualRootInstance, LivingEntity entity) {
        return false;
    }

    // ==================== АБСТРАКТНІ МЕТОДИ ЕЛЕМЕНТІВ ТА ПРОГРЕСІЇ ====================

    /**
     * Повертає елемент, пов'язаний з цим Духовним Коренем.
     * <p>
     * Елемент визначає тип елементальної енергії (Ци), з якою сутність має
     * найбільшу спорідненість. Це впливає на:
     * <ul>
     *     <li>Швидкість поглинання відповідної Ци з навколишнього середовища</li>
     *     <li>Ефективність елементальних технік</li>
     *     <li>Взаємодію з іншими елементами (синергія або конфлікти)</li>
     *     <li>Доступ до специфічних локацій та ресурсів</li>
     * </ul>
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param entity   Сутність, для якої визначається елемент
     * @return {@link Element} пов'язаний з коренем, або {@code null} якщо елемент не визначено
     */
    public abstract @Nullable Element getElement(SpiritualRootInstance instance, LivingEntity entity);

    /**
     * Повертає перший ступінь еволюції цього Духовного Кореня.
     * <p>
     * Система еволюції дозволяє коренями розвиватися в більш потужні форми.
     * Перший ступінь зазвичай покращує базові характеристики та може
     * відкривати доступ до нових здібностей.
     * </p>
     * <p>
     * Приклад еволюції: Звичайний Вогняний Корінь → Пекельний Вогняний Корінь
     * </p>
     *
     * @param instance Екземпляр поточного Духовного Кореня
     * @param living   Сутність, для якої визначається шлях еволюції
     * @return Еволюціонований Духовний Корінь або {@code null}, якщо еволюція неможлива
     * @see SpiritualRootInstance#getFirstDegree(LivingEntity)
     */
    @Nullable
    public abstract SpiritualRoot getFirstDegree(SpiritualRootInstance instance, LivingEntity living);

    /**
     * Повертає другий ступінь еволюції цього Духовного Кореня.
     * <p>
     * Другий ступінь еволюції представляє вищий рівень розвитку кореня
     * з додатковими унікальними здібностями та значно покращеними характеристиками.
     * Досягнення другого ступеня зазвичай вимагає виконання особливих умов.
     * </p>
     *
     * @param spiritualRootInstance Екземпляр поточного Духовного Кореня
     * @param living               Сутність, для якої визначається шлях еволюції
     * @return Еволюціонований Духовний Корінь другого ступеня або {@code null}, якщо еволюція неможлива
     * @see SpiritualRootInstance#getSecondDegree(LivingEntity)
     */
    public @Nullable SpiritualRoot getSecondDegree(SpiritualRootInstance spiritualRootInstance, LivingEntity living) {
        return null;
    }

    /**
     * Повертає протилежний Духовний Корінь для поточного.
     * <p>
     * Протилежні Духовні Корені мають конфліктуючі елементальні властивості
     * та можуть створювати проблеми при одночасному культивуванні.
     * </p>
     * <p>
     * Приклади протилежностей:
     * <ul>
     *     <li>Вогонь ↔ Вода</li>
     *     <li>Метал ↔ Дерево</li>
     *     <li>Світло ↔ Темрява</li>
     *     <li>Інь ↔ Ян</li>
     * </ul>
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param entity   Сутність, для якої визначається протилежний Корінь
     * @return Протилежний Духовний Корінь або {@code null} якщо протилежності немає
     */
    public @Nullable SpiritualRoot getOpposite(SpiritualRootInstance instance, LivingEntity entity) {
        return null;
    }

    // ==================== МЕТОДИ ПОДІЙ ====================

    /**
     * Викликається при першому отриманні цього Духовного Кореня сутністю.
     * <p>
     * Цей метод дозволяє додати спеціальну логіку, що виконується одразу
     * після призначення кореня сутності. Наприклад:
     * <ul>
     *     <li>Надання початкових бонусів</li>
     *     <li>Активація пасивних здібностей</li>
     *     <li>Зміна зовнішнього вигляду сутності</li>
     *     <li>Додавання спеціальних ефектів</li>
     * </ul>
     * </p>
     *
     * @param instance Новий екземпляр Духовного Кореня
     * @param living   Сутність, яка отримала Духовний Корінь
     */
    public void onAdd(SpiritualRootInstance instance, LivingEntity living) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається при просуванні сутності на наступний рівень цього Духовного Кореня.
     * <p>
     * Цей метод автоматично запускає подію {@link SpiritualRootEvents#ADVANCE}
     * та може містити додаткову логіку, специфічну для конкретного типу кореня.
     * </p>
     * <p>
     * Типові дії при просуванні:
     * <ul>
     *     <li>Збільшення потужності модифікаторів атрибутів</li>
     *     <li>Розблокування нових здібностей</li>
     *     <li>Покращення ефективності культивації</li>
     *     <li>Візуальні ефекти прориву</li>
     * </ul>
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня, що просувається
     * @param living   Сутність, яка досягла наступного рівня
     */
    public void onAdvance(SpiritualRootInstance instance, LivingEntity living) {
        SpiritualRootEvents.ADVANCE.invoker().advance(instance, living, false, Changeable.of(false), null);
    }

    /**
     * Викликається при отриманні досвіду для цього Духовного Кореня.
     * <p>
     * Метод автоматично запускає подію {@link SpiritualRootEvents#EXPERIENCE_GAIN}
     * та може містити додаткову логіку обробки досвіду.
     * </p>
     * <p>
     * Досвід може отримуватися через:
     * <ul>
     *     <li>Медитацію в підходящому середовищі</li>
     *     <li>Використання технік відповідного елементу</li>
     *     <li>Поглинання елементальних ресурсів</li>
     *     <li>Участь у бойових діях</li>
     * </ul>
     * </p>
     *
     * @param spiritualRootInstance Екземпляр Духовного Кореня, що отримує досвід
     * @param entity               Сутність, яка культивує корінь
     */
    public void onAddExperience(SpiritualRootInstance spiritualRootInstance, LivingEntity entity) {
        SpiritualRootEvents.EXPERIENCE_GAIN.invoker().gainExperience(spiritualRootInstance, entity, 0.0F);
    }
}