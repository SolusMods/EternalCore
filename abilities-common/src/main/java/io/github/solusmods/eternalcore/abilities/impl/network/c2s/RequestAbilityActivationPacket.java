package io.github.solusmods.eternalcore.abilities.impl.network.c2s;

import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import io.github.solusmods.eternalcore.abilities.EternalCoreAbilities;
import io.github.solusmods.eternalcore.abilities.api.Abilities;
import io.github.solusmods.eternalcore.abilities.api.AbilityAPI;
import io.github.solusmods.eternalcore.abilities.api.AbilityEvents;
import io.github.solusmods.eternalcore.abilities.api.AbilityInstance;
import io.github.solusmods.eternalcore.abilities.impl.AbilityStorage;
import io.github.solusmods.eternalcore.abilities.impl.TickingAbility;
import io.github.solusmods.eternalcore.network.api.util.Changeable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record RequestAbilityActivationPacket(int keyNumber,
                                             ResourceLocation abilityId,
                                             int mode) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RequestAbilityActivationPacket> TYPE = new CustomPacketPayload.Type<>(EternalCoreAbilities.create("request_ability_activation"));
    public static final StreamCodec<FriendlyByteBuf, RequestAbilityActivationPacket> STREAM_CODEC = CustomPacketPayload.codec(RequestAbilityActivationPacket::encode, RequestAbilityActivationPacket::new);

    public RequestAbilityActivationPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readResourceLocation(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.keyNumber);
        buf.writeResourceLocation(this.abilityId);
        buf.writeInt(this.mode);
    }

    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnvironment() != Env.SERVER) return;
        context.queue(() -> {
            Player player = context.getPlayer();
            if(player == null) return;
            Abilities storage = AbilityAPI.getAbilitiesFrom(player);
            storage.getAbility(abilityId).ifPresent(instance -> {
                Changeable<AbilityInstance> changeable = Changeable.of(instance);
                if (AbilityEvents.ACTIVATE_ABILITY.invoker().activateAbility(changeable, player, keyNumber, mode).isFalse()) return;

                AbilityInstance abilityInstance = changeable.get();
                if (abilityInstance == null) return;
                if(!abilityInstance.canInteractAbility(player)) return;

                if (mode < 0 || mode >= abilityInstance.getModes()) return;
                if (abilityInstance.onCoolDown(mode) && !abilityInstance.canIgnoreCoolDown(player, mode)) return;

                abilityInstance.onPressed(player, keyNumber, mode);
                abilityInstance.addHeldAttributeModifiers(player, mode);
                AbilityStorage.tickingAbilities.put(player.getUUID(), new TickingAbility(abilityInstance.getAbility(), mode));
                storage.markDirty();
            });
        });
    }

    public @NotNull Type<RequestAbilityActivationPacket> type() {
        return TYPE;
    }
}
