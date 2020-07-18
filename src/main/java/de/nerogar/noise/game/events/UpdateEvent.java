package de.nerogar.noise.game.events;

import de.nerogar.noise.event.IEvent;

public class UpdateEvent implements IEvent {

	public final float  delta;
	public final double runtime;

	public UpdateEvent(float delta, double runtime) {

		this.delta = delta;
		this.runtime = runtime;
	}
}
