package io.github.solusmods.eternalcore.impl.spiritual_root;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.spiritual_root.SpiritualRootEvents;
import io.github.solusmods.eternalcore.api.spiritual_root.SpiritualRoots;
import io.github.solusmods.eternalcore.api.storage.AbstractStorage;
import io.github.solusmods.eternalcore.api.storage.StorageEvents;
import io.github.solusmods.eternalcore.api.storage.StorageHolder;
import io.github.solusmods.eternalcore.api.storage.StorageKey;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Сховище для управління духовними коренями сутності.
 */
public class SpiritualRootStorage extends AbstractStorage implements SpiritualRoots {

    public static final ResourceLocation ID = EternalCore.create("spiritual_root_storage");
    private static final String SPIRITUAL_ROOTS_KEY = "spiritual_roots_key";
    @Getter
    private static StorageKey<SpiritualRootStorage> key = null;

    private final Map<ResourceLocation, AbstractSpiritualRoot> spiritualRoots = new HashMap<>();

    private boolean hasRemovedRoots = false;

    protected SpiritualRootStorage(StorageHolder holder) {
        super(holder);
    }

    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID, SpiritualRootStorage.class, Entity.class::isInstance, SpiritualRootStorage::new));
    }

    @Override
    public void save(CompoundTag data) {
        ListTag rootsTag = new ListTag();
        spiritualRoots.values().forEach(root -> rootsTag.add(root.toNBT()));
        data.put(SPIRITUAL_ROOTS_KEY, rootsTag);
    }

    @Override
    public void load(CompoundTag data) {
        if (data.contains("resetExistingData")) {
            this.spiritualRoots.clear();
        }
        for (Tag tag : data.getList(SPIRITUAL_ROOTS_KEY, Tag.TAG_COMPOUND)) {
            try {
                var root = AbstractSpiritualRoot.fromNBT((CompoundTag) tag);
                this.spiritualRoots.put(root.getResource(), root);
            } catch (Exception e) {
                EternalCore.LOG.error("Failed to load spiritual root from NBT", e);
            }
        }
    }

    @Override
    public Collection<AbstractSpiritualRoot> getGainedRoots() {
        return this.spiritualRoots.values();
    }

    @Override
    public void saveOutdated(CompoundTag data) {
        if (this.hasRemovedRoots) {
            this.hasRemovedRoots = false;
            data.putBoolean("resetExistingData", true);
            super.saveOutdated(data);
        } else {
            ListTag rootList = new ListTag();
            for (AbstractSpiritualRoot root : this.spiritualRoots.values()) {
                rootList.add(root.toNBT());
            }
            data.put(SPIRITUAL_ROOTS_KEY, rootList);
        }
    }

    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }

    @Override
    public Map<ResourceLocation, AbstractSpiritualRoot> getSpiritualRoots() {
        return spiritualRoots;
    }

    @Override
    public boolean addSpiritualRoot(@NotNull AbstractSpiritualRoot root, boolean advancement, boolean notify, @Nullable MutableComponent component) {
        if (this.spiritualRoots.containsKey(root.getResource())) {
            EternalCore.LOG.debug("Tried to register duplicate spiritual root: {}", root.getResource());
            return false;
        }

        var rootMessage = Changeable.of(component);
        var notifyPlayer = Changeable.of(notify);

        var result = SpiritualRootEvents.ADD.invoker().add(root, getOwner(), advancement, notifyPlayer, rootMessage);
        if (result.isFalse()) return false;

        var newRoot = result.object();
        this.spiritualRoots.put(newRoot.getResource(), newRoot);

        if (rootMessage.isPresent()) getOwner().sendSystemMessage(rootMessage.get());
        markDirty();
        return true;
    }

    @Override
    public void forEachRoot(BiConsumer<ResourceLocation, AbstractSpiritualRoot> consumer) {
        spiritualRoots.forEach(consumer);
    }

    @Override
    public void forgetRoot(@NotNull ResourceLocation rootId, @Nullable MutableComponent component) {
        if (!this.spiritualRoots.containsKey(rootId)) return;

        var root = this.spiritualRoots.get(rootId);
        var forgetMessage = Changeable.of(component);

        var result = SpiritualRootEvents.FORGET_SPIRITUAL_ROOT.invoker().forget(root, getOwner(), forgetMessage);
        if (result.isFalse()) return;

        if (forgetMessage.isPresent()) getOwner().sendSystemMessage(forgetMessage.get());
        this.spiritualRoots.remove(rootId);
        this.hasRemovedRoots = true;
        markDirty();
    }

    @Override
    public boolean updateSpiritualRoot(@NonNull AbstractSpiritualRoot root, boolean advancement, boolean notify, @Nullable MutableComponent message) {
        if (!this.spiritualRoots.containsKey(root.getResource())) return false;

        var rootMessage = Changeable.of(message);
        var notifyPlayer = Changeable.of(notify);

        var result = SpiritualRootEvents.UPDATE.invoker().update(root, getOwner(), rootMessage);
        if (result.isFalse()) return false;
        if (rootMessage.isPresent()) getOwner().sendSystemMessage(rootMessage.get());
        this.spiritualRoots.put(root.getResource(), root);
        markDirty();
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s{roots=[%s], owner={%s}}", this.getClass().getSimpleName(), this.spiritualRoots.values(), getOwner().toString());
    }
}
