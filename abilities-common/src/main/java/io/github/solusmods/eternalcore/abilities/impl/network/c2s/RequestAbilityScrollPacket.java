package io.github.solusmods.eternalcore.abilities.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import io.github.solusmods.eternalcore.abilities.api.Abilities;
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI;
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents;
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RequestAbilityScrollPacket(
        double delta,
        List<ResourceLocation> abilityList
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestAbilityScrollPacket> TYPE = new CustomPacketPayload.Type<>(EternalCoreAbilities.create("request_ability_scroll"));
    public static final StreamCodec<FriendlyByteBuf, RequestAbilityScrollPacket> STREAM_CODEC = CustomPacketPayload.codec(RequestAbilityScrollPacket::encode, RequestAbilityScrollPacket::new);

    public RequestAbilityScrollPacket(FriendlyByteBuf buf) {
        this(buf.readDouble(), validateList(buf.readList(FriendlyByteBuf::readResourceLocation)));
    }

    private static List<ResourceLocation> validateList(List<ResourceLocation> list) {
        int maxSize = 100;
        if (list.size() > maxSize) throw new IllegalArgumentException("Ability list exceeds maximum size of " + maxSize);
        return list;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.delta);
        buf.writeCollection(this.abilityList, FriendlyByteBuf::writeResourceLocation);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> {
            Player player = context.getPlayer();
            if (player == null) return;

            Abilities storage = AbilityAPI.getAbilitiesFrom(player);
            for (ResourceLocation skillId : abilityList) {
                storage.getAbility(skillId).ifPresent(abilityInstance -> {

                    Changeable<AbilityInstance> skillChangeable = Changeable.of(abilityInstance);
                    Changeable<Double> deltaChangeable = Changeable.of(delta);
                    if (AbilityEvents.ABILITY_SCROLL.invoker().scroll(skillChangeable, player, deltaChangeable).isFalse()) return;

                    AbilityInstance abilityInstance1 = skillChangeable.get();
                    if (abilityInstance1 == null || deltaChangeable.isEmpty()) return;
                    if (!abilityInstance1.canScroll(player)) return;
                    if (!abilityInstance1.canInteractAbility(player)) return;

                    abilityInstance1.onScroll(player, deltaChangeable.get(), 0);
                    storage.markDirty();
                });
            }
        });
    }

    public @NotNull Type<RequestAbilityScrollPacket> type() {
        return TYPE;
    }
}
