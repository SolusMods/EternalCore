package io.github.solusmods.eternalcore.api.stage;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.ServerConfigs;
import io.github.solusmods.eternalcore.api.data.IResource;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
import io.github.solusmods.eternalcore.api.registry.SpiritualRootRegistry;
import io.github.solusmods.eternalcore.api.registry.StageRegistry;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.storage.INBTSerializable;
import io.github.solusmods.eternalcore.config.StageConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Базовий опис стадії культивації.
 * <p>
 * Кожна реалізація повинна бути зареєстрована через {@link StageRegistry} до початку гри.
 * Життєвий цикл стадії включає серіалізацію через {@link #toNBT()} / {@link #deserialize(CompoundTag)}
 * та виклики подій {@link #onReach(LivingEntity)}, {@link #onSet(LivingEntity)}, {@link #onBreakthrough(LivingEntity)}
 * під час просування гравця. Методи життєвого циклу викликаються лише на сервері, але реалізації мають
 * бути ідемпотентними для уникнення дублювання ефектів під час повторної синхронізації.
 * </p>
 */
@Getter
@NoArgsConstructor
public abstract class AbstractStage implements IResource, INBTSerializable<CompoundTag> {

    private String stageID = null;
    private String stageName = null;
    @Nullable
    private CompoundTag tag = null;

    /**
     * Повертає ресурсний ідентифікатор стадії.
     *
     * @return {@link ResourceLocation} стадії, використовується при реєстрації
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
     * Повертає конфігурацію за замовчуванням, яка буде застосована під час генерації server config.
     *
     * @return Конфігурація стадії
     */
    public abstract StageConfig getDefaultConfig();

    public final double getMinBaseQi() {
        return ServerConfigs.getStageConfig(this).getMinQi();
    }

    public final double getMaxBaseQi() {
        return ServerConfigs.getStageConfig(this).getMaxQi();
    }

    /**
     * Повертає додаткову інформацію, яка відображається гравцю при перегляді стадії.
     *
     * @param living Сутність, для якої генерується опис (може бути {@code null})
     * @return Список компонентів, що описують особливості стадії
     */
    public List<MutableComponent> getUniqueInfo(@Nullable LivingEntity living) {
        return List.of();
    }
    /**
     * Відновлює стадію з NBT-представлення.
     *
     * @param tag Тег з серіалізованою стадією
     * @return Екземпляр стадії або {@code null}, якщо ідентифікатор не зареєстровано
     */
    @Nullable
    public static AbstractStage fromNBT(CompoundTag tag) {
        if (tag.contains("Id")) {
            val id = ResourceLocation.tryParse(tag.getString("Id"));
            val abstractStage = StageAPI.getStageRegistry().get(id);
            if (abstractStage != null) {
                abstractStage.deserialize(tag);
            }
            return abstractStage;
        }
        return null;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", this.getResource().toString());
        serialize(tag);
        return tag;
    }

    /**
     * Серіалізує додаткові дані стадії у вказаний тег.
     *
     * @param tag Тег для запису
     * @return Той самий тег для ланцюжка викликів
     */
    @Override
    public CompoundTag serialize(CompoundTag tag) {
        if (this.tag != null) tag.put("tag", this.tag.copy());
        return tag;
    }

    /**
     * Повертає існуючий або створює новий тег користувацьких даних.
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
     * Відновлює додаткові дані стадії з тега.
     *
     * @param tag Джерело даних
     */
    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains("tag", 10)) this.tag = tag.getCompound("tag");
        // No additional data to deserialize in the base class
    }

    /**
     * Повертає назву Етапу (path частина ResourceLocation).
     * Кешує результат для оптимізації.
     *
     * @return Назва Етапу
     */
    public final String getStageName() {
        if (stageName == null) {
            stageName = getResource().getPath().intern();
        }
        return stageName;
    }

    /**
     * Повертає повний ідентифікатор Етапу (toString ResourceLocation).
     * Кешує результат для оптимізації.
     *
     * @return Повний ідентифікатор Етапу
     */
    public final String getId() {
        if (stageID == null) {
            stageID = getResource().toString().intern();
        }
        return stageID;
    }

    /**
     * Повертає потенційні наступні стадії.
     *
     * @param living Контекст сутності (може бути {@code null})
     * @return Список потенційних проривів
     */
    public abstract List<AbstractStage> getNextBreakthroughs(@Nullable LivingEntity living);

    /**
     * Повертає список попередніх стадій, з яких можна перейти на поточну.
     *
     * @param living Контекст сутності (може бути {@code null})
     * @return Список попередніх проривів
     */
    public abstract List<AbstractStage> getPreviousBreakthroughs(@Nullable LivingEntity living);

    /**
     * Визначає рекомендований наступний прорив для сутності.
     *
     * @param living Контекст сутності (може бути {@code null})
     * @return Стадія-прорив або {@code null}, якщо рекомендація відсутня
     */
    @Nullable
    public abstract AbstractStage getDefaultBreakthrough(@Nullable LivingEntity living);

    @Override
    public String getClassName() {
        return "stage";
    }

    /**
     * Викликається, коли стадію встановлено як активну.
     *
     * @param living Сутність-власник (може бути {@code null})
     */
    public void onSet(@Nullable LivingEntity living) {
    }

    /**
     * Викликається під час досягнення стадії вперше.
     *
     * @param living Сутність-власник (може бути {@code null})
     */
    public void onReach(@Nullable LivingEntity living) {
    }

    /**
     * Викликається, коли стадію починають відслідковувати (наприклад, для прогресу).
     *
     * @param living Сутність-власник (може бути {@code null})
     */
    public void onTrack(@Nullable LivingEntity living) {
    }

    /**
     * Викликається на кожному тіку, коли стадія активна.
     *
     * @param living Сутність-власник (може бути {@code null})
     */
    public void onTick(@Nullable LivingEntity living) {
    }

    /**
     * Викликається після успішного прориву на наступну стадію.
     *
     * @param living Сутність-власник (може бути {@code null})
     */
    public void onBreakthrough(@Nullable LivingEntity living) {
    }

    /**
     * Повертає ефект, який застосовується під час активної стадії.
     *
     * @return Обгортка {@link Changeable} з ефектом або порожній об'єкт
     */
    public Changeable<MobEffectInstance> getEffect() {
        return Changeable.of(null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        AbstractStage other = (AbstractStage) obj;
        ResourceLocation thisId = this.getResource();
        ResourceLocation otherId = other.getResource();
        return thisId != null && thisId.equals(otherId);
    }

    /**
     * Повертає хеш-код для цього Етапу.
     *
     * @return Хеш-код
     */
    @Override
    public int hashCode() {
        ResourceLocation resource = getResource();
        return resource != null ? resource.hashCode() : 0;
    }

    /**
     * Повертає рядкове представлення цього Етапу.
     *
     * @return Рядкове представлення
     */
    @Override
    public String toString() {
        return String.format("%s{id='%s'}", this.getClass().getSimpleName(), getId());
    }


    /**
     * Перевіряє, чи належить стадія до певного тегу.
     *
     * @param tag Тег стадій
     * @return {@code true}, якщо стадія входить до тегу
     */
    public boolean is(TagKey<AbstractStage> tag) {
        return StageRegistry.getRegistrySupplier(this).is(tag);
    }
}
