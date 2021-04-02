package de.nerogar.noise.event;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noiseInterface.event.IEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EventHub {

	private final List<Class<? extends IEvent>>              eventClasses;
	private final Map<Class<? extends IEvent>, List<IEvent>> eventMap;

	private final Function<Class<? extends IEvent>, List<IEvent>> addNewEventListLambda = eventClass -> new ArrayList<>();

	public EventHub() {
		eventClasses = new ArrayList<>();
		eventMap = new HashMap<>();
	}

	public void addEvent(IEvent event) {
		List<IEvent> events = eventMap.computeIfAbsent(event.getClass(), addNewEventListLambda);
		events.add(event);
	}

	public void resetEvents() {
		for (List<IEvent> events : eventMap.values()) {
			events.clear();
		}
	}

	public <T extends IEvent> void triggerListener(Class<T> eventClass, IEventListener<T> eventListener) {
		List<IEvent> events = eventMap.get(eventClass);

		if (events != null) {
			int size = events.size();
			for (int i = 0; i < size; i++) {
				eventListener.trigger((T) events.get(i));
			}
		}
	}

}
