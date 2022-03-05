package de.nerogar.noiseInterface.game;

import de.nerogar.noiseInterface.event.IEvent;

public interface IEventConsumer<T extends IEvent> {

	void triggerEvents(IEventTrigger<T> trigger);

}
