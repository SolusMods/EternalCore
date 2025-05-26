package io.github.solusmods.eternalcore.realm.api;

import com.mojang.datafixers.util.Pair;
import io.github.solusmods.eternalcore.realm.ModuleConstants;
import io.github.solusmods.eternalcore.stage.api.Stage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Абстрактний клас, що представляє Реалм  у системі EternalCore.
 * <p>
 * Реалм  визначає базові атрибути, здібності, механіки прориву (breakthrough)
 * та стадії розвитку для сутностей. Кожен Реалм  має унікальні характеристики,
 * що впливають на ігрову механіку.
 * </p>
 * <p>
 * Конкретні реалізації Реалмів  мають визначати базові атрибути, можливі шляхи
 * прориву та стадії розвитку. Реалми  організовані в ієрархію, де кожен наступний
 * Реалм  досягається через прорив і надає більші здібності.
 * </p>
 */
public abstract class Realm {
    /**
     * Мапа модифікаторів атрибутів, що застосовуються до сутності в цьому Реалмі
     */
    private final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();
    /**
     * Повертає тип Реалму.
     * <p>
     * Тип Реалму визначає його рівень у ієрархії Реалмів.
     * </p>
     *
     * @see RealmInstance#getType()
     */
    @Getter
    private final Type type;

    protected Realm(Type type) {
        this.type = type;
    }

    /**
     * Створює новий екземпляр Реалму з базовими налаштуваннями.
     * <p>
     * Цей метод використовується для створення екземпляра Реалму з його основними
     * характеристиками для призначення сутності.
     * </p>
     *
     * @return Новий екземпляр Реалму
     */
    public RealmInstance createDefaultInstance() {
        return new RealmInstance(this);
    }

    /**
     * Повертає базове здоров'я для цього Реалму .
     * <p>
     * Це значення визначає базовий максимум здоров'я, що сутність отримує
     * перебуваючи в цьому Реалмі .
     * </p>
     *
     * @return Базове значення здоров'я
     * @see RealmInstance#getBaseHealth()
     */
    public abstract double getBaseHealth();

    /**
     * Повертає пару значень мінімальної та максимальної кількості Ці для цього Реалму .
     * <p>
     * Ці значення визначають діапазон енергії Ці, доступний для сутностей у цьому Реалмі .
     * </p>
     *
     * @return Пара (мінімум, максимум) значень Ці
     * @see RealmInstance#getBaseQiRange()
     */
    public abstract Pair<Float, Float> getBaseQiRange();

    /**
     * Повертає базову силу атаки для цього Реалму .
     * <p>
     * Це значення визначає базову шкоду, яку сутність завдає при атаці.
     * </p>
     *
     * @return Базове значення сили атаки
     * @see RealmInstance#getBaseAttackDamage()
     */
    public abstract double getBaseAttackDamage();

    /**
     * Повертає базову швидкість атаки для цього Реалму .
     * <p>
     * Це значення визначає, як швидко сутність може атакувати.
     * </p>
     *
     * @return Базове значення швидкості атаки
     * @see RealmInstance#getBaseAttackSpeed()
     */
    public abstract double getBaseAttackSpeed();

    /**
     * Повертає опір відкиданню для цього Реалму .
     * <p>
     * Це значення визначає, наскільки сутність стійка до відкидання від атак.
     * </p>
     *
     * @return Значення опору відкиданню
     * @see RealmInstance#getKnockBackResistance()
     */
    public abstract double getKnockBackResistance();

    /**
     * Повертає висоту стрибка для цього Реалму .
     * <p>
     * Це значення визначає, наскільки високо сутність може стрибати.
     * </p>
     *
     * @return Значення висоти стрибка
     * @see RealmInstance#getJumpHeight()
     */
    public abstract double getJumpHeight();

    /**
     * Повертає швидкість руху для цього Реалму .
     * <p>
     * Це значення визначає, як швидко сутність може рухатися.
     * </p>
     *
     * @return Значення швидкості руху
     * @see RealmInstance#getMovementSpeed()
     */
    public abstract double getMovementSpeed();


    /**
     * Повертає швидкість бігу для цього Реалму .
     * <p>
     * За замовчуванням це значення розраховується як 130% від швидкості руху.
     * </p>
     *
     * @return Значення швидкості бігу
     * @see RealmInstance#getSprintSpeed()
     */
    public double getSprintSpeed() {
        return this.getMovementSpeed() * 1.3;
    }

    /**
     * Повертає мінімальне значення Ці для цього Реалму .
     * <p>
     * Це значення витягується з пари значень, що повертаються методом {@link #getBaseQiRange()}.
     * </p>
     *
     * @return Мінімальне значення Ці
     * @see RealmInstance#getMinBaseQi()
     */
    public float getMinBaseQi() {
        return this.getBaseQiRange().getFirst();
    }

    /**
     * Повертає максимальне значення Ці для цього Реалму .
     * <p>
     * Це значення витягується з пари значень, що повертаються методом {@link #getBaseQiRange()}.
     * </p>
     *
     * @return Максимальне значення Ці
     * @see RealmInstance#getMaxBaseQi()
     */
    public float getMaxBaseQi() {
        return this.getBaseQiRange().getSecond();
    }

    /**
     * Повертає коефіцієнт для розрахунку витрат Ці при прориві.
     * <p>
     * Показник ступеня визначає зростання витрати Кі при прориві. За замовчуванням 0.2.
     * </p>
     *
     * @return Коефіцієнт витрат Ці
     */
    public double getCoefficient() {
        return 0.2;
    }

    /**
     * Повертає список Реалмів , у які можливий прорив з цього Реалму .
     * <p>
     * Цей метод визначає можливі шляхи прогресії для сутності в поточному Реалмі .
     * </p>
     *
     * @param instance Екземпляр поточного Реалму
     * @param living   Сутність, для якої визначаються можливі прориви
     * @return Список Реалмів  для прориву
     * @see RealmInstance#getNextBreakthroughs(LivingEntity)
     */
    public abstract List<Realm> getNextBreakthroughs(RealmInstance instance, LivingEntity living);

    /**
     * Повертає список Реалмів , з яких можливий прорив у цей Реалм .
     * <p>
     * За замовчуванням повертає порожній список. Перевизначте цей метод для
     * встановлення ієрархії Реалмів .
     * </p>
     *
     * @param instance Екземпляр поточного Реалму
     * @param living   Сутність, для якої визначаються попередні Реалми
     * @return Список попередніх Реалмів
     * @see RealmInstance#getPreviousBreakthroughs(LivingEntity)
     */
    public abstract List<Realm> getPreviousBreakthroughs(RealmInstance instance, LivingEntity living);

    /**
     * Повертає Реалм , у який відбувається прорив за замовчуванням з цього Реалму .
     * <p>
     * Визначає основний шлях прогресії для сутності.
     * </p>
     *
     * @param instance Екземпляр поточного Реалму
     * @param living   Сутність, для якої визначається стандартний прорив
     * @return Реалм  для прориву або null, якщо прорив неможливий
     * @see RealmInstance#getDefaultBreakthrough(LivingEntity)
     */
    @Nullable
    public abstract Realm getDefaultBreakthrough(RealmInstance instance, LivingEntity living);

    /**
     * Повертає список стадій, доступних у цьому Реалмі .
     * <p>
     * Стадії представляють проміжні етапи розвитку в межах одного Реалму .
     * </p>
     *
     * @param instance Екземпляр поточного Реалму
     * @param living   Сутність, для якої визначаються стадії
     * @return Список стадій
     * @see RealmInstance#getRealmStages(LivingEntity)
     */
    public abstract List<Stage> getRealmStages(RealmInstance instance, LivingEntity living);

    /**
     * Повертає вимір та блок для відродження сутності в цьому Реалмі .
     * <p>
     * Визначає, де сутність відродиться після смерті та який блок буде
     * створено за відсутності валідної точки відродження.
     * </p>
     *
     * @param instance Екземпляр поточного Реалму
     * @param owner    Сутність, для якої визначається точка відродження
     * @return Пара (ключ виміру, стан блоку)
     * @see RealmInstance#getRespawnDimension(LivingEntity)
     */
    public Pair<ResourceKey<Level>, BlockState> getRespawnDimension(RealmInstance instance, LivingEntity owner) {
        return Pair.of(Level.OVERWORLD, Blocks.AIR.defaultBlockState());
    }

    /**
     * Визначає, чи є сутність пасивно дружньою до володаря цього Реалму .
     * <p>
     * За замовчуванням повертає false.
     * </p>
     *
     * @param entity Сутність для перевірки
     * @return true, якщо сутність дружня, false - в іншому випадку
     */
    public abstract boolean passivelyFriendlyWith(RealmInstance instance, LivingEntity entity);

    /**
     * Визначає, чи може сутність в цьому Реалмі  літати.
     * <p>
     * За замовчуванням повертає false.
     * </p>
     *
     * @return true, якщо політ дозволено, false - в іншому випадку
     */
    public abstract boolean canFly(RealmInstance instance, LivingEntity living);

    /**
     * Отримує ідентифікатор цього Реалму  з реєстру.
     *
     * @return Ідентифікатор Реалму  або null, якщо Реалм  не зареєстровано
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return RealmAPI.getRealmRegistry().getId(this);
    }

    /**
     * Отримує локалізовану назву цього Реалму .
     *
     * @return Компонент тексту з назвою Реалму  або null, якщо Реалм  не зареєстровано
     */
    @Nullable
    public MutableComponent getName() {
        ResourceLocation id = this.getRegistryName();
        return id == null ? null : Component.translatable(String.format("%s.realm.%s", id.getNamespace(), id.getPath().replace('/', '.'))).withStyle(type.getName().getStyle());
    }

    /**
     * Отримує локалізовану назву цього Реалму  з позначкою відстеження.
     * <p>
     * Використовується для відображення в меню, коли Реалм  відстежується гравцем.
     * </p>
     *
     * @return Компонент тексту з назвою Реалму  та позначкою відстеження або null
     */
    @Nullable
    public MutableComponent getTrackedName() {
        ResourceLocation id = this.getRegistryName();
        MutableComponent name = Component.translatable(String.format("%s.realm.%s", id.getNamespace(), id.getPath().replace('/', '.')));
        MutableComponent track = Component.translatable("%s.rank_menu.track".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.YELLOW);
        return name.append(track);
    }

    /**
     * Отримує ключ перекладу для назви цього Реалму .
     *
     * @return Ключ перекладу
     */
    public String getNameTranslationKey() {
        return ((TranslatableContents) this.getName().getContents()).getKey();
    }

    /**
     * Перевіряє рівність Реалмів  за їх ідентифікаторами.
     *
     * @param o Об'єкт для порівняння
     * @return true, якщо об'єкти рівні, false - в іншому випадку
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Realm realm = (Realm) o;
            return this.getRegistryName().equals(realm.getRegistryName());
        } else {
            return false;
        }
    }

    /**
     * Викликається, коли сутність встановлює цей Реалм  як активний.
     * <p>
     * Перевизначте цей метод для додавання власної логіки при встановленні Реалму .
     * </p>
     *
     * @param instance Екземпляр Реалму , що встановлюється
     * @param living   Сутність, яка встановлює Реалм
     * @see RealmInstance#onSet(LivingEntity)
     */
    public void onSet(RealmInstance instance, LivingEntity living) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається, коли сутність досягає цього Реалму .
     * <p>
     * Перевизначте цей метод для додавання власної логіки при досягненні Реалму .
     * </p>
     *
     * @param instance Екземпляр Реалму , якого досягають
     * @param living   Сутність, яка досягає Реалму
     * @see RealmInstance#onReach(LivingEntity)
     */
    public void onReach(RealmInstance instance, LivingEntity living) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається, коли сутність починає відстежувати цей Реалм .
     * <p>
     * Перевизначте цей метод для додавання власної логіки при відстеженні Реалму .
     * </p>
     *
     * @param instance Екземпляр Реалму , який відстежується
     * @param living   Сутність, яка відстежує Реалм
     * @see RealmInstance#onTrack(LivingEntity)
     */
    public void onTrack(RealmInstance instance, LivingEntity living) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається, коли сутність здійснює прорив у цей Реалм.
     * <p>
     * Перевизначте цей метод для додавання власної логіки при прориві.
     * </p>
     *
     * @param instance Екземпляр Реалму , в який відбувається прорив
     * @param living   Сутність, яка здійснює прорив
     * @see RealmInstance#onBreakthrough(LivingEntity)
     */
    public void onBreakthrough(RealmInstance instance, LivingEntity living) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається кожен тік для сутності, що має цей Реалм .
     * <p>
     * Перевизначте цей метод для додавання власної логіки, що виконується щотіка.
     * </p>
     *
     * @param instance Екземпляр активного Реалму
     * @param living   Сутність, що має цей Реалм
     */
    public void onTick(RealmInstance instance, LivingEntity living) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Додає модифікатор атрибуту до цього Реалму .
     * <p>
     * Модифікатори застосовуються до сутності, коли Реалм  встановлюється як активний.
     * </p>
     *
     * @param holder           Тримач атрибуту
     * @param resourceLocation Ідентифікатор модифікатора
     * @param amount           Значення модифікатора
     * @param operation        Операція модифікатора
     */
    public Realm addAttributeModifier(Holder<Attribute> holder, ResourceLocation resourceLocation, double amount, AttributeModifier.Operation operation) {
        this.attributeModifiers.put(holder, new AttributeTemplate(resourceLocation, amount, operation));
        return this;
    }

    public void createModifiers(RealmInstance instance, int i, BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
        this.attributeModifiers.forEach((holder, template) -> consumer.accept(holder, template.create(i)));
    }

    public void removeAttributeModifiers(RealmInstance instance, LivingEntity entity) {
        AttributeMap attributeMap = entity.getAttributes();
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
            if (attributeInstance != null) {
                attributeInstance.removeModifier((entry.getValue()).id());
            }
        }
    }

    public void addAttributeModifiers(RealmInstance instance, LivingEntity entity, int i) {
        AttributeMap attributeMap = entity.getAttributes();
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(entry.getValue().id());
                attributeInstance.addPermanentModifier((entry.getValue()).create(i));
            }
        }

    }


}