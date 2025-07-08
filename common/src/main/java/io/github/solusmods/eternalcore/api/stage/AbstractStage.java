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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@NoArgsConstructor
public abstract class AbstractStage implements IResource, INBTSerializable<CompoundTag> {

    private String stageID = null;
    private String stageName = null;
    @Nullable
    private CompoundTag tag = null;

    public abstract ResourceLocation getResource();

    public final ResourceLocation creteResource(String name){
        return EternalCore.create(name);
    }

    public abstract StageConfig getDefaultConfig();

    public final double getMinBaseQi() {
        return ServerConfigs.getStageConfig(this).getMinQi();
    }

    public final double getMaxBaseQi() {
        return ServerConfigs.getStageConfig(this).getMaxQi();
    }


    @Nullable
    public static AbstractStage fromNBT(CompoundTag tag) {
        if (tag.contains("Id")) {
            val id = ResourceLocation.tryParse(tag.getString("Id"));
            val abstractStage = StageAPI.getStageRegistry().get(id);
            abstractStage.deserialize(tag);
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

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        if (this.tag != null) tag.put("tag", this.tag.copy());
        return tag;
    }

    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.tag = new CompoundTag();
        }
        return this.tag;
    }

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

    public abstract List<AbstractStage> getNextBreakthroughs(@Nullable LivingEntity living);

    public abstract List<AbstractStage> getPreviousBreakthroughs(@Nullable LivingEntity living);

    @Nullable
    public abstract AbstractStage getDefaultBreakthrough(@Nullable LivingEntity living);

    @Override
    public String getClassName() {
        return "stage";
    }

    public void onSet(@Nullable LivingEntity living) {
    }

    public void onReach(@Nullable LivingEntity living) {
    }

    public void onTrack(@Nullable LivingEntity living) {
    }

    public void onTick(@Nullable LivingEntity living) {
    }

    public void onBreakthrough(@Nullable LivingEntity living) {
    }

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


    public boolean is(TagKey<AbstractStage> tag) {
        return StageRegistry.getRegistrySupplier(this).is(tag);
    }
}
