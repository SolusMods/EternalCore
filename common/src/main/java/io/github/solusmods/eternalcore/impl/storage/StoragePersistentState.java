package io.github.solusmods.eternalcore.impl.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class StoragePersistentState extends SavedData {
    public static final ThreadLocal<Boolean> LOADING = ThreadLocal.withInitial(() -> false);
    private final CombinedStorage storage;

    public StoragePersistentState(CombinedStorage storage) {
        this.storage = storage;
    }

    public static Factory<StoragePersistentState> getFactory(CombinedStorage storage) {
        return new Factory<>(
                () -> new StoragePersistentState(storage),
                (tag, provider) -> fromNBT(storage, tag),
                DataFixTypes.LEVEL
        );
    }

    public static StoragePersistentState fromNBT(CombinedStorage storage, CompoundTag tag) {
        StoragePersistentState state = new StoragePersistentState(storage);
        state.storage.handleUpdatePacket(tag);
        return state;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        return this.storage.toNBT();
    }
}
