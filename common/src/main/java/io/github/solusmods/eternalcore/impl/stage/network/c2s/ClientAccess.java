package io.github.solusmods.eternalcore.impl.stage.network.c2s;


import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import io.github.solusmods.eternalcore.api.stage.StageAPI;
import io.github.solusmods.eternalcore.api.stage.Stages;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientAccess {


    static void handle(RequestStageBreakthroughPacket packet, Player player) {
        if (player == null) return;

        Stages storage = StageAPI.getStageFrom(player);
        Optional<AbstractStage> optional = storage.getStage();
        if (optional.isEmpty()) return;

        AbstractStage stage = StageAPI.getStageRegistry().get(packet.stage());
        if (stage == null) return;

        storage.breakthroughStage(stage);
    }
}
