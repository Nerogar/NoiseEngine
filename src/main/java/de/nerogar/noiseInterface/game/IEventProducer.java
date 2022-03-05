package de.nerogar.noiseInterface.game;

import de.nerogar.noiseInterface.event.IEvent;

public interface IEventProducer<T extends IEvent> {

	void addEvent(T event);

}
