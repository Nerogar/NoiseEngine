package de.nerogar.noise.event;

public interface EventListener<T extends IEvent> {

	void trigger(T event);

}
