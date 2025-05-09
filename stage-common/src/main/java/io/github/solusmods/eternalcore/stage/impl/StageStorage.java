package io.github.solusmods.eternalcore.stage.impl;

import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.stage.EternalCoreStage;
import io.github.solusmods.eternalcore.stage.api.*;
import io.github.solusmods.eternalcore.stage.api.entity.EntityEvents;
import io.github.solusmods.eternalcore.stage.impl.network.InternalStorageActions;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageEvents;
import io.github.solusmods.eternalcore.storage.api.StorageHolder;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Система зберігання та управління стадіями гравців.
 * <p>
 * StageStorage відповідає за зберігання поточної стадії гравця та всіх досягнутих стадій.
 * Реалізує інтерфейси {@link Stages} та {@link IReachedStages} для забезпечення функціональності
 * роботи зі стадіями.
 * </p>
 * <p>
 * Клас підтримує серіалізацію/десеріалізацію даних через NBT, а також подієву систему
 * для повідомлення про зміни в стадіях.
 * </p>
 */
public class StageStorage extends Storage implements Stages, IReachedStages {
    /** Ключ для зберігання поточної стадії у NBT */
    private static final String STAGE_KEY = "stage_key";

    /** Ключ для зберігання колекції досягнутих стадій у NBT */
    private static final String REACHED_STAGES_KEY = "reached_stages_key";

    /** Ідентифікатор сховища стадій */
    public static final ResourceLocation ID = EternalCoreStage.create("stage_storage");

    /** Ключ сховища для доступу до даних StageStorage */
    @Getter
    private static StorageKey<StageStorage> key = null;

    /** Поточна активна стадія гравця */
    private StageInstance stageInstance = null;

    /** Колекція всіх досягнутих гравцем стадій */
    private final Collection<StageInstance> reachedStages = new ArrayList<>();

    /**
     * Створює нове сховище стадій для вказаного власника.
     *
     * @param holder Власник сховища (гравець)
     */
    protected StageStorage(StorageHolder holder) {
        super(holder);
    }

    /**
     * Ініціалізує систему сховища стадій.
     * <p>
     * Реєструє сховище в системі, налаштовує обробники подій для:
     * <ul>
     *   <li>Тікових оновлень стадій</li>
     *   <li>Оновлення ефектів стадій</li>
     * </ul>
     * </p>
     */
    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        StageStorage.class, Entity.class::isInstance,
                        StageStorage::new));
        EntityEvents.LIVING_POST_TICK.register(entity -> {
            Level level = entity.level();
            if (level.isClientSide) return;
            if (!(entity instanceof Player)) return;
            IReachedStages reachedStages = StageAPI.getReachedStagesFrom(entity);
            if (reachedStages.getReachedStages().isEmpty()) return;
            reachedStages.getReachedStages().forEach(stageInstance -> stageInstance.onTick(entity));
        });
        StageEvents.STAGE_POST_TICK.register((instance, owner) -> {
            if (instance.getEffect(owner).isEmpty()) return;
            MobEffectInstance effectInstance = instance.getEffect(owner).get();
            if (!owner.hasEffect(effectInstance.getEffect()))
                owner.addEffect(effectInstance);
        });
    }

    /**
     * {@inheritDoc}
     *
     * @return Колекція всіх досягнутих стадій
     */
    @Override
    public Collection<StageInstance> getReachedStages() {
        return reachedStages;
    }

    /**
     * {@inheritDoc}
     *
     * Додає нову досягнуту стадію та викликає відповідні події.
     *
     * @param stage Стадія для додавання
     * @param breakthrough Чи є ця стадія проривом
     * @param notifyPlayer Чи потрібно сповіщати гравця
     * @param component Компонент повідомлення для гравця (може бути null)
     * @return true, якщо стадія була успішно додана, false - якщо була скасована подією
     */
    @Override
    public boolean addStage(StageInstance stage, boolean breakthrough, boolean notifyPlayer, @Nullable MutableComponent component) {
        Changeable<MutableComponent> stageMessage = Changeable.of(component);
        Changeable<Boolean> notify = Changeable.of(notifyPlayer);
        EventResult result = StageEvents.REACH_STAGE.invoker().reach(stage, getOwner(), breakthrough, notify, stageMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();
        if (stageMessage.isPresent()) getOwner().sendSystemMessage(stageMessage.get());
        stage.markDirty();
        stage.onReach(owner);
        reachedStages.add(stage);
        markDirty();
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return Optional, що містить поточну активну стадію, або порожній Optional, якщо стадія не встановлена
     */
    @Override
    public Optional<StageInstance> getStage() {
        return Optional.ofNullable(stageInstance);
    }

    /**
     * {@inheritDoc}
     *
     * Встановлює нову активну стадію та викликає відповідні події.
     *
     * @param stage Нова стадія для встановлення
     * @param advancement Чи є це просуванням вперед
     * @param notify Чи потрібно сповіщати гравця
     * @param component Компонент повідомлення для гравця (може бути null)
     * @return true, якщо стадія була успішно встановлена, false - якщо була скасована подією
     */
    @Override
    public boolean setStage(StageInstance stage, boolean advancement, boolean notify, @Nullable MutableComponent component) {
        StageInstance instance = this.stageInstance;
        Changeable<MutableComponent> realmMessage = Changeable.of(component);
        Changeable<Boolean> notifyPlayer = Changeable.of(notify);
        EventResult result = StageEvents.SET_STAGE.invoker().set(instance, getOwner(), stage, advancement, notifyPlayer, realmMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();

        if (realmMessage.isPresent()) getOwner().sendSystemMessage(realmMessage.get());
        stage.markDirty();
        stage.onSet(owner);
        this.stageInstance = stage;
        markDirty();
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Зберігає дані стадій у NBT.
     *
     * @param data NBT тег для зберігання даних
     */
    @Override
    public void save(CompoundTag data) {
        if (stageInstance != null)
            data.put(STAGE_KEY, this.stageInstance.toNBT());
        CompoundTag reachedStagesTag = new CompoundTag();
        reachedStages.forEach(instance -> reachedStagesTag.put(instance.getStageId().toString(), instance.toNBT()));
        data.put(REACHED_STAGES_KEY, reachedStagesTag);
    }

    /**
     * {@inheritDoc}
     *
     * Завантажує дані стадій з NBT.
     *
     * @param data NBT тег для завантаження даних
     */
    @Override
    public void load(CompoundTag data) {
        if (data.contains(STAGE_KEY, 10)) {
            stageInstance = StageInstance.fromNBT(data.getCompound(STAGE_KEY));
        }
        loadCollections(data);
    }

    /**
     * Завантажує колекції стадій з NBT даних.
     *
     * @param data NBT тег, що містить дані колекцій
     */
    private void loadCollections(CompoundTag data) {
        reachedStages.clear();
        loadInstanceCollection(data, REACHED_STAGES_KEY, reachedStages, StageInstance::fromNBT);
    }

    /**
     * Отримує власника сховища стадій.
     *
     * @return Живу сутність, що володіє цим сховищем стадій
     */
    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }


    public void sync(){
        CompoundTag data = new CompoundTag();
        saveOutdated(data);
        InternalStorageActions.sendSyncStoragePayload(data);
    }
}