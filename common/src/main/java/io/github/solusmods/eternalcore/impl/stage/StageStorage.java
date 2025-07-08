package io.github.solusmods.eternalcore.impl.stage;

import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.network.util.Changeable;
import io.github.solusmods.eternalcore.api.stage.*;
import io.github.solusmods.eternalcore.api.storage.AbstractStorage;
import io.github.solusmods.eternalcore.api.storage.StorageEvents;
import io.github.solusmods.eternalcore.api.storage.StorageHolder;
import io.github.solusmods.eternalcore.api.storage.StorageKey;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Система зберігання та управління стадіями гравців.
 */
public class StageStorage extends AbstractStorage implements Stages, IReachedStages {

    public static final ResourceLocation ID = EternalCore.create("stage_storage");
    private static final String STAGE_KEY = "stage_key";
    private static final String REACHED_STAGES_KEY = "reached_stages_key";
    @Getter
    private static StorageKey<StageStorage> key = null;
    private final List<AbstractStage> reachedStages = new ArrayList<>();
    private AbstractStage stage = null;

    protected StageStorage(StorageHolder holder) {
        super(holder);
    }

    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        StageStorage.class, Entity.class::isInstance,
                        StageStorage::new));
    }

    @Override
    public Collection<AbstractStage> getReachedStages() {
        return reachedStages;
    }

    @Override
    public boolean addStage(AbstractStage stage, boolean breakthrough, boolean teleportToSpawn, @Nullable MutableComponent component) {
        if (hasReached(stage)) return false;

        Changeable<MutableComponent> stageMessage = Changeable.of(component);
        Changeable<Boolean> teleport = Changeable.of(teleportToSpawn);
        EventResult result = StageEvents.REACH_STAGE.invoker().reach(stage, getOwner(), breakthrough, teleport, stageMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();
        if (stageMessage.isPresent()) owner.sendSystemMessage(stageMessage.get());
        stage.onReach(owner);
        reachedStages.add(stage);
        markDirty();
        return true;
    }

    @Override
    public Optional<AbstractStage> getStage() {
        return Optional.ofNullable(stage);
    }

    @Override
    public boolean setStage(AbstractStage stage, boolean advancement, boolean notify, @Nullable MutableComponent component) {
        AbstractStage current = this.stage;
        Changeable<MutableComponent> stageMessage = Changeable.of(component);
        Changeable<Boolean> notifyPlayer = Changeable.of(notify);
        EventResult result = StageEvents.SET_STAGE.invoker().set(current, getOwner(), stage, advancement, notifyPlayer, stageMessage);
        if (result.isFalse()) return false;

        LivingEntity owner = getOwner();
        if (stageMessage.isPresent()) owner.sendSystemMessage(stageMessage.get());
        stage.onSet(owner);
        this.stage = stage;
        markDirty();
        return true;
    }

    @Override
    public void save(CompoundTag data) {
        if (stage != null) {
            CompoundTag stageTag = new CompoundTag();
            stageTag.putString("id", stage.getResource().toString());
            data.put(STAGE_KEY, stageTag);
        }
        ListTag reachedStagesTag = new ListTag();
        for (AbstractStage stage : reachedStages) {
            reachedStagesTag.add(stage.toNBT());
        }
        data.put(REACHED_STAGES_KEY, reachedStagesTag);
    }

    @Override
    public void load(CompoundTag data) {
        reachedStages.clear();
        if (data.contains(STAGE_KEY, Tag.TAG_COMPOUND)) {
            CompoundTag tag = data.getCompound(STAGE_KEY);
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
            if (id != null) {
                this.stage = StageAPI.getStageRegistry().get(id);
            }
        }
        for (Tag tag : data.getList(REACHED_STAGES_KEY, Tag.TAG_COMPOUND)) {
            try {
                CompoundTag stageTag = (CompoundTag) tag;
                var stage = AbstractStage.fromNBT(stageTag);
                if (stage != null) {
                    reachedStages.add(stage);
                }
            } catch (Exception e) {
                EternalCore.LOG.error("Failed to load stage from NBT", e);
            }
        }
    }

    private boolean hasReached(AbstractStage stage) {
        for (AbstractStage s : reachedStages) {
            if (s.getId().equals(stage.getId())) {
                return true;
            }
        }
        return false;
    }

    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }

    @Override
    public String toString() {
        return String.format("%s{currentStage={%s}, reachedStages=[%s], owner={%s}}", this.getClass().getSimpleName(), getStage().toString(), getReachedStages().size(), getOwner().toString());
    }
}
