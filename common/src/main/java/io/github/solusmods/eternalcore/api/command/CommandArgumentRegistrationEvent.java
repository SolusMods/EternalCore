package io.github.solusmods.eternalcore.api.command;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import io.github.solusmods.eternalcore.command.CommandArgumentRegistry;


public interface CommandArgumentRegistrationEvent {
    Event<CommandArgumentRegistrationEvent> EVENT = EventFactory.createLoop();

    void register(CommandArgumentRegistry registry);
}
