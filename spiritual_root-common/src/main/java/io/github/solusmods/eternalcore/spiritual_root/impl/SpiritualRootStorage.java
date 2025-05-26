package io.github.solusmods.eternalcore.spiritual_root.impl;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.spiritual_root.EternalCoreSpiritualRoot;
import io.github.solusmods.eternalcore.spiritual_root.api.*;
import io.github.solusmods.eternalcore.spiritual_root.impl.network.InternalSpiritualRootPacketActions;
import io.github.solusmods.eternalcore.storage.EternalCoreStorage;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageEvents;
import io.github.solusmods.eternalcore.storage.api.StorageHolder;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Клас для зберігання та управління духовними коренями сутності.
 * Реалізує інтерфейс {@link SpiritualRoots} та розширює {@link Storage}.
 */
public class SpiritualRootStorage extends Storage implements SpiritualRoots {
    private static final String SPIRITUAL_ROOTS_KEY = "spiritual_roots_key";
    public static final ResourceLocation ID = EternalCoreSpiritualRoot.create("spiritual_root_storage");
    @Getter
    private static StorageKey<SpiritualRootStorage> key = null;
    private final Map<ResourceLocation, SpiritualRootInstance> spiritualRoots = new HashMap<>();

    private boolean hasRemovedRoots = false;

    protected SpiritualRootStorage(StorageHolder holder) {
        super(holder);
    }

    /**
     * Ініціалізує систему зберігання духовних коренів.
     * Реєструє сховище для гравців.
     */
    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        SpiritualRootStorage.class, Entity.class::isInstance,
                        SpiritualRootStorage::new));
        SpiritualRootEvents.ADD.register((instance, living, advancement, notifyPlayer, rootMessage) -> CompoundEventResult.interruptTrue(instance));
    }

    /**
     * Зберігає стан духовних коренів у NBT.
     *
     * @param data CompoundTag для збереження даних
     */
    @Override
    public void save(CompoundTag data) {
        ListTag elementsTag = new ListTag();
        spiritualRoots.values().forEach(instance -> {
            elementsTag.add(instance.toNBT());
            instance.resetDirty();
        });
        data.put(SPIRITUAL_ROOTS_KEY, elementsTag);
    }

    /**
     * Завантажує стан духовних коренів з NBT.
     *
     * @param data CompoundTag з даними для завантаження
     */
    @Override
    public void load(CompoundTag data) {
        if (data.contains("resetExistingData")) {
            this.spiritualRoots.clear();
        }
        for (Tag tag : data.getList(SPIRITUAL_ROOTS_KEY, Tag.TAG_COMPOUND)) {
            try {
                SpiritualRootInstance instance = SpiritualRootInstance.fromNBT((CompoundTag) tag);
                this.spiritualRoots.put(instance.getSpiritualRootId(), instance);
            } catch (Exception e) {
                EternalCoreStorage.LOG.error("Failed to load root instance from NBT", e);
            }
        }
    }

    @Override
    public Collection<SpiritualRootInstance> getGainedRoots() {
        return this.spiritualRoots.values();
    }

    @Override
    public void saveOutdated(CompoundTag data) {
        if (this.hasRemovedRoots) {
            this.hasRemovedRoots = false;
            data.putBoolean("resetExistingData", true);
            super.saveOutdated(data);
        } else {
            ListTag skillList = new ListTag();
            for (SpiritualRootInstance instance : this.spiritualRoots.values()) {
                if (!instance.isDirty()) continue;
                skillList.add(instance.toNBT());
                instance.resetDirty();
            }
            data.put(SPIRITUAL_ROOTS_KEY, skillList);
        }
    }

    /**
     * Отримує власника цього сховища.
     *
     * @return Жива сутність-власник сховища
     */
    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }

    /**
     * Повертає колекцію всіх духовних коренів сутності.
     *
     * @return Незмінна колекція екземплярів духовних коренів
     */
    @Override
    public Map<ResourceLocation, SpiritualRootInstance> getSpiritualRoots() {
        return spiritualRoots;
    }

    /**
     * Додає новий духовний корінь до сутності.
     *
     * @param instance Екземпляр духовного кореня для додавання
     * @param advance Чи потрібно застосовувати просунуті ефекти
     * @param notify Чи потрібно сповіщати гравця
     * @param component Компонент повідомлення для сповіщення (може бути null)
     * @return true якщо корінь успішно додано, false якщо додавання скасовано
     */
    @Override
    public boolean addSpiritualRoot(SpiritualRootInstance instance, boolean advance, boolean notify, @Nullable MutableComponent component) {
        if (this.spiritualRoots.containsKey(instance.getSpiritualRootId())) {
            EternalCoreStorage.LOG.debug("Tried to register a deduplicate of {}.", instance.getSpiritualRoot());
            return false;
        }


        Changeable<MutableComponent> rootMessage = Changeable.of(component);
        Changeable<Boolean> notifyPlayer = Changeable.of(notify);
        CompoundEventResult<SpiritualRootInstance> result = SpiritualRootEvents.ADD.invoker().add(instance, getOwner(), advance, notifyPlayer, rootMessage);
        if (result.isFalse()) return false;
        LivingEntity owner = getOwner();
        if (rootMessage.isPresent()) getOwner().sendSystemMessage(rootMessage.get());
        SpiritualRootInstance newInstance = result.object();
        newInstance.markDirty();
        newInstance.onAdd(owner);
        spiritualRoots.put(newInstance.getSpiritualRootId(), newInstance);
        markDirty();
        return true;
    }

    /**
     * Updates a root instance and optionally synchronizes the change across the network.
     * <p>
     *
     * @param updatedInstance The instance to update
     * @param sync            If true, synchronizes the change to all clients/server
     */
    @Override
    public void updateRoot(SpiritualRootInstance updatedInstance, boolean sync) {
        updatedInstance.markDirty();
        spiritualRoots.put(updatedInstance.getSpiritualRootId(), updatedInstance);
        if (sync) markDirty();
    }

    @Override
    public void forEachRoot(BiConsumer<SpiritualRootStorage, SpiritualRootInstance> biConsumer) {
        List.copyOf(spiritualRoots.values()).forEach(spiritualRootInstance -> biConsumer.accept(this, spiritualRootInstance));
        markDirty();
    }

    @Override
    public void forgetRoot(@NotNull ResourceLocation resourceLocation, @Nullable MutableComponent component) {
        if (!this.spiritualRoots.containsKey(resourceLocation)) return;
        SpiritualRootInstance instance = this.spiritualRoots.get(resourceLocation);

        Changeable<MutableComponent> forgetMessage = Changeable.of(component);
        EventResult result = SpiritualRootEvents.FORGET_SPIRITUAL_ROOT.invoker().forget(instance, getOwner(), forgetMessage);
        if (result.isFalse()) return;

        if (forgetMessage.isPresent()) getOwner().sendSystemMessage(forgetMessage.get());
        instance.markDirty();

        this.getGainedRoots().remove(instance);
        this.hasRemovedRoots = true;
        markDirty();
    }

    public void sync(){
        CompoundTag data = new CompoundTag();
        saveOutdated(data);
        InternalSpiritualRootPacketActions.sendSyncStoragePayload(data);
    }
}
