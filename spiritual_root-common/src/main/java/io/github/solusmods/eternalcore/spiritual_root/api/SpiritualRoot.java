package io.github.solusmods.eternalcore.spiritual_root.api;

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

@Getter
@AllArgsConstructor
public abstract class SpiritualRoot {
    public final RootType type;
    protected final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();



    public SpiritualRootInstance createDefaultInstance() {
        return new SpiritualRootInstance(this);
    }

    /**
     * Used to get the {@link ResourceLocation} id of this spiritual root.
     */
    @Nullable
    public ResourceLocation getRegistryName() {
        return SpiritualRootAPI.getSpiritualRootRegistry().getId(this);
    }

    /**
     * Used to get the {@link MutableComponent} name of this spiritual root for translation.
     */
    @Nullable
    public MutableComponent getName() {
        ResourceLocation id = this.getRegistryName();
        return id == null ? null : Component.translatable(String.format("%s.spiritual_root.%s", id.getNamespace(), id.getPath().replace('/', '.')));
    }

    public RootLevels getMaxLevel() {
        return RootLevels.X;
    }

    public boolean isMastered(SpiritualRootInstance instance, LivingEntity entity) {
        return instance.getExperience() >= instance.getNextLevel().getExperience();
    }

    public void addExperience(SpiritualRootInstance instance, LivingEntity living, float exp) {
        if (!isMastered(instance, living)) {
            instance.setExperience(instance.getExperience() + exp);
            if (isMastered(instance, living)) {
                onMastered(instance, living);
            }
        }
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
     * Applies the attribute modifiers of this {@link SpiritualRoot} on the {@link LivingEntity} when set.
     *
     * @param entity   Affected {@link LivingEntity} being this {@link SpiritualRoot}.
     * @param instance Affected {@link SpiritualRootInstance}
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
     * Removes the attribute modifiers of this rootId from the {@link LivingEntity} when changing {@link SpiritualRoot}.
     *
     * @param entity Affected {@link LivingEntity} being this {@link SpiritualRoot}.
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

    public void increaseStrength(SpiritualRootInstance instance, LivingEntity living, float amount) {
        instance.setStrength(Math.min(1.0f, instance.getStrength() + amount));
    }

    /**
     * Returns the default {@link SpiritualRoot} that this {@link SpiritualRoot} advances into.
     *
     * @see SpiritualRootInstance#getAdvanced(LivingEntity)
     */
    @Nullable
    public SpiritualRoot getAdvanced(SpiritualRootInstance instance, LivingEntity living) {
        return null;
    }

    /**
     * Called when the {@link LivingEntity} mastered this {@link SpiritualRoot}.
     *
     * @param instance Affected {@link SpiritualRootInstance}
     * @param living   Affected {@link LivingEntity} being this {@link SpiritualRoot}.
     */
    public void onMastered(SpiritualRootInstance instance, LivingEntity living) {
        Changeable<Boolean> notify = Changeable.of(false);
        SpiritualRootEvents.MASTERING.invoker().mastering(instance, living, false, notify, null);
    }

    /**
     * Called when the {@link LivingEntity} owning this {@link SpiritualRoot}.
     *
     * @param instance Affected {@link SpiritualRootInstance}
     * @param living   Affected {@link LivingEntity} being this {@link SpiritualRoot}.
     */
    public void onAdd(SpiritualRootInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    /**
     * Called when the {@link LivingEntity} advances this {@link SpiritualRoot}.
     *
     * @param instance Affected {@link SpiritualRootInstance}
     * @param living   Affected {@link LivingEntity} being this {@link SpiritualRoot}.
     */
    public void onAdvance(SpiritualRootInstance instance, LivingEntity living) {
        // Override this method to add your own logic
    }

    public @Nullable SpiritualRoot getOpposite(SpiritualRootInstance instance, LivingEntity entity){return null;}

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
