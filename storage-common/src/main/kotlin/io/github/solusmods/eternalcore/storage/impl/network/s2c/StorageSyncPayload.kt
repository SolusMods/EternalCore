package io.github.solusmods.eternalcore.storage.impl.network.s2c

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.common.custom.CustomPacketPayload


interface StorageSyncPayload : CustomPacketPayload {
    val isUpdate: Boolean

    val storageTag: CompoundTag
}