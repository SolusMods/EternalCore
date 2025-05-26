package io.github.solusmods.eternalcore.abilities.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import io.github.solusmods.eternalcore.abilities.api.Abilities;
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI;
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents;
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import io.github.solusmods.eternalcore.stage.api.entity.EntityEvents;
import io.github.solusmods.eternalcore.storage.EternalCoreStorage;
import io.github.solusmods.eternalcore.storage.api.Storage;
import io.github.solusmods.eternalcore.storage.api.StorageEvents;
import io.github.solusmods.eternalcore.storage.api.StorageKey;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

@Log4j2
public class AbilityStorage extends Storage implements Abilities {
    @Getter
    private static StorageKey<AbilityStorage> key = null;
    public static final int INSTANCE_UPDATE = 20;
    public static final int PASSIVE_SKILL = 100;
    public static final Multimap<UUID, TickingAbility> tickingAbilities = ArrayListMultimap.create();
    private static final String ABILITY_LIST_KEY = "abilities";
    private static final ResourceLocation ID = EternalCoreAbilities.create("ability_storage");

    public static void init() {
        StorageEvents.REGISTER_ENTITY_STORAGE.register(registry -> key = registry.register(ID, AbilityStorage.class, LivingEntity.class::isInstance, target -> new AbilityStorage((LivingEntity) target)));

        EntityEvents.LIVING_HURT.register((entity, source, changeable) -> {
            Abilities skills = AbilityAPI.getAbilitiesFrom(entity);
            if (AbilityEvents.ABILITY_DAMAGE_PRE_CALCULATION.invoker().calculate(skills, entity, source, changeable).isFalse()) return EventResult.interruptFalse();
            if (AbilityEvents.ABILITY_DAMAGE_CALCULATION.invoker().calculate(skills, entity, source, changeable).isFalse()) return EventResult.interruptFalse();
            if (AbilityEvents.ABILITY_DAMAGE_POST_CALCULATION.invoker().calculate(skills, entity, source, changeable).isFalse()) return EventResult.interruptFalse();
            return EventResult.pass();
        });

        EntityEvents.LIVING_POST_TICK.register(entity -> {
            Level level = entity.level();
            if (level.isClientSide()) return;
            Abilities storage = AbilityAPI.getAbilitiesFrom(entity);
            handleAbilityTick(entity, level, storage);
            if (entity instanceof Player player) handleAbilityHeldTick(player, storage);
            storage.markDirty();
        });

        PlayerEvent.PLAYER_QUIT.register(player -> {
            Multimap<UUID, TickingAbility> multimap = tickingAbilities;
            if (multimap.containsKey(player.getUUID())) {
                for (TickingAbility skill : multimap.get(player.getUUID())) {
                    Optional<AbilityInstance> instance = AbilityAPI.getAbilitiesFrom(player).getAbility(skill.getAbility());
                    if (instance.isEmpty()) continue;
                    skill.getAbility().removeAttributeModifiers(instance.get(), player, skill.getMode());
                }
                multimap.removeAll(player.getUUID());
            }
        });
    }

    private static void handleAbilityTick(LivingEntity entity, Level level, Abilities storage) {
        MinecraftServer server = level.getServer();
        if (server == null) return;

        boolean shouldPassiveConsume = server.getTickCount() % INSTANCE_UPDATE == 0;
        if (!shouldPassiveConsume) return;
        checkPlayerOnlyEffects(entity, storage);

        boolean passiveAbilityActivate = server.getTickCount() % PASSIVE_SKILL == 0;
        if (!passiveAbilityActivate) return;

        tickAbilities(entity, storage);
    }

    private static void tickAbilities(LivingEntity entity, Abilities storage) {
        List<AbilityInstance> tickingAbilities = new ArrayList<>();
        for (AbilityInstance instance : storage.getLearnedAbilities()) {
            Optional<AbilityInstance> optional = storage.getAbility(instance.getAbility());
            if (optional.isEmpty()) continue;

            AbilityInstance skillInstance = optional.get();
            if (!skillInstance.canInteractAbility(entity)) continue;
            if (!skillInstance.canTick(entity)) continue;
            if (AbilityEvents.ABILITY_PRE_TICK.invoker().tick(skillInstance, entity).isFalse()) continue;
            tickingAbilities.add(skillInstance);
        }

        for (AbilityInstance instance : tickingAbilities) {
            instance.onTick(entity);
            AbilityEvents.ABILITY_POST_TICK.invoker().tick(instance, entity);
        }
    }

    private static void checkPlayerOnlyEffects(LivingEntity entity, Abilities storage) {
        if (!(entity instanceof Player)) return;
        List<AbilityInstance> toBeRemoved = new ArrayList<>();

        for (AbilityInstance instance : storage.getLearnedAbilities()) {
            // Update cooldown
            for (int i = 0; i < instance.getModes(); i++) {
                if (!instance.onCoolDown(i)) continue;
                if (!AbilityEvents.ABILITY_UPDATE_COOLDOWN.invoker().cooldown(instance, entity, instance.getCoolDown(i), i).isFalse())
                    instance.decreaseCoolDown(1, i);
            }

            // Update temporary skill timer
            if (!instance.isTemporaryAbility()) continue;
            instance.decreaseRemoveTime(1);
            if (!instance.shouldRemove()) continue;
            toBeRemoved.add(instance);
        }

        // Remove temporary skills
        for (AbilityInstance instance : toBeRemoved) {
            storage.forgetAbility(instance);
        }
    }

    private static void handleAbilityHeldTick(Player player, Abilities storage) {
        if (!tickingAbilities.containsKey(player.getUUID())) return;
        tickingAbilities.get(player.getUUID()).removeIf(tickingAbility -> {
            if (!tickingAbility.tick(storage, player)) {
                Optional<AbilityInstance> instance = storage.getAbility(tickingAbility.getAbility());
                if (instance.isEmpty()) return true;
                tickingAbility.getAbility().removeAttributeModifiers(instance.get(), player, tickingAbility.getMode());
                return true;
            }
            return false;
        });
    }

    private final Map<ResourceLocation, AbilityInstance> abilityInstance = new HashMap<>();
    private boolean hasRemovedAbilities = false;

    protected AbilityStorage(LivingEntity holder) {
        super(holder);
    }

    public Collection<AbilityInstance> getLearnedAbilities() {
        return this.abilityInstance.values();
    }

    public void updateAbility(@NonNull AbilityInstance updatedInstance, boolean sync) {
        updatedInstance.markDirty();
        this.abilityInstance.put(updatedInstance.getAbilityId(), updatedInstance);
        if (sync) markDirty();
    }

    public boolean learnAbility(@NonNull AbilityInstance instance, MutableComponent component) {
        if (this.abilityInstance.containsKey(instance.getAbilityId())) {
            log.debug("Tried to register a deduplicate of {}.", instance.getAbilityId());
            return false;
        }

        Changeable<MutableComponent> unlockMessage = Changeable.of(component);
        EventResult result = AbilityEvents.UNLOCK_ABILITY.invoker().unlockAbility(instance, getOwner(), unlockMessage);
        if (result.isFalse()) return false;

        instance.markDirty();
        this.abilityInstance.put(instance.getAbilityId(), instance);
        if (unlockMessage.isPresent()) getOwner().sendSystemMessage(unlockMessage.get());
        instance.onLearnAbility(this.getOwner());
        markDirty();
        return true;
    }

    public Optional<AbilityInstance> getAbility(@NonNull ResourceLocation skillId) {
        return Optional.ofNullable(this.abilityInstance.get(skillId));
    }

    public void forgetAbility(@NotNull ResourceLocation skillId, @Nullable MutableComponent component) {
        if (!this.abilityInstance.containsKey(skillId)) return;
        AbilityInstance instance = this.abilityInstance.get(skillId);

        Changeable<MutableComponent> forgetMessage = Changeable.of(component);
        EventResult result = AbilityEvents.REMOVE_ABILITY.invoker().removeAbility(instance, getOwner(), forgetMessage);
        if (result.isFalse()) return;

        if (forgetMessage.isPresent()) getOwner().sendSystemMessage(forgetMessage.get());
        instance.onForgetAbility(this.getOwner());
        instance.markDirty();

        this.getLearnedAbilities().remove(instance);
        this.hasRemovedAbilities = true;
        markDirty();
    }

    public void forEachAbility(BiConsumer<AbilityStorage, AbilityInstance> abilityInstanceBiConsumer) {
        List.copyOf(this.abilityInstance.values()).forEach(abilityInstance -> abilityInstanceBiConsumer.accept(this, abilityInstance));
        markDirty();
    }

    public void handleAbilityRelease(ResourceLocation skillId, int heldTick, int keyNumber, int mode) {
        getAbility(skillId).ifPresent(skillInstance -> {
            Changeable<AbilityInstance> changeable = Changeable.of(skillInstance);
            if (AbilityEvents.RELEASE_ABILITY.invoker().releaseAbility(changeable, this.getOwner(), keyNumber, mode, heldTick).isFalse()) return;
            AbilityInstance skill = changeable.get();
            if (skill == null) return;

            if (skill.canInteractAbility(getOwner()) && mode < skill.getModes()) {
                if (!skill.onCoolDown(mode) || skill.canIgnoreCoolDown(getOwner(), mode)) {
                    skill.onRelease(getOwner(), heldTick, keyNumber, mode);
                    if (skill.isDirty()) markDirty();
                }
            }

            skill.removeAttributeModifiers(getOwner(), mode);
            UUID ownerID = getOwner().getUUID();
            if (tickingAbilities.containsKey(ownerID))
                tickingAbilities.get(ownerID).removeIf(tickingAbility -> tickingAbility.getAbility() == skill.getAbility());
        });
    }

    @Override
    public void save(CompoundTag data) {
        ListTag skillList = new ListTag();
        this.abilityInstance.values().forEach(instance -> {
            skillList.add(instance.toNBT());
            instance.resetDirty();
        });
        data.put(ABILITY_LIST_KEY, skillList);
    }

    @Override
    public void load(CompoundTag data) {
        if (data.contains("resetExistingData")) {
            this.abilityInstance.clear();
        }

        for (Tag tag : data.getList(ABILITY_LIST_KEY, Tag.TAG_COMPOUND)) {
            try {
                AbilityInstance instance = AbilityInstance.fromNBT((CompoundTag) tag);
                this.abilityInstance.put(instance.getAbilityId(), instance);
            } catch (Exception e) {
                EternalCoreStorage.LOG.error("Failed to load ability instance from NBT", e);
            }
        }
    }

    @Override
    public void saveOutdated(CompoundTag data) {
        if (this.hasRemovedAbilities) {
            this.hasRemovedAbilities = false;
            data.putBoolean("resetExistingData", true);
            super.saveOutdated(data);
        } else {
            ListTag skillList = new ListTag();
            for (AbilityInstance instance : this.abilityInstance.values()) {
                if (!instance.isDirty()) continue;
                skillList.add(instance.toNBT());
                instance.resetDirty();
            }
            data.put(ABILITY_LIST_KEY, skillList);
        }
    }

    protected LivingEntity getOwner() {
        return (LivingEntity) this.holder;
    }
}
