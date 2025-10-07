package io.github.solusmods.eternalcore.api.realm;

import com.mojang.datafixers.util.Pair;
import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.ServerConfigs;
import io.github.solusmods.eternalcore.api.data.IResource;
import io.github.solusmods.eternalcore.api.registry.RealmRegistry;
import io.github.solusmods.eternalcore.api.registry.SpiritualRootRegistry;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import io.github.solusmods.eternalcore.api.storage.INBTSerializable;
import io.github.solusmods.eternalcore.config.RealmConfig;
import io.github.solusmods.eternalcore.config.RealmEntry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
 * Базова реалізація шляху культивації (Realm).
 * <p>
 * Реалізації мають бути зареєстровані через {@link RealmRegistry} до старту гри.
 * {@link AbstractRealm} описує життєвий цикл шляху: серіалізація виконується через
 * {@link #toNBT()} / {@link #deserialize(CompoundTag)}, а події {@link #onReach(LivingEntity)},
 * {@link #onSet(LivingEntity)} та {@link #onBreakthrough(LivingEntity)} викликаються сервером при
 * переході між світами культивації. Реалізації повинні бути ідемпотентними, оскільки методи можуть
 * викликатися повторно під час синхронізації.
 * </p>
 */
@NoArgsConstructor
public abstract class AbstractRealm implements INBTSerializable<CompoundTag>, IResource {

    /**
     * Ключ серіалізованого ідентифікатора Реалму в NBT.
     */
    public static final String REALM_ID_KEY = "Realm";
    /**
     * Мапа модифікаторів атрибутів, що застосовуються до сутності в цьому Реалмі
     */
    private final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();
    /**
     * Кешовані значення для оптимізації
     */
    private String realmID = null;
    private String realmName = null;
    private final MutableComponent displayName = null;
    private final String translationKey = getNameTranslationKey();

    @Nullable
    private CompoundTag tag = null;

    // ========== CORE IDENTIFICATION METHODS ==========

    /**
     * Створює новий екземпляр {@link AbstractRealm} з {@link CompoundTag} даних.
     * Якщо дані не містять ідентифікатора, повертає null.
     *
     * @param tag NBT дані для створення Реалму
     * @return Новий екземпляр {@link AbstractRealm} або null, якщо ідентифікатор відсутній
     */
    @Nullable
    public static AbstractRealm fromNBT(CompoundTag tag) {
        if (tag.contains(REALM_ID_KEY)) {
            val id = ResourceLocation.tryParse(tag.getString(REALM_ID_KEY));
            val abstractRealm = RealmAPI.getRealmRegistry().get(id);
            if (abstractRealm != null) {
                abstractRealm.deserialize(tag);
            }
            return abstractRealm;
        }
        return null;
    }

    /**
     * Повертає унікальний ідентифікатор ресурсу для цього Реалму.
     * Цей метод повинен бути перевизначений у кожній конкретній реалізації.
     *
     * @return ResourceLocation, що унікально ідентифікує цей Реалм
     */
    public abstract ResourceLocation getResource();

    /**
     * Створює ресурсний ідентифікатор у просторі імен EternalCore.
     *
     * @param name Шляхова частина
     * @return Новий {@link ResourceLocation}
     */
    public final ResourceLocation creteResource(String name){
        return EternalCore.create(name);
    }

    /**
     * Повертає назву Реалму (path частина ResourceLocation).
     * Кешує результат для оптимізації.
     *
     * @return Назва Реалму
     */
    public final String getRealmName() {
        if (realmName == null) {
            realmName = getResource().getPath().intern();
        }
        return realmName;
    }

    /**
     * Повертає повний ідентифікатор Реалму (toString ResourceLocation).
     * Кешує результат для оптимізації.
     *
     * @return Повний ідентифікатор Реалму
     */
    public final String getId() {
        if (realmID == null) {
            realmID = getResource().toString().intern();
        }
        return realmID;
    }

    // ========== CONFIGURATION METHODS ==========

    @Override
    public String getClassName() {
        return "realm";
    }

    /**
     * Повертає конфігурацію за замовчуванням для цього Реалму.
     * Цей метод повинен бути перевизначений у кожній конкретній реалізації.
     *
     * @return Конфігурація Реалму за замовчуванням
     */
    public abstract RealmConfig getDefaultConfig();

    /**
     * Отримує поточну конфігурацію Реалму з системи конфігурацій.
     *
     * @return Активна конфігурація Реалму
     */
    public final RealmEntry getConfig() {
        return ServerConfigs.getRealmConfig(this);
    }

    /**
     * Повертає базове здоров'я для цього Реалму з конфігурації.
     *
     * @return Базове здоров'я
     */
    public final double getBaseHealth() {
        return getConfig().getBaseHealth();
    }

    /**
     * Повертає мінімальний Qi для цього Реалму з конфігурації.
     */
    public final double getMinQi() {
        return getConfig().getMinQi();
    }

    /**
     * Повертає максимальний Qi для цього Реалму з конфігурації.
     */
    public final double getMaxQi() {
        return getConfig().getMaxQi();
    }

    /**
     * Повертає базову атаку для цього Реалму з конфігурації.
     */
    public final double getBaseAttackDamage() {
        return getConfig().getBaseAttackDamage();
    }

    /**
     * Повертає швидкість атаки для цього Реалму з конфігурації.
     */
    public final double getBaseAttackSpeed() {
        return getConfig().getBaseAttackSpeed();
    }

    /**
     * Повертає коефіцієнт віддачі для цього Реалму з конфігурації.
     */
    public final double getKnockbackResistance() {
        return getConfig().getKnockBackResistance();
    }

    /**
     * Повертає висоту стрибка для цього Реалму з конфігурації.
     */
    public final double getJumpHeight() {
        return getConfig().getJumpHeight();
    }

    /**
     * Повертає швидкість пересування для цього Реалму з конфігурації.
     */
    public final double getMovementSpeed() {
        return getConfig().getMovementSpeed();
    }

    /**
     * Повертає швидкість бігу для цього Реалму з конфігурації.
     */
    public final double getSprintSpeed() {
        return getConfig().getSprintSpeed();
    }

    /**
     * Повертає коефіцієнт споживання Qi для цього Реалму з конфігурації.
     */
    public final double getCoefficient() {
        return getConfig().getCoefficient();
    }


    /**
     * Повертає бонус до абсорбції з конфігурації.
     *
     * @return Значення бонусу
     */
    public final double getAbsorptionBonus() {
        return getConfig().getAbsorptionBonus();
    }

    // ========== PROGRESSION METHODS ==========

    /**
     * Перевіряє, чи може сутність літати в цьому Реалмі за конфігом.
     */
    public final boolean canFlyConfig() {
        return getConfig().getCanFly();
    }

    /**
     * Повертає список Реалмів, у які можливий прорив з цього Реалму.
     *
     * @param living Сутність, для якої визначаються можливі прориви
     * @return Список Реалмів для прориву
     */
    public abstract List<AbstractRealm> getNextBreakthroughs(@Nullable LivingEntity living);

    /**
     * Повертає список Реалмів, з яких можливий прорив у цей Реалм.
     *
     * @param living Сутність, для якої визначаються попередні Реалми
     * @return Список попередніх Реалмів
     */
    public abstract List<AbstractRealm> getPreviousBreakthroughs(@Nullable LivingEntity living);

    /**
     * Повертає Реалм, у який відбувається прорив за замовчуванням з цього Реалму.
     *
     * @param living Сутність, для якої визначається стандартний прорив
     * @return Реалм для прориву або null, якщо прорив неможливий
     */
    @Nullable
    public abstract AbstractRealm getDefaultBreakthrough(@Nullable LivingEntity living);

    // ========== WORLD INTERACTION METHODS ==========

    /**
     * Повертає список стадій, доступних у цьому Реалмі.
     *
     * @param living Сутність, для якої визначаються стадії
     * @return Список стадій
     */
    public abstract List<AbstractStage> getRealmStages(@Nullable LivingEntity living);

    /**
     * Повертає вимір та блок для відродження сутності в цьому Реалмі.
     *
     * @param owner Сутність, для якої визначається точка відродження
     * @return Пара (ключ виміру, стан блоку)
     */
    public Pair<ResourceKey<Level>, BlockState> getRespawnDimension(@Nullable LivingEntity owner) {
        return Pair.of(Level.OVERWORLD, Blocks.AIR.defaultBlockState());
    }

    /**
     * Визначає, чи є сутність пасивно дружньою до володаря цього Реалму.
     *
     * @param entity Сутність для перевірки
     * @return true, якщо сутність дружня, false - в іншому випадку
     */
    public abstract boolean passivelyFriendlyWith(@Nullable LivingEntity entity);

    // ========== LIFECYCLE METHODS ==========

    /**
     * Визначає, чи може сутність в цьому Реалмі літати.
     *
     * @param living Сутність для перевірки
     * @return true, якщо політ дозволено, false - в іншому випадку
     */
    public boolean canFly(@Nullable LivingEntity living) {
        return getConfig().getCanFly();
    }

    /**
     * Викликається, коли сутність встановлює цей Реалм як активний.
     *
     * @param living Сутність, яка встановлює Реалм
     */
    public void onSet(@Nullable LivingEntity living) {
        // Базова реалізація - можна перевизначити
    }

    /**
     * Повертає додаткову інформацію для відображення гравцю.
     *
     * @param living Сутність (може бути {@code null})
     * @return Список компонентів опису
     */
    public List<MutableComponent> getUniqueInfo(@Nullable LivingEntity living) {
        return List.of();
    }

    /**
     * Викликається, коли сутність досягає цього Реалму.
     *
     * @param living Сутність, яка досягає Реалму
     */
    public void onReach(@Nullable LivingEntity living) {
        // Базова реалізація - можна перевизначити
    }

    /**
     * Викликається, коли сутність починає відстежувати цей Реалм.
     *
     * @param living Сутність, яка відстежує Реалм
     */
    public void onTrack(@Nullable LivingEntity living) {
        // Базова реалізація - можна перевизначити
    }

    /**
     * Викликається, коли сутність здійснює прорив у цей Реалм.
     *
     * @param living Сутність, яка здійснює прорив
     */
    public void onBreakthrough(@Nullable LivingEntity living) {
        // Базова реалізація - можна перевизначити
    }

    // ========== ATTRIBUTE MANAGEMENT ==========

    /**
     * Викликається кожен тік для сутності, що має цей Реалм.
     *
     * @param living Сутність, що має цей Реалм
     */
    public void onTick(@Nullable LivingEntity living) {
        // Базова реалізація - можна перевизначити
    }

    /**
     * Додає модифікатор атрибуту до цього Реалму.
     *
     * @param holder           Тримач атрибуту
     * @param resourceLocation Ідентифікатор модифікатора
     * @param amount           Значення модифікатора
     * @param operation        Операція модифікатора
     * @return Цей Реалм для ланцюжкового виклику
     */
    public final AbstractRealm addAttributeModifier(Holder<Attribute> holder, ResourceLocation resourceLocation,
                                                    double amount, AttributeModifier.Operation operation) {
        this.attributeModifiers.put(holder, new AttributeTemplate(resourceLocation, amount, operation));
        return this;
    }

    /**
     * Створює модифікатори атрибутів для цього Реалму.
     *
     * @param level    Рівень Реалму
     * @param consumer Споживач для обробки модифікаторів
     */
    public final void createModifiers(int level, BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
        this.attributeModifiers.forEach((holder, template) ->
                consumer.accept(holder, template.create(level)));
    }

    /**
     * Видаляє модифікатори атрибутів цього Реалму з сутності.
     *
     * @param entity Сутність, з якої видаляються модифікатори
     */
    public final void removeAttributeModifiers(LivingEntity entity) {
        AttributeMap attributeMap = entity.getAttributes();
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(entry.getValue().id());
            }
        }
    }

    /**
     * Додає модифікатори атрибутів цього Реалму до сутності.
     *
     * @param entity Сутність, до якої додаються модифікатори
     * @param level  Рівень Реалму
     */
    public final void addAttributeModifiers(LivingEntity entity, int level) {
        AttributeMap attributeMap = entity.getAttributes();
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
            if (attributeInstance != null) {
                attributeInstance.removeModifier(entry.getValue().id());
                attributeInstance.addPermanentModifier(entry.getValue().create(level));
            }
        }
    }

    /**
     * Серіалізує Реалм у NBT, включно з ідентифікатором.
     *
     * @return Тег із даними Реалму
     */
    @Override
    public CompoundTag toNBT() {
        CompoundTag compoundTag = new CompoundTag();
        serialize(compoundTag);
        return compoundTag;
    }

    /**
     * Записує внутрішні дані Реалму у вказаний тег.
     *
     * @param tag Тег для запису
     * @return Той самий тег для ланцюжкових викликів
     */
    @Override
    public CompoundTag serialize(CompoundTag tag) {
        if (this.tag != null) tag.put("tag", this.tag.copy());
        ResourceLocation resource = this.getResource();
        if (resource != null) {
            tag.putString(REALM_ID_KEY, resource.toString());
        }
        return tag;
    }

    /**
     * Відновлює стан Реалму з NBT.
     *
     * @param tag Тег з даними
     */
    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
    }

    // ========== UTILITY METHODS ==========

    /**
     * Перевіряє рівність Реалмів за їх ідентифікаторами.
     *
     * @param obj Об'єкт для порівняння
     * @return true, якщо об'єкти рівні, false - в іншому випадку
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        AbstractRealm other = (AbstractRealm) obj;
        ResourceLocation thisId = this.getResource();
        ResourceLocation otherId = other.getResource();
        return thisId != null && thisId.equals(otherId);
    }

    /**
     * Повертає хеш-код для цього Реалму.
     *
     * @return Хеш-код
     */
    @Override
    public int hashCode() {
        ResourceLocation resource = getResource();
        return resource != null ? resource.hashCode() : 0;
    }

    /**
     * Повертає існуючий або створює новий тег додаткових даних.
     *
     * @return Тег користувацьких даних
     */
    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.tag = new CompoundTag();
        }
        return this.tag;
    }

    /**
     * Повертає рядкове представлення цього Реалму.
     *
     * @return Рядкове представлення
     */
    @Override
    public String toString() {
        return String.format("%s{id='%s'}", this.getClass().getSimpleName(), getId());
    }

    /**
     * Перевіряє належність Реалму до тегу.
     *
     * @param tag Тег для перевірки
     * @return {@code true}, якщо Реалм присутній у вказаному тегу
     */
    public boolean is(TagKey<AbstractRealm> tag) {
        return RealmRegistry.getRegistrySupplier(this).is(tag);
    }
}