package io.github.solusmods.eternalcore.stage.api

import dev.architectury.platform.Platform
import dev.architectury.registry.registries.Registrar
import dev.architectury.utils.Env
import io.github.solusmods.eternalcore.stage.impl.StageRegistry
import io.github.solusmods.eternalcore.stage.impl.StageStorage
import io.github.solusmods.eternalcore.stage.impl.network.InternalStagePacketActions
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import java.util.Optional

object StageAPI {
    /**
     * This Method returns the [Stage] Registry.
     * It can be used to load [Stage]s from the Registry.
     */
    @JvmField
    val stageRegistry: Registrar<Stage> = StageRegistry.STAGES

    /**
     * This Method returns the Registry Key of the [StageRegistry].
     * It can be used to create [dev.architectury.registry.registries.DeferredRegister] instances
     */
    @JvmField
    val stageRegistryKey: ResourceKey<Registry<Stage>> = StageRegistry.KEY

    /**
     * Can be used to load the [StageStorage] from an [LivingEntity].
     */
    @JvmStatic
    fun getStageFrom(entity: LivingEntity): Stages? {
        return entity.getStorage(StageStorage.key)
    }

    @JvmStatic
    fun getStorageOptional(entity: LivingEntity): Optional<StageStorage> {
        return entity.getStorageOptional(StageStorage.key)
    }

    /**
     * Can be used to load the [StageStorage] from an [LivingEntity].
     */
    @JvmStatic
    fun getReachedStagesFrom(entity: LivingEntity): IReachedStages? {
        return entity.getStorage(StageStorage.key)
    }

    /**
     * Send [InternalStagePacketActions.sendStageBreakthroughPacket] with a DistExecutor on client side.
     * Used when player Breakthrough into a stage.
     *
     * @see InternalStagePacketActions.sendStageBreakthroughPacket
     */
    @JvmStatic
    fun stageBreakthroughPacket(location: ResourceLocation?) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            InternalStagePacketActions.sendStageBreakthroughPacket(location)
        }
    }
}
