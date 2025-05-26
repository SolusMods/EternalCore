package io.github.solusmods.eternalcore.spiritual_root.api;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.element.api.Element;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Екземпляр Духовного Кореня, що представляє конкретну реалізацію {@link SpiritualRoot} для сутності.
 * <p>
 * Цей клас є контейнером для збереження стану розвитку конкретного Духовного Кореня у сутності.
 * Він містить всю інформацію про поточний прогрес культивації, включаючи:
 * <ul>
 *     <li>Рівень розвитку кореня (від I до X)</li>
 *     <li>Накопичений досвід культивації</li>
 *     <li>Силу (чистоту) кореня (0.0 - 1.0)</li>
 *     <li>Статус активності для доступу до технік</li>
 *     <li>Додаткові дані для розширення функціональності</li>
 * </ul>
 * </p>
 * <p>
 * Кожен екземпляр прив'язаний до конкретного типу {@link SpiritualRoot} через реєстр,
 * що забезпечує цілісність даних при серіалізації/десеріалізації.
 * </p>
 * <p>
 * Система відстеження змін (dirty tracking) забезпечує ефективну синхронізацію
 * між сервером та клієнтом тільки при необхідності.
 * </p>
 *
 * @author EternalCore Team
 * @version 1.0.4.5
 * @since 1.0
 * @see SpiritualRoot
 * @see RootLevels
 */
@Getter
@Setter
public class SpiritualRootInstance implements Cloneable {

    // ==================== КОНСТАНТИ ДЛЯ СЕРІАЛІЗАЦІЇ ====================

    /** Ключ для збереження основного ідентифікатора духовного кореня в NBT */
    public static final String KEY = "spiritual_root";

    /** Ключ для збереження рівня розвитку кореня в NBT */
    public static final String LEVEL_KEY = "level";

    /** Ключ для збереження накопиченого досвіду в NBT */
    public static final String EXPERIENCE_KEY = "experience";

    /** Ключ для збереження сили (чистоти) кореня в NBT */
    public static final String STRENGTH_KEY = "strength";
    public static final String PURITY_KEY = "Purity";

    // ==================== ОСНОВНІ ПОЛЯ ====================

    /**
     * Постачальник реєстру для отримання типу духовного кореня.
     * <p>
     * Використання RegistrySupplier забезпечує ліниве завантаження та
     * стабільність посилань при перезавантаженні модів.
     * </p>
     */
    protected final RegistrySupplier<SpiritualRoot> spiritualRootRegistrySupplier;

    /**
     * Сила (чистота) духовного кореня від 0.0 до 1.0.
     * <p>
     * Більша сила означає:
     * <ul>
     *     <li>Вищу ефективність культивації відповідної стихії</li>
     *     <li>Потужніші техніки та здібності</li>
     *     <li>Кращу спорідненість з елементальною енергією</li>
     *     <li>Швидший прогрес у розвитку</li>
     * </ul>
     * </p>
     */
    private float strength = 0.0F;

    /**
     * Поточний рівень розвитку духовного кореня.
     * <p>
     * Рівні прогресують від I до X, кожен наступний рівень вимагає
     * експоненційно більше досвіду та розблоковує нові можливості.
     * </p>
     */
    private RootLevels level = RootLevels.I;

    /**
     * Накопичений досвід культивації.
     * <p>
     * Досвід накопичується через медитацію, використання технік,
     * поглинання елементальних ресурсів та інші активності культивації.
     * При досягненні порогового значення відбувається автоматичне просування.
     * </p>
     */
    private float experience = 0.0F;

    /**
     * Чи є доступ до технік цього духовного кореня.
     * <p>
     * Тільки активні корені дозволяють використовувати відповідні техніки
     * та надають бонуси до атрибутів. Сутність може мати декілька коренів,
     * але не всі з них обов'язково активні одночасно.
     * </p>
     */
    private boolean active = false;

    /**
     * Чистота (purity) духовного кореня — показник якості від 0.0 до 1.0.
     * Висока чистота підвищує ефективність культивації, шанс прориву, та зменшує негативні ефекти.
     */
    private float purity;


    /**
     * Додаткові дані для розширення функціональності.
     * <p>
     * Цей тег може використовуватися підкласами або додатками для
     * збереження специфічної інформації, що не покривається базовими полями.
     * </p>
     */
    @Nullable
    private CompoundTag tag = null;

    /**
     * Прапорець, що вказує на необхідність синхронізації з клієнтом.
     * <p>
     * Автоматично встановлюється при зміні важливих даних екземпляра
     * та скидається після успішної синхронізації.
     * </p>
     */
    @Getter
    private boolean dirty = false;

    // ==================== КОНСТРУКТОРИ ТА СТВОРЕННЯ ====================

    /**
     * Створює новий екземпляр духовного кореня для вказаного типу.
     * <p>
     * Конструктор ініціалізує екземпляр з базовими значеннями:
     * <ul>
     *     <li>Рівень I (початковий)</li>
     *     <li>Нульовий досвід</li>
     *     <li>Нульова сила</li>
     *     <li>Неактивний стан</li>
     * </ul>
     * </p>
     *
     * @param spiritualRoot Тип духовного кореня для створення екземпляра
     */
    protected SpiritualRootInstance(SpiritualRoot spiritualRoot) {
        this.spiritualRootRegistrySupplier = SpiritualRootAPI.getSpiritualRootRegistry()
                .delegate(SpiritualRootAPI.getSpiritualRootRegistry().getId(spiritualRoot));
    }

    /**
     * Створює екземпляр духовного кореня з даних NBT.
     * <p>
     * Цей метод використовується для відновлення збережених духовних коренів
     * із файлів світу або при передачі даних між сервером та клієнтом.
     * </p>
     * <p>
     * NBT тег повинен містити всі необхідні дані, створені методом {@link #toNBT()}.
     * </p>
     *
     * @param tag NBT тег з серіалізованими даними екземпляра
     * @return Відновлений екземпляр духовного кореня
     * @throws NullPointerException якщо в реєстрі не знайдено духовний корінь з вказаним ідентифікатором
     * @see #toNBT()
     */
    public static SpiritualRootInstance fromNBT(CompoundTag tag) throws NullPointerException {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString(KEY));
        SpiritualRoot spiritualRoot = SpiritualRootAPI.getSpiritualRootRegistry().get(location);
        if (spiritualRoot == null) {
            throw new NullPointerException("No spiritualRoot found for location: " + location);
        }
        SpiritualRootInstance instance = spiritualRoot.createDefaultInstance();
        instance.deserialize(tag);
        return instance;
    }

    // ==================== МЕТОДИ ДОСТУПУ ДО БАЗОВОЇ ІНФОРМАЦІЇ ====================

    /**
     * Отримує тип духовного кореня для цього екземпляра.
     * <p>
     * Повертає базовий {@link SpiritualRoot}, що визначає поведінку
     * та характеристики цього екземпляра.
     * </p>
     *
     * @return Тип духовного кореня
     */
    public SpiritualRoot getSpiritualRoot() {
        return spiritualRootRegistrySupplier.get();
    }

    /**
     * Отримує ідентифікатор типу духовного кореня.
     * <p>
     * Ідентифікатор використовується для серіалізації, реєстрації
     * та ідентифікації типу кореня в системі.
     * </p>
     *
     * @return ResourceLocation ідентифікатор типу духовного кореня
     */
    public ResourceLocation getSpiritualRootId() {
        return this.spiritualRootRegistrySupplier.getId();
    }

    /**
     * Отримує локалізовану назву духовного кореня для відображення.
     * <p>
     * Назва автоматично локалізується відповідно до мови клієнта
     * та може використовуватися в інтерфейсі користувача.
     * </p>
     *
     * @return Локалізований компонент з назвою духовного кореня
     */
    public MutableComponent getDisplayName() {
        return this.getSpiritualRoot().getName();
    }

    /**
     * Отримує тип (категорію) цього духовного кореня.
     * <p>
     * Тип визначає рідкісність, потенціал та особливі характеристики кореня.
     * </p>
     *
     * @return Тип духовного кореня
     * @see RootType
     */
    public RootType getType() {
        return this.getSpiritualRoot().getType();
    }

    /**
     * Перевіряє, чи належить цей духовний корінь до вказаного тегу.
     * <p>
     * Теги використовуються для групування духовних коренів за певними критеріями,
     * такими як стихія, рідкісність чи походження.
     * </p>
     *
     * @param tag Тег для перевірки
     * @return true, якщо корінь належить до тегу
     */
    public boolean is(TagKey<SpiritualRoot> tag) {
        return this.spiritualRootRegistrySupplier.is(tag);
    }

    // ==================== МЕТОДИ СЕРІАЛІЗАЦІЇ ====================

    /**
     * Серіалізує екземпляр духовного кореня в NBT формат.
     * <p>
     * Цей метод забезпечує збереження всієї необхідної інформації
     * для повного відновлення стану екземпляра. Включає базові дані
     * та викликає {@link #serialize(CompoundTag)} для додаткових даних.
     * </p>
     *
     * @return CompoundTag з усіма даними екземпляра
     * @see #fromNBT(CompoundTag)
     * @see #serialize(CompoundTag)
     */
    public final CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(KEY, this.getSpiritualRootId().toString());
        serialize(nbt);
        return nbt;
    }

    /**
     * Серіалізує специфічні дані екземпляра в NBT тег.
     * <p>
     * Цей метод може бути перевизначений підкласами для збереження
     * додаткових даних. Базова реалізація зберігає:
     * <ul>
     *     <li>Рівень розвитку</li>
     *     <li>Накопичений досвід</li>
     *     <li>Силу кореня</li>
     *     <li>Додаткові теги (якщо є)</li>
     * </ul>
     * </p>
     *
     * @param nbt NBT тег для збереження даних
     * @return Тей же NBT тег з доданими даними
     * @see #deserialize(CompoundTag)
     */
    public CompoundTag serialize(CompoundTag nbt) {
        if (this.tag != null) nbt.put("tag", this.tag.copy());
        nbt.putInt(LEVEL_KEY, this.level.getLevel());
        nbt.putFloat(EXPERIENCE_KEY, this.experience);
        nbt.putFloat(STRENGTH_KEY, this.strength);
        nbt.putFloat(PURITY_KEY, purity); // нове поле
        return nbt;
    }

    /**
     * Десеріалізує дані екземпляра з NBT тегу.
     * <p>
     * Відновлює стан екземпляра з збережених даних. Метод безпечно
     * обробляє відсутні поля, використовуючи значення за замовчуванням.
     * </p>
     *
     * @param tag NBT тег з збереженими даними
     * @see #serialize(CompoundTag)
     */
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
        if (tag.contains(LEVEL_KEY)) this.level = RootLevels.byId(tag.getInt(LEVEL_KEY));
        if (tag.contains(EXPERIENCE_KEY)) this.experience = tag.getFloat(EXPERIENCE_KEY);
        if (tag.contains(PURITY_KEY)) this.purity = tag.getFloat(PURITY_KEY);
        if (tag.contains(STRENGTH_KEY)) this.strength = tag.getFloat(STRENGTH_KEY);
    }

    // ==================== МЕТОДИ УПРАВЛІННЯ СТАНОМ ====================

    /**
     * Позначає екземпляр як змінений для синхронізації з клієнтом.
     * <p>
     * Цей метод автоматично викликається при зміні важливих параметрів
     * та сигналізує системі про необхідність синхронізації даних.
     * </p>
     */
    public void markDirty() {
        this.dirty = true;
    }

    /**
     * Скидає прапорець синхронізації після успішної передачі даних клієнту.
     * <p>
     * <strong>УВАГА:</strong> Цей метод призначений тільки для внутрішнього
     * використання системою синхронізації. Не викликайте його самостійно!
     * </p>
     */
    @ApiStatus.Internal
    public void resetDirty() {
        this.dirty = false;
    }

    /**
     * Встановлює рівень розвитку духовного кореня.
     * <p>
     * Автоматично позначає екземпляр як змінений для синхронізації.
     * </p>
     *
     * @param level Новий рівень розвитку
     */
    public void setLevel(RootLevels level) {
        this.level = level;
        markDirty();
    }

    /**
     * Встановлює накопичений досвід культивації.
     * <p>
     * Автоматично позначає екземпляр як змінений для синхронізації.
     * </p>
     *
     * @param experience Нове значення досвіду
     */
    public void setExperience(float experience) {
        this.experience = experience;
        markDirty();
    }

    /**
     * Встановлює силу (чистоту) духовного кореня.
     * <p>
     * Автоматично позначає екземпляр як змінений для синхронізації.
     * Значення обмежується діапазоном 0.0 - 1.0.
     * </p>
     *
     * @param strength Нове значення сили (0.0 - 1.0)
     */
    public void setStrength(float strength) {
        this.strength = strength;
        markDirty();
    }

    public void setPurity(float purity) {
        this.purity = purity;
        markDirty();
    }

    // ==================== МЕТОДИ ПРОГРЕСІЇ ТА РОЗВИТКУ ====================

    /**
     * Оновлює рівень духовного кореня на основі накопиченого досвіду.
     * <p>
     * Автоматично просуває корінь на наступні рівні, якщо накопичено
     * достатньо досвіду. Процес продовжується до досягнення максимального
     * рівня або вичерпання досвіду.
     * </p>
     * <p>
     * При кожному просуванні викликається метод {@link #onAdvance(LivingEntity)}
     * для активації відповідних ефектів та подій.
     * </p>
     *
     * @param entity Сутність, чий духовний корінь розвивається
     */
    public void updateLevel(LivingEntity entity) {
        while (experience >= level.getExperience() && level != getSpiritualRoot().getMaxLevel()) {
            level = level.getNext();
            onAdvance(entity);
        }
    }

    /**
     * Перевіряє, чи може духовний корінь просунутися на наступний рівень.
     * <p>
     * Умови просування можуть включати достатній досвід, наявність ресурсів,
     * підходяще середовище або виконання спеціальних завдань.
     * </p>
     *
     * @param entity Сутність, що бажає просунути корінь
     * @return true, якщо просування можливе
     */
    public boolean canAdvance(LivingEntity entity) {
        return this.getSpiritualRoot().canAdvance(this, entity);
    }

    /**
     * Збільшує силу духовного кореня на вказану величину.
     * <p>
     * Делегує виклик до базового типу духовного кореня для
     * застосування специфічної логіки покращення.
     * </p>
     *
     * @param living Сутність, чия сила кореня збільшується
     * @param amount Величина збільшення сили
     */
    public void increaseStrength(LivingEntity living, float amount) {
        this.getSpiritualRoot().increaseStrength(this, living, amount);
    }

    // ==================== МЕТОДИ ЕЛЕМЕНТІВ ТА ЕВОЛЮЦІЇ ====================

    /**
     * Отримує елемент, пов'язаний з цим духовним коренем.
     * <p>
     * Елемент визначає тип елементальної енергії та впливає на
     * взаємодію з навколишнім середовищем і техніками.
     * </p>
     *
     * @param entity Сутність, для якої визначається елемент
     * @return Елемент духовного кореня або null
     */
    public @Nullable Element getElement(LivingEntity entity) {
        return this.getSpiritualRoot().getElement(this, entity);
    }

    /**
     * Отримує перший ступінь еволюції цього духовного кореня.
     * <p>
     * Еволюція дозволяє корению розвинутися в більш потужну форму
     * з покращеними характеристиками та новими здібностями.
     * </p>
     *
     * @param living Сутність, для якої визначається еволюція
     * @return Еволюціонований духовний корінь або null
     */
    @Nullable
    public SpiritualRoot getFirstDegree(LivingEntity living) {
        return this.getSpiritualRoot().getFirstDegree(this, living);
    }

    /**
     * Отримує другий ступінь еволюції цього духовного кореня.
     * <p>
     * Другий ступінь представляє найвищий рівень еволюції з унікальними
     * здібностями та значно покращеними характеристиками.
     * </p>
     *
     * @param living Сутність, для якої визначається еволюція
     * @return Еволюціонований духовний корінь другого ступеня або null
     */
    @Nullable
    public SpiritualRoot getSecondDegree(LivingEntity living) {
        return this.getSpiritualRoot().getSecondDegree(this, living);
    }

    /**
     * Отримує протилежний духовний корінь для поточного.
     * <p>
     * Протилежні корені мають конфліктуючі елементальні властивості
     * та можуть створювати проблеми при одночасному культивуванні.
     * </p>
     *
     * @param entity Сутність, для якої визначається протилежний корінь
     * @return Протилежний духовний корінь або null
     */
    public @Nullable SpiritualRoot getOpposite(LivingEntity entity) {
        return getSpiritualRoot().getOpposite(this, entity);
    }

    // ==================== МЕТОДИ УПРАВЛІННЯ АТРИБУТАМИ ====================

    /**
     * Застосовує модифікатори атрибутів цього духовного кореня до сутності.
     * <p>
     * Викликається при активації кореня для надання бонусів до характеристик.
     * Модифікатори можуть включати збільшення сили, швидкості, здоров'я тощо.
     * </p>
     *
     * @param entity Сутність, до якої застосовуються модифікатори
     */
    public void addAttributeModifiers(LivingEntity entity) {
        this.getSpiritualRoot().addAttributeModifiers(this, entity);
    }

    /**
     * Видаляє модифікатори атрибутів цього духовного кореня від сутності.
     * <p>
     * Викликається при деактивації кореня або його заміні для видалення
     * раніше застосованих бонусів.
     * </p>
     *
     * @param entity Сутність, від якої видаляються модифікатори
     */
    public void removeAttributeModifiers(LivingEntity entity) {
        this.getSpiritualRoot().removeAttributeModifiers(this, entity);
    }

    // ==================== МЕТОДИ ПОДІЙ ЖИТТЄВОГО ЦИКЛУ ====================

    /**
     * Викликається при першому отриманні цього духовного кореня сутністю.
     * <p>
     * Дозволяє виконати ініціалізацію, надати початкові бонуси
     * або активувати спеціальні ефекти при отриманні кореня.
     * </p>
     *
     * @param living Сутність, яка отримала духовний корінь
     */
    public void onAdd(LivingEntity living) {
        this.getSpiritualRoot().onAdd(this, living);
    }

    /**
     * Викликається при просуванні духовного кореня на наступний рівень.
     * <p>
     * Дозволяє виконати дії, специфічні для просування: покращення
     * модифікаторів, розблокування здібностей, візуальні ефекти тощо.
     * </p>
     *
     * @param living Сутність, чий духовний корінь просувається
     */
    public void onAdvance(LivingEntity living) {
        this.getSpiritualRoot().onAdvance(this, living);
    }

    /**
     * Викликається при отриманні досвіду для цього духовного кореня.
     * <p>
     * Дозволяє додати спеціальну логіку обробки досвіду, такі як
     * бонусні ефекти або додаткові нарахування.
     * </p>
     *
     * @param entity Сутність, яка отримує досвід культивації
     */
    public void onAddExperience(LivingEntity entity) {
        this.getSpiritualRoot().onAddExperience(this, entity);
    }

    // ==================== СИСТЕМНІ МЕТОДИ ====================

    /**
     * Створює повну копію цього екземпляра духовного кореня.
     * <p>
     * Копіюються всі дані, включаючи рівень, досвід, силу та додаткові теги.
     * Стан синхронізації (dirty) також копіюється.
     * </p>
     *
     * @return Незалежна копія екземпляра
     */
    @Override
    public SpiritualRootInstance clone() {
        try {
            SpiritualRootInstance clone = (SpiritualRootInstance) super.clone();
            clone.dirty = this.dirty;
            if (this.tag != null) clone.tag = this.tag.copy();
            clone.level = this.level;
            clone.experience = this.experience;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Перевіряє рівність цього екземпляра з іншим об'єктом.
     * <p>
     * Два екземпляри вважаються рівними, якщо вони посилаються на
     * той же тип духовного кореня в тому ж реєстрі.
     * </p>
     *
     * @param o Об'єкт для порівняння
     * @return true, якщо об'єкти рівні
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpiritualRootInstance instance = (SpiritualRootInstance) o;
        return this.getSpiritualRootId().equals(instance.getSpiritualRootId()) &&
                spiritualRootRegistrySupplier.getRegistryKey().equals(instance.spiritualRootRegistrySupplier.getRegistryKey());
    }

    /**
     * Обчислює хеш-код для цього екземпляра.
     * <p>
     * Хеш-код базується на ідентифікаторі духовного кореня та ключі реєстру.
     * </p>
     *
     * @return Хеш-код екземпляра
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getSpiritualRootId(), spiritualRootRegistrySupplier.getRegistryKey());
    }
}