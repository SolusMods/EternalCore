package io.github.solusmods.eternalcore.api.spiritual_root;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.ServerConfigs;
import io.github.solusmods.eternalcore.api.data.IResource;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
import io.github.solusmods.eternalcore.api.qi_energy.ElementType;
import io.github.solusmods.eternalcore.api.realm.AttributeTemplate;
import io.github.solusmods.eternalcore.api.registry.ElementTypeRegistry;
import io.github.solusmods.eternalcore.api.registry.SpiritualRootRegistry;
import io.github.solusmods.eternalcore.api.storage.INBTSerializable;
import io.github.solusmods.eternalcore.config.SpiritualRootConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public abstract class AbstractSpiritualRoot implements INBTSerializable<CompoundTag>, IResource {

    private final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();
    /**
     * Кешовані значення для оптимізації
     */
    private String rootID = null;
    private String rootName = null;
    private final MutableComponent displayName = null;
    private final String translationKey = null;
    /**
     * Поточний досвід кореня
     */
    private float experience = 0.0f;
    /**
     * Поточна сила кореня (чистота)
     */
    private float strength = 0.0f;
    @Getter
    @Setter
    private boolean dominant = false;
    /**
     * Рівень кореня
     * Використовується для визначення поточного рівня розвитку кореня
     */
    @Getter
    @Setter
    private int level = 0;
    /**
     * NBT дані для зберігання додаткової інформації про корінь
     */
    @Nullable
    private CompoundTag tag = null;

    /**
     * Створює новий екземпляр AbstractSpiritualRoot з NBT даних.
     * Якщо дані не містять ідентифікатора, повертає null.
     *
     * @param tag NBT дані для створення кореня
     * @return Новий екземпляр AbstractSpiritualRoot або null, якщо ідентифікатор відсутній
     */
    @Nullable
    public static AbstractSpiritualRoot fromNBT(CompoundTag tag) {
        val id = ResourceLocation.parse(tag.getString("Resource"));
        val abstractSpiritualRoot = SpiritualRootRegistry.getSpiritualRootRegistry().get(id);
        if (abstractSpiritualRoot != null) {
            abstractSpiritualRoot.deserialize(tag);
        }
        return abstractSpiritualRoot;
    }

    /**
     * Максимальний рівень
     */
    public int getMaxLevel() {
        return ServerConfigs.getSpiritualRootConfig(this).getMaxLevel();
    }

    public double getExperiencePerLevel() {
        return ServerConfigs.getSpiritualRootConfig(this).getExperiencePerLevel();
    }

    /**
     * Повертає унікальний ідентифікатор ресурсу для цього Духовного кореня.
     * Цей метод повинен бути перевизначений у кожній конкретній реалізації.
     *
     * @return ResourceLocation, що унікально ідентифікує цей Реалм
     */
    public abstract ResourceLocation getResource();

    public final ResourceLocation creteResource(String name){
        return EternalCore.create(name);
    }

    /**
     * Повертає конфігурацію Духовного кореня.
     * Цей метод повинен бути перевизначений у кожній конкретній реалізації.
     *
     * @return SpiritualRootConfig, що містить налаштування Духовного кореня
     */
    public abstract SpiritualRootConfig getDefaultConfig();

    @Override
    public String getClassName() {
        return "spiritual_root";
    }

    /**
     * Повертає назву Духовного кореня (path частина ResourceLocation).
     * Кешує результат для оптимізації.
     *
     * @return Назва Духовного кореня
     */
    public final String getRootName() {
        if (rootName == null) {
            rootName = getResource().getPath().intern();
        }
        return rootName;
    }

    /**
     * Повертає повний ідентифікатор Духовного кореня (toString ResourceLocation).
     * Кешує результат для оптимізації.
     *
     * @return Повний ідентифікатор Духовного кореня
     */
    public final String getId() {
        if (rootID == null) {
            rootID = getResource().toString().intern();
        }
        return rootID;
    }

    /**
     * Додавання модифікатора атрибуту
     */
    public void addAttributeModifier(Holder<Attribute> holder, ResourceLocation resourceLocation,
                                     double amount, AttributeModifier.Operation operation) {
        this.attributeModifiers.put(holder, new AttributeTemplate(resourceLocation, amount, operation));
    }

    /**
     * Застосування модифікаторів
     */
    public void addAttributeModifiers(LivingEntity entity) {
        if (this.attributeModifiers.isEmpty()) return;

        AttributeMap attributeMap = entity.getAttributes();
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());

            if (attributeInstance == null) continue;
            attributeInstance.removeModifier(entry.getValue().id());
            attributeInstance.addPermanentModifier(entry.getValue().create(1));
        }
    }

    /**
     * Видалення модифікаторів
     */
    public void removeAttributeModifiers(LivingEntity entity) {
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
     * Збільшення сили
     */
    public void increaseStrength(@Nullable LivingEntity living, float amount) {
        this.strength = Math.min(1.0f, this.strength + amount);
    }

    /**
     * Перевірка на просування
     */
    public boolean canAdvance(@Nullable LivingEntity entity) {
        return false;
    }

    /**
     * Отримання Qi енергії
     */
    public abstract @Nullable ElementType getElementType(@Nullable LivingEntity entity);

    /**
     * Еволюція 1-го ступеня
     */
    @Nullable
    public abstract AbstractSpiritualRoot getFirstDegree(@Nullable LivingEntity living);

    /**
     * Еволюція 2-го ступеня
     */
    @Nullable
    public AbstractSpiritualRoot getSecondDegree(@Nullable LivingEntity living) {
        return null;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Resource", this.getResource().toString());
        serialize(tag);
        return tag;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        if (this.tag != null) tag.put("tag", this.tag.copy());
        tag.putBoolean("Dominant", this.dominant);
        tag.putFloat("Strength", this.strength);
        tag.putFloat("Experience", this.experience);
        tag.putInt("Level", this.level);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
        dominant = tag.getBoolean("Dominant");
        strength = tag.getFloat("Strength");
        experience = tag.getFloat("Experience");
        level = tag.getInt("Level");
    }

    /**
     * Отримання протилежного кореня
     */
    @Nullable
    public AbstractSpiritualRoot getOpposite(@Nullable LivingEntity entity) {
        return null;
    }

    /**
     * Попередній ступінь
     */
    @Nullable
    public abstract AbstractSpiritualRoot getPreviousDegree(@Nullable LivingEntity living);

    /**
     * Викликається при додаванні кореня
     */
    public void onAdd(@Nullable LivingEntity living) {
    }

    /**
     * Викликається при просуванні
     */
    public void onAdvance(@Nullable LivingEntity living) {
        SpiritualRootEvents.ADVANCE.invoker().advance(this, living, false, Changeable.of(false), null);
    }

    /**
     * Викликається при отриманні досвіду
     */
    public void onAddExperience(@Nullable LivingEntity entity) {
        this.experience += (float) getExperiencePerLevel();
        SpiritualRootEvents.EXPERIENCE_GAIN.invoker().gainExperience(this, entity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        AbstractSpiritualRoot other = (AbstractSpiritualRoot) obj;
        ResourceLocation thisId = this.getResource();
        ResourceLocation otherId = other.getResource();
        return thisId != null && thisId.equals(otherId);
    }

    /**
     * Повертає хеш-код для цього Духовного кореня.
     *
     * @return Хеш-код
     */
    @Override
    public int hashCode() {
        ResourceLocation resource = getResource();
        return resource != null ? resource.hashCode() : 0;
    }

    /**
     * Повертає рядкове представлення цього Духовного кореня.
     *
     * @return Рядкове представлення
     */
    @Override
    public String toString() {
        return String.format("%s{id='%s'}", this.getClass().getSimpleName(), getId());
    }

    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.tag = new CompoundTag();
        }
        return this.tag;
    }


    /**
     * Перевіряє, чи належить цей Духовний корінь до вказаного тегу.
     *
     * @param tag Тег для перевірки
     * @return true, якщо корінь належить до тегу, інакше false
     */
    public boolean is(TagKey<AbstractSpiritualRoot> tag) {
        return SpiritualRootRegistry.getRegistrySupplier(this).is(tag);
    }
}
