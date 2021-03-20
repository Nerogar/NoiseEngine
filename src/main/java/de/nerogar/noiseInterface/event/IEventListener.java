package de.nerogar.noiseInterface.event;

public interface IEventListener<T extends IEvent> {

	void trigger(T event);

}
