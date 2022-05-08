package de.nerogar.noise.game.events;

import de.nerogar.noiseInterface.event.IEvent;

public record UpdateEvent(float delta, double runtime) implements IEvent {

}
