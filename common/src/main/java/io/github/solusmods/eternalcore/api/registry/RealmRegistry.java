package io.github.solusmods.eternalcore.api.registry;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.solusmods.eternalcore.EternalCore;
import io.github.solusmods.eternalcore.api.realm.AbstractRealm;
import io.github.solusmods.eternalcore.api.realm.RealmAPI;
import io.github.solusmods.eternalcore.api.spiritual_root.AbstractSpiritualRoot;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import static io.github.solusmods.eternalcore.EternalCore.REGISTRIES;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RealmRegistry {

    private static final ResourceLocation registryId = EternalCore.create("realms");

    /**
     * The Realm Registry, which contains all registered realms.
     * This registry is used to manage and access different realms in the game.
     */
    public static final Registrar<AbstractRealm> REALMS = REGISTRIES.get().<AbstractRealm>builder(registryId)
            .syncToClients().build();


    public static final ResourceKey<Registry<AbstractRealm>> KEY = (ResourceKey<Registry<AbstractRealm>>) REALMS.key();

    public static Registrar<AbstractRealm> getRealmRegistry() {
        return REALMS;
    }

    public static ResourceKey<Registry<AbstractRealm>> getRegistryKey() {
        return KEY;
    }

    public static RegistrySupplier<AbstractRealm> getRegistrySupplier(AbstractRealm abstractRealm) {
        return getRealmRegistry().delegate(getRealmRegistry().getId(abstractRealm));
    }

    public static void init() {
        PlayerEvent.PLAYER_RESPAWN.register((newPlayer, conqueredEnd, removalReason) -> {
            Optional<AbstractRealm> optional = RealmAPI.getRealmFrom(newPlayer).getRealm();
            if (optional.isEmpty()) return;

            AbstractRealm instance = optional.get();
            if (!conqueredEnd) {
                instance.addAttributeModifiers(newPlayer, 0);
            }
        });
    }
}
