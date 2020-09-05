package de.nerogar.noise.event;

import java.util.HashSet;

public class DefaultEventManager<E extends IEvent> {

	protected HashSet<EventListener<E>> listeners;

	public DefaultEventManager() {
		listeners = new HashSet<>();
	}

	public void register(EventListener<E> listener) {
		listeners.add(listener);
	}

	public void unregister(EventListener<E> listener) {
		listeners.remove(listener);
	}

}
