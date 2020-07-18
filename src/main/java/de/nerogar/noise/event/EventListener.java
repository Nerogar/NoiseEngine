package de.nerogar.noise.event;

public interface EventListener<T extends IEvent> {

	public void trigger(T event);

}
