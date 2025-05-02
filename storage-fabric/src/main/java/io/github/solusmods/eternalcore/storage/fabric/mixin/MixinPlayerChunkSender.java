/*
 * Copyright (c) 2025. ManasMods
 * GNU General Public License 3
 */

package io.github.solusmods.eternalcore.storage.fabric.mixin;

import io.github.solusmods.eternalcore.storage.impl.StorageManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerChunkSender.class)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MixinPlayerChunkSender {
    @Inject(method = "sendChunk", at = @At("RETURN"))
    private static void syncPlayer(ServerGamePacketListenerImpl serverGamePacketListenerImpl, ServerLevel serverLevel, LevelChunk levelChunk, CallbackInfo ci) {
        StorageManager.syncTarget(levelChunk, serverGamePacketListenerImpl.player);
    }
}
