package io.github.solusmods.eternalcore.neoforge;

import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.qi_energy.ElementType;
import io.github.solusmods.eternalcore.api.qi_energy.QiEnergyAPI;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.realm.RealmAPI;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import io.github.solusmods.eternalcore.api.spiritual_root.SpiritualRootAPI;
import io.github.solusmods.eternalcore.api.stage.AbstractStage;
import io.github.solusmods.eternalcore.api.stage.StageAPI;
import io.github.solusmods.eternalcore.impl.storage.StorageManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(EternalCore.MOD_ID)
public class EternalCoreNeoForge {

    public static final DeferredRegister<AbstractRealm> REALMS = DeferredRegister.create(RealmAPI.getRealmRegistryKey(), EternalCore.MOD_ID);
    public static final DeferredRegister<AbstractStage> STAGES = DeferredRegister.create(StageAPI.getStageRegistryKey(), EternalCore.MOD_ID);
    public static final DeferredRegister<AbstractSpiritualRoot> SPIRITUAL_ROOTS = DeferredRegister.create(SpiritualRootAPI.getSpiritualRootRegistryKey(), EternalCore.MOD_ID);
    public static final DeferredRegister<ElementType> ELEMENT_TYPES = DeferredRegister.create(QiEnergyAPI.getElementRegistryKey(), EternalCore.MOD_ID);
    public EternalCoreNeoForge(IEventBus modBus, ModContainer modContainer) {

        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfigsImpl.SPEC);
        var neoEventBus = NeoForge.EVENT_BUS;
        neoEventBus.addListener(EternalCoreNeoForge::onPlayerStartTrackingChunk);
        neoEventBus.addListener(EternalCoreNeoForge::onPlayerStartTrackingEntity);
        REALMS.register(modBus);
        STAGES.register(modBus);
        SPIRITUAL_ROOTS.register(modBus);
        ELEMENT_TYPES.register(modBus);
        EternalCore.init();
    }

    private static void onPlayerStartTrackingEntity(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            StorageManager.syncTarget(event.getTarget(), player);
        }
    }

    private static void onPlayerStartTrackingChunk(ChunkWatchEvent.Sent e) {
        StorageManager.syncTarget(e.getChunk(), e.getPlayer());
    }
}
