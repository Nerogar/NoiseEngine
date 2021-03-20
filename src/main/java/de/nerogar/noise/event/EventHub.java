package de.nerogar.noise.event;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noiseInterface.event.IEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EventHub {

	private final List<IEventListener<?>>                    listeners;
	private final List<Class<? extends IEvent>>              eventClasses;
	private final Map<Class<? extends IEvent>, List<IEvent>> eventMap;

	private final Function<Class<? extends IEvent>, List<IEvent>> addNewEventListLambda = eventClass -> new ArrayList<>();

	public EventHub() {
		listeners = new ArrayList<>();
		eventClasses = new ArrayList<>();
		eventMap = new HashMap<>();
	}

	public void addEvent(IEvent event) {
		List<IEvent> events = eventMap.computeIfAbsent(event.getClass(), addNewEventListLambda);
		events.add(event);
	}

	public <T extends IEvent> void resetEvents() {
		for (List<IEvent> events : eventMap.values()) {
			events.clear();
		}
	}

	public <T extends IEvent> int addListener(Class<T> eventClass, IEventListener<T> listener) {
		listeners.add(listener);
		eventClasses.add(eventClass);

		return listeners.size() - 1;
	}

	public void removeListener(int listenerHandle) {
		listeners.set(listenerHandle, null);
		eventClasses.set(listenerHandle, null);
	}

	public void triggerListener(int listenerHandle) {
		IEventListener<IEvent> eventListener = (IEventListener<IEvent>) listeners.get(listenerHandle);
		Class<IEvent> eventClass = (Class<IEvent>) eventClasses.get(listenerHandle);

		List<IEvent> events = eventMap.get(eventClass);

		if (events != null) {
			int size = events.size();
			for (int i = 0; i < size; i++) {
				eventListener.trigger(events.get(i));
			}
		}
	}

}
