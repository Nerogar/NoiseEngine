package de.nerogar.noise.event;

import de.nerogar.noise.game.EventQueue;
import de.nerogar.noiseInterface.event.IEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EventHub {

	private final Map<Class<? extends IEvent>, EventQueue<IEvent>> eventMap;

	private final Function<Class<? extends IEvent>, EventQueue<IEvent>> addNewEventQueueLambda = eventClass -> new EventQueue<>();

	public EventHub() {
		eventMap = new HashMap<>();
	}

	public <T extends IEvent> EventQueue<T> getQueue(Class<T> eventClass) {
		return (EventQueue<T>) eventMap.computeIfAbsent(eventClass, addNewEventQueueLambda);
	}

	public void resetEvents() {
		for (EventQueue<IEvent> eventQueue : eventMap.values()) {
			eventQueue.resetEvents();
		}
	}

}
