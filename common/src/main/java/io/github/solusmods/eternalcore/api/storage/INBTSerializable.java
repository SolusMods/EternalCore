package io.github.solusmods.eternalcore.api.storage;

import net.minecraft.nbt.Tag;

public interface INBTSerializable<T extends Tag> {
    T toNBT();

    T serialize(T tag);

    void deserialize(T tag);
}
