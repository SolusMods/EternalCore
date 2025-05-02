package io.github.solusmods.eternalcore.spiritual_root.api;

import lombok.NonNull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface SpiritualRoots {
    Collection<SpiritualRootInstance> getSpiritualRoots();

    default boolean addSpiritualRoot(@NotNull ResourceLocation realmId, boolean notify) {
        return addSpiritualRoot(realmId, notify, null);
    }

    default boolean addSpiritualRoot(@NotNull ResourceLocation realmId, boolean notify, @Nullable MutableComponent component) {
        SpiritualRoot spiritualRoot = SpiritualRootAPI.getSpiritualRootRegistry().get(realmId);
        if (spiritualRoot == null) return false;
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(), false, notify, component);
    }

    default boolean addSpiritualRoot(@NonNull SpiritualRoot spiritualRoot, boolean notify) {
        return addSpiritualRoot(spiritualRoot, notify, null);
    }

    default boolean addSpiritualRoot(@NonNull SpiritualRoot spiritualRoot, boolean notify, @Nullable MutableComponent component) {
        return addSpiritualRoot(spiritualRoot.createDefaultInstance(), false, notify, component);
    }

    default boolean addSpiritualRoot(SpiritualRootInstance instance, boolean advance, boolean notify) {
        return addSpiritualRoot(instance, advance, notify, null);
    }

    /**
     * Create Random Root Count
     *
     * @return Random Root Count
     */
    default int getRandomRootCount() {
        // Зважений випадковий вибір:
        // 1 корінь - 10% (найрідкісніший, найсильніший)
        // 2 корені - 20%
        // 3 корені - 40% (найпоширеніший)
        // 4 корені - 20%
        // 5 коренів - 10% (найслабший, але найбільш гнучкий)

        double random = Math.random();
        if (random < 0.1) return 1;
        if (random < 0.3) return 2;
        if (random < 0.7) return 3;
        if (random < 0.9) return 4;
        return 5;
    }

    boolean addSpiritualRoot(SpiritualRootInstance instance, boolean advance, boolean notify, @Nullable MutableComponent component);

    float getCultivationEfficiency(SpiritualRootInstance instance);

    float getCultivationSpeedMultiplier();

    void generateRandomRoots(List<SpiritualRoot> roots);

    RootType getDominantRootType();

    void markDirty();
}
