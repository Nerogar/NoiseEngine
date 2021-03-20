package de.nerogar.noise.event;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noiseInterface.event.IEventListener;

import java.util.HashSet;

public class DefaultEventManager<E extends IEvent> {

	protected HashSet<IEventListener<E>> listeners;

	public DefaultEventManager() {
		listeners = new HashSet<>();
	}

	public void register(IEventListener<E> listener) {
		listeners.add(listener);
	}

	public void unregister(IEventListener<E> listener) {
		listeners.remove(listener);
	}

}
