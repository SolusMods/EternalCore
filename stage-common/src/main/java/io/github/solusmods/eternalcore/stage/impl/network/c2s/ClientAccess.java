package io.github.solusmods.eternalcore.stage.impl.network.c2s;

import io.github.solusmods.eternalcore.stage.api.Stage;
import io.github.solusmods.eternalcore.stage.api.StageAPI;
import io.github.solusmods.eternalcore.stage.api.StageInstance;
import io.github.solusmods.eternalcore.stage.api.Stages;
import io.github.solusmods.eternalcore.stage.impl.StageStorage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {

    static void handle(SyncStagesStoragePayload packet, ServerPlayer player) {
        player.eternalCore$getStorageOptional(StageStorage.getKey()).
                ifPresent(storage -> storage.load(packet.data()));
    }

    static void handle(RequestStageBreakthroughPacket packet, Player player){
        if (player == null) return;

        Stages storage = StageAPI.getStageFrom(player);
        Optional<StageInstance> optional = storage.getStage();
        if (optional.isEmpty()) return;

        Stage stage = StageAPI.getStageRegistry().get(packet.stage());
        if (stage == null) return;

        StageInstance instance = optional.get();
        if (!instance.getNextBreakthroughs(player).contains(stage)) {
        }

        storage.breakthroughStage(stage);
    }
}
