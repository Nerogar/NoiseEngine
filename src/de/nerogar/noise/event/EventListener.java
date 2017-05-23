package de.nerogar.noise.event;

public interface EventListener<T extends Event> {

	public void trigger(T event);

}
