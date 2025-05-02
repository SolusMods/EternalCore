package io.github.solusmods.eternalcore.realm.api;

import com.mojang.datafixers.util.Pair;
import io.github.solusmods.eternalcore.realm.ModuleConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Realm {
    protected final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();

    public RealmInstance createDefaultInstance() {
        return new RealmInstance(this);
    }

    /**
     * Return base health for this {@link Realm}
     *
     * @see RealmInstance#getBaseHealth()
     */
    public abstract double getBaseHealth();

    /**
     * Return {@link Pair} of min and max Qi for this {@link Realm}
     *
     * @see RealmInstance#getBaseQiRange()
     */
    public abstract Pair<Float, Float> getBaseQiRange();

    /**
     * Return base attack damage for this {@link Realm}
     *
     * @see RealmInstance#getBaseAttackDamage()
     */
    public abstract double getBaseAttackDamage();

    /**
     * Return base attack speed for this {@link Realm}
     *
     * @see RealmInstance#getBaseAttackSpeed()
     */
    public abstract double getBaseAttackSpeed();

    /**
     * Return knock back Resistance for this {@link Realm}
     *
     * @see RealmInstance#getKnockBackResistance()
     */
    public abstract double getKnockBackResistance();

    /**
     * Return jump height for this {@link Realm}
     *
     * @see RealmInstance#getJumpHeight()
     */
    public abstract double getJumpHeight();

    /**
     * Return movement speed for this {@link Realm}
     *
     * @see RealmInstance#getMovementSpeed()
     */
    public abstract double getMovementSpeed();

    /**
     * Used to get the type of this {@link Realm}.
     *
     * @see RealmInstance#getType()
     */
    public abstract MutableComponent getType();

    /**
     * Return sprint speed for this {@link Realm}
     *
     * @see RealmInstance#getSprintSpeed()
     */
    public double getSprintSpeed() {
        return this.getMovementSpeed() * 1.3;
    }

    /**
     * Return min Qi from {@link #getBaseQiRange()} for this {@link Realm}
     *
     * @see RealmInstance#getMinBaseQi()
     */
    public float getMinBaseQi() {
        return this.getBaseQiRange().getFirst();
    }

    /**
     * Return max Qi from {@link #getBaseQiRange()} for this {@link Realm}
     *
     * @see RealmInstance#getMaxBaseQi()
     */
    public float getMaxBaseQi() {
        return this.getBaseQiRange().getSecond();
    }

    /**
     * Показник ступеня (визначає зростання витрати Кі при прориві) початково 0.2
     *
     * @return Показник ступеня
     */
    public double getCoefficient() {
        return 0.2;
    }

    /**
     * Returns a list of all {@link Realm} that this Realm can break through into.
     *
     * @see RealmInstance#getNextBreakthroughs(LivingEntity)
     */
    public List<Realm> getNextBreakthroughs(RealmInstance instance, LivingEntity living) {
        return new ArrayList();
    }

    /**
     * Returns a list of all {@link Realm} that break through into this Realm.
     *
     * @see RealmInstance#getPreviousBreakthroughs(LivingEntity)
     */
    public List<Realm> getPreviousBreakthroughs(RealmInstance instance, LivingEntity living) {
        return new ArrayList();
    }

    /**
     * Returns the default {@link Realm} that this Realm breakthroughs into.
     *
     * @see RealmInstance#getDefaultBreakthrough(LivingEntity)
     */
    @Nullable
    public Realm getDefaultBreakthrough(RealmInstance instance, LivingEntity living) {
        return null;
    }

    /**
     * Returns the dimension that {@link LivingEntity} respawns at as this Realm.
     * Decides whether if the game should spawn a 3x3 platform of {@link BlockState} when no valid spawn is found.
     *
     * @see RealmInstance#getRespawnDimension(LivingEntity)
     */
    public Pair<ResourceKey<Level>, BlockState> getRespawnDimension(RealmInstance instance, LivingEntity owner) {
        return Pair.of(Level.OVERWORLD, Blocks.AIR.defaultBlockState());
    }

    public boolean passivelyFriendlyWith(LivingEntity entity) {
        return false;
    }

    public boolean canFly() {
        return false;
    }

    public List<Component> getRequirementsForRendering(Player player) {
        List<Component> list = new ArrayList();
        list.add(Component.translatable("%s.realm_menu.qi_requirement".formatted(ModuleConstants.MOD_ID)));
        return list;
    }

//    public double getBreakPercentage(Player player) {
//        double minimalQi = this.getMaxBaseQi();
//        return EntityRealmStorage.getBaseQI(player) * (double)100.0F / minimalQi;
//    }

    /**
     * Used to get the {@link ResourceLocation} id of this realm.
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return RealmAPI.getRealmRegistry().getId(this);
    }

    /**
     * Used to get the {@link MutableComponent} name of this realm for translation.
     */
    @Nullable
    public MutableComponent getName() {
        ResourceLocation id = this.getRegistryName();
        return id == null ? null : Component.translatable(String.format("%s.realm.%s", id.getNamespace(), id.getPath().replace('/', '.')));
    }

    @Nullable
    public MutableComponent getTrackedName() {
        ResourceLocation id = this.getRegistryName();
        MutableComponent name = Component.translatable(String.format("%s.realm.%s", id.getNamespace(), id.getPath().replace('/', '.')));
        MutableComponent track = Component.translatable("%s.rank_menu.track".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.YELLOW);
        return name.append(track);
    }

    public String getNameTranslationKey() {
        return ((TranslatableContents) this.getName().getContents()).getKey();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Realm realm = (Realm) o;
            return this.getRegistryName().equals(realm.getRegistryName());
        } else {
            return false;
        }
    }

    /**
     * Called when the {@link LivingEntity} sets to this Realm.
     *
     * @param instance Affected {@link RealmInstance}
     * @param living   Affected {@link LivingEntity} sets to this Realm.
     * @see RealmInstance#onSet(LivingEntity)
     */
    public void onSet(RealmInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} reach to this Realm.
     *
     * @param instance Affected {@link RealmInstance}
     * @param living   Affected {@link LivingEntity} sets to this Realm.
     * @see RealmInstance#onReach(LivingEntity)
     */
    public void onReach(RealmInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} track to this Realm.
     *
     * @param instance Affected {@link RealmInstance}
     * @param living   Affected {@link LivingEntity} sets to this Realm.
     * @see RealmInstance#onTrack(LivingEntity)
     */
    public void onTrack(RealmInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} Breakthrough to this Realm.
     *
     * @param instance Affected {@link RealmInstance}
     * @param living   Affected {@link LivingEntity} sets to this Realm.
     */
    public void onBreakthrough(RealmInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Called every tick of the {@link LivingEntity} owning this Realm.
     *
     * @param instance Affected {@link RealmInstance}
     * @param living   Affected {@link LivingEntity} being this Realm.
     */
    public void onTick(RealmInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Adds an attribute modifier to this realmId. This method can be called for more than one attribute.
     * The attributes are applied to an entity when the Realm is set.
     * </p>
     */
    public void addAttributeModifier(Holder<Attribute> holder, ResourceLocation resourceLocation, double amount, AttributeModifier.Operation operation) {
        this.attributeModifiers.put(holder, new AttributeTemplate(resourceLocation, amount, operation));
    }

    /**
     * Applies the attribute modifiers of this Realm on the {@link LivingEntity} when set.
     *
     * @param entity   Affected {@link LivingEntity} being this Realm.
     * @param instance Affected {@link RealmInstance}
     */
    public void addAttributeModifiers(RealmInstance instance, LivingEntity entity) {
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
     * Removes the attribute modifiers of this skillId from the {@link LivingEntity} when changing Realm.
     *
     * @param entity Affected {@link LivingEntity} being this Realm.
     */
    public void removeAttributeModifiers(RealmInstance instance, LivingEntity entity) {
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

    @RequiredArgsConstructor
    public enum Type {
        I(Component.translatable("%s.realm.type.1".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
        II(Component.translatable("%s.realm.type.2".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
        III(Component.translatable("%s.realm.type.3".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
        IV(Component.translatable("%s.realm.type.4".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
        V(Component.translatable("%s.realm.type.5".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
        VI(Component.translatable("%s.realm.type.6".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
        VII(Component.translatable("%s.realm.type.7".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
        VIII(Component.translatable("%s.realm.type.8".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
        IX(Component.translatable("%s.realm.type.9".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.GREEN)),
        X(Component.translatable("%s.realm.type.10".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.RED)),
        XI(Component.translatable("%s.realm.type.11".formatted(ModuleConstants.MOD_ID)).withStyle(ChatFormatting.RED));

        @Getter
        private final MutableComponent name;
    }

    public record AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        public AttributeTemplate(ResourceLocation id, double amount, AttributeModifier.Operation operation) {
            this.id = id;
            this.amount = amount;
            this.operation = operation;
        }

        public AttributeModifier create() {
            return new AttributeModifier(this.id, this.amount, this.operation);
        }

        public ResourceLocation id() {
            return this.id;
        }

        public double amount() {
            return this.amount;
        }

        public AttributeModifier.Operation operation() {
            return this.operation;
        }
    }

}
