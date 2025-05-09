package io.github.solusmods.eternalcore.storage.impl.network.c2s;


import io.github.solusmods.eternalcore.storage.api.StorageKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface StoragesSyncPayload extends CustomPacketPayload {
    boolean isUpdate();

    CompoundTag storageTag();

    StorageKey<?> key();
}
