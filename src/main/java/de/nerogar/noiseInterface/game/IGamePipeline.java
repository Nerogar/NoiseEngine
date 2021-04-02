package de.nerogar.noiseInterface.game;

import de.nerogar.noise.event.EventHub;
import de.nerogar.noiseInterface.event.IEvent;

public interface IGamePipeline<T> {

	void register(IGameSystem system);

	void trigger(T t);

	IEventTrigger getEventTrigger(Class<? extends IEvent> eventClass);

}
