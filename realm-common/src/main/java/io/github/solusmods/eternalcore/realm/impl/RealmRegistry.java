package io.github.solusmods.eternalcore.realm.impl;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import io.github.solusmods.eternalcore.realm.EternalCoreRealm;
import io.github.solusmods.eternalcore.realm.ModuleConstants;
import io.github.solusmods.eternalcore.realm.api.Realm;
import io.github.solusmods.eternalcore.realm.api.RealmAPI;
import io.github.solusmods.eternalcore.realm.api.RealmInstance;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
public class RealmRegistry {

    private static final ResourceLocation registryId = EternalCoreRealm.create("realms");
    public static final Registrar<Realm> REALMS = RegistrarManager.get(ModuleConstants.MOD_ID).<Realm>builder(registryId)
            .syncToClients().build();
    public static final ResourceKey<Registry<Realm>> KEY = (ResourceKey<Registry<Realm>>) REALMS.key();


    public static void init() {
        PlayerEvent.PLAYER_RESPAWN.register((newPlayer, conqueredEnd, removalReason) -> {
            Optional<RealmInstance> optional = RealmAPI.getRealmFrom(newPlayer).getRealm();
            if (optional.isEmpty()) return;

            RealmInstance instance = optional.get();
            if (!conqueredEnd) {
                instance.addAttributeModifiers(newPlayer, 0);
            }
        });
    }
}
