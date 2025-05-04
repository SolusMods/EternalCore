package io.github.solusmods.eternalcore.stage.impl;

import dev.architectury.event.EventResult;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.stage.EternalCoreStage;
import io.github.solusmods.eternalcore.stage.api.*;
import io.github.solusmods.eternalcore.stage.api.entity.EntityEvents;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageEvents;
import io.github.solusmods.eternalcore.storage.api.StorageHolder;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class StageStorage extends Storage implements Stages, IReachedStages {
    private static final String STAGE_KEY = "stage_key";
    private static final String REACHED_STAGES_KEY = "reached_stages_key";
    public static final ResourceLocation ID = EternalCoreStage.create("stage_storage");
    @Getter
    private static StorageKey<StageStorage> key = null;
    private StageInstance stageInstance = null;
    private final Collection<StageInstance> reachedStages = new ArrayList<>();

    protected StageStorage(StorageHolder holder) {
        super(holder);
    }

    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry ->
                key = registry.register(ID,
                        StageStorage.class, Player.class::isInstance,
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

    @Override
    public Collection<StageInstance> getReachedStages() {
        return reachedStages;
    }

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

    @Override
    public Optional<StageInstance> getStage() {
        return Optional.ofNullable(stageInstance);
    }

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

    @Override
    public void save(CompoundTag data) {
        if (stageInstance != null)
            data.put(STAGE_KEY, this.stageInstance.toNBT());
        CompoundTag reachedStagesTag = new CompoundTag();
        reachedStages.forEach(instance -> reachedStagesTag.put(instance.getStageId().toString(), instance.toNBT()));
        data.put(REACHED_STAGES_KEY, reachedStagesTag);
    }

    @Override
    public void load(CompoundTag data) {
        stageInstance = StageInstance.fromNBT(data.getCompound(STAGE_KEY));
        loadCollections(data);
    }

    private void loadCollections(CompoundTag data) {
        reachedStages.clear();
        loadInstanceCollection(data, REACHED_STAGES_KEY, reachedStages, StageInstance::fromNBT);
    }

    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }
}
