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

public record RequestAbilityTogglePacket(
        ResourceLocation abilityId
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestAbilityTogglePacket> TYPE = new CustomPacketPayload.Type<>(EternalCoreAbilities.create("request_ability_toggle"));
    public static final StreamCodec<FriendlyByteBuf, RequestAbilityTogglePacket> STREAM_CODEC = CustomPacketPayload.codec(RequestAbilityTogglePacket::encode, RequestAbilityTogglePacket::new);

    public RequestAbilityTogglePacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.abilityId);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> {
            Player player = context.getPlayer();
            if(player == null) return;
            Abilities storage = AbilityAPI.getAbilitiesFrom(player);
            storage.getAbility(abilityId).ifPresent(abilityInstance -> {
                Changeable<AbilityInstance> changeable = Changeable.of(abilityInstance);
                if (AbilityEvents.TOGGLE_ABILITY.invoker().toggleAbility(changeable, player).isFalse()) return;

                AbilityInstance skill = changeable.get();
                if (skill == null) return;
                if(!skill.canInteractAbility(player)) return;

                if(skill.isToggled()) {
                    skill.setToggled(false);
                    skill.onToggleOff(player);
                } else {
                    skill.setToggled(true);
                    skill.onToggleOn(player);
                }
                storage.markDirty();
            });
        });
    }

    public @NotNull Type<RequestAbilityTogglePacket> type() {
        return TYPE;
    }
}
