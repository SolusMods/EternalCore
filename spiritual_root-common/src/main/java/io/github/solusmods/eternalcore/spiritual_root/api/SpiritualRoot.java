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
 * Духовний Корінь визначає природні здібності та схильності сутності до певного типу
 * елементальної енергії чи шляху культивації. Кожен Духовний Корінь має унікальні
 * характеристики та впливає на розвиток сутності.
 * </p>
 * <p>
 * Клас містить методи для управління атрибутами, рівнем майстерності,
 * досвідом та взаємодією з елементами.
 * </p>
 */
@Getter
@AllArgsConstructor
public abstract class SpiritualRoot {
    /** Тип Духовного Кореня, визначає його елементальну приналежність */
    public final RootType type;

    /** Мапа модифікаторів атрибутів, що застосовуються до сутності з цим Духовним Коренем */
    protected final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();

    /**
     * Створює новий екземпляр Духовного Кореня з базовими налаштуваннями.
     * <p>
     * Цей метод використовується для створення екземпляра Духовного Кореня з його основними
     * характеристиками для призначення сутності.
     * </p>
     *
     * @return Новий екземпляр Духовного Кореня
     */
    public SpiritualRootInstance createDefaultInstance() {
        return new SpiritualRootInstance(this);
    }

    /**
     * Отримує ідентифікатор цього Духовного Кореня з реєстру.
     *
     * @return Ідентифікатор Духовного Кореня або null, якщо корінь не зареєстровано
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return SpiritualRootAPI.getSpiritualRootRegistry().getId(this);
    }

    /**
     * Отримує локалізовану назву цього Духовного Кореня.
     *
     * @return Компонент тексту з назвою Духовного Кореня або null, якщо корінь не зареєстровано
     */
    @Nullable
    public MutableComponent getName() {
        ResourceLocation id = this.getRegistryName();
        return id == null ? null : Component.translatable(String.format("%s.spiritual_root.%s", id.getNamespace(), id.getPath().replace('/', '.')));
    }

    /**
     * Повертає максимальний рівень майстерності для цього Духовного Кореня.
     * <p>
     * За замовчуванням, максимальний рівень встановлено як X.
     * </p>
     *
     * @return Максимальний рівень майстерності
     */
    public RootLevels getMaxLevel() {
        return RootLevels.X;
    }

    /**
     * Перевіряє, чи досягла сутність повного майстерності з цим Духовним Коренем.
     * <p>
     * Сутність вважається майстром, якщо її поточний досвід перевищує або дорівнює
     * необхідному досвіду для наступного рівня.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param entity Сутність для перевірки
     * @return true, якщо сутність досягла майстерності, false - в іншому випадку
     */
    public boolean isMastered(SpiritualRootInstance instance, LivingEntity entity) {
        return instance.getExperience() >= instance.getNextLevel().getExperience();
    }

    /**
     * Додає досвід до екземпляра Духовного Кореня.
     * <p>
     * Якщо після додавання досвіду сутність досягає майстерності, викликається метод
     * {@link #onMastered(SpiritualRootInstance, LivingEntity)}.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param living Сутність, якій додається досвід
     * @param exp Кількість досвіду для додавання
     */
    public void addExperience(SpiritualRootInstance instance, LivingEntity living, float exp) {
        if (!isMastered(instance, living)) {
            instance.setExperience(instance.getExperience() + exp);
            if (isMastered(instance, living)) {
                onMastered(instance, living);
            }
        }
    }

    /**
     * Додає модифікатор атрибуту до цього Духовного Кореня.
     * <p>
     * Модифікатори застосовуються до сутності, коли Духовний Корінь встановлюється як активний.
     * </p>
     *
     * @param holder Тримач атрибуту
     * @param resourceLocation Ідентифікатор модифікатора
     * @param amount Значення модифікатора
     * @param operation Операція модифікатора
     */
    public void addAttributeModifier(Holder<Attribute> holder, ResourceLocation resourceLocation, double amount, AttributeModifier.Operation operation) {
        this.attributeModifiers.put(holder, new AttributeTemplate(resourceLocation, amount, operation));
    }

    /**
     * Застосовує модифікатори атрибутів цього Духовного Кореня до сутності.
     * <p>
     * Викликається під час встановлення Духовного Кореня як активного.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param entity Сутність, до якої застосовуються модифікатори
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
     * Видаляє модифікатори атрибутів цього Духовного Кореня від сутності.
     * <p>
     * Викликається при зміні активного Духовного Кореня.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param entity Сутність, від якої видаляються модифікатори
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

    /**
     * Збільшує силу Духовного Кореня на вказану величину.
     * <p>
     * Сила Духовного Кореня обмежена максимальним значенням 1.0.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param living Сутність, чия сила збільшується
     * @param amount Величина збільшення
     */
    public void increaseStrength(SpiritualRootInstance instance, LivingEntity living, float amount) {
        instance.setStrength(Math.min(1.0f, instance.getStrength() + amount));
    }

    /**
     * Повертає розвинутий Духовний Корінь, у який може просунутися сутність з поточним Коренем.
     * <p>
     * Цей метод визначає шлях прогресії для сутності.
     * </p>
     *
     * @param instance Екземпляр поточного Духовного Кореня
     * @param living Сутність, для якої визначається шлях просування
     * @return Розвинутий Духовний Корінь або null, якщо просування неможливе
     * @see SpiritualRootInstance#getAdvanced(LivingEntity)
     */
    @Nullable
    public abstract SpiritualRoot getAdvanced(SpiritualRootInstance instance, LivingEntity living);

    /**
     * Повертає елемент, пов'язаний з цим Духовним Коренем.
     * <p>
     * Елемент визначає тип енергії, з якою сутність має найбільшу спорідненість.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param entity Сутність, для якої визначається елемент
     * @return Елемент або null, якщо елемент не визначено
     */
    public abstract @Nullable Element getElement(SpiritualRootInstance instance, LivingEntity entity);

    /**
     * Викликається, коли сутність досягає повного майстерності з цим Духовним Коренем.
     * <p>
     * За замовчуванням, викликає подію майстерності для обробки іншими компонентами системи.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param living Сутність, яка досягла майстерності
     */
    public void onMastered(SpiritualRootInstance instance, LivingEntity living) {
        Changeable<Boolean> notify = Changeable.of(false);
        SpiritualRootEvents.MASTERING.invoker().mastering(instance, living, false, notify, null);
    }

    /**
     * Викликається, коли сутність отримує цей Духовний Корінь.
     * <p>
     * Перевизначте цей метод для додавання власної логіки при отриманні Духовного Кореня.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param living Сутність, яка отримує Духовний Корінь
     */
    public void onAdd(SpiritualRootInstance instance, LivingEntity living) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Викликається, коли сутність просувається до наступного рівня цього Духовного Кореня.
     * <p>
     * Перевизначте цей метод для додавання власної логіки при просуванні.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param living Сутність, яка просувається
     */
    public void onAdvance(SpiritualRootInstance instance, LivingEntity living) {
        // Перевизначте цей метод для додавання власної логіки
    }

    /**
     * Повертає протилежний Духовний Корінь для поточного.
     * <p>
     * Протилежні Духовні Корені мають конфліктуючі елементальні властивості.
     * За замовчуванням повертає null.
     * </p>
     *
     * @param instance Екземпляр Духовного Кореня
     * @param entity Сутність, для якої визначається протилежний Корінь
     * @return Протилежний Духовний Корінь або null
     */
    public @Nullable SpiritualRoot getOpposite(SpiritualRootInstance instance, LivingEntity entity){return null;}

    /**
     * Запис, що представляє шаблон модифікатора атрибутів.
     * <p>
     * Використовується для створення модифікаторів атрибутів при застосуванні
     * Духовного Кореня до сутності.
     * </p>
     */
    public record AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        /**
         * Створює новий шаблон модифікатора атрибутів.
         *
         * @param id Ідентифікатор модифікатора
         * @param amount Значення модифікатора
         * @param operation Операція модифікатора
         */
        public AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
            this.id = id;
            this.amount = amount;
            this.operation = operation;
        }

        /**
         * Створює новий модифікатор атрибутів на основі цього шаблону.
         *
         * @return Новий модифікатор атрибутів
         */
        public AttributeModifier create() {
            return new AttributeModifier(this.id, this.amount, this.operation);
        }

        /**
         * Отримує ідентифікатор модифікатора.
         *
         * @return Ідентифікатор модифікатора
         */
        public ResourceLocation id() {
            return this.id;
        }

        /**
         * Отримує значення модифікатора.
         *
         * @return Значення модифікатора
         */
        public double amount() {
            return this.amount;
        }

        /**
         * Отримує операцію модифікатора.
         *
         * @return Операція модифікатора
         */
        public AttributeModifier.Operation operation() {
            return this.operation;
        }
    }
}