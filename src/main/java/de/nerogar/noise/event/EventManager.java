package de.nerogar.noise.event;

import java.util.*;

public class EventManager {

	private final String name;

	private final List<EventManager> children;

	private final Map<Class<? extends IEvent>, DefaultEventManager<? extends IEvent>> defaultListenerMap;
	private final Map<EventListener<?>, Boolean>                                      isImmediateMap;

	private final Queue<IEvent> eventQueue;
	boolean triggered;

	public EventManager(String name) {
		this.name = name;

		children = new ArrayList<>();

		defaultListenerMap = new HashMap<>();
		isImmediateMap = new HashMap<>();

		eventQueue = new ArrayDeque<>();
	}

	@SuppressWarnings("unchecked")
	private <T extends IEvent> DefaultEventManager<T> getDefaultEventManager(Class<? extends IEvent> eventClass) {
		return (DefaultEventManager<T>) defaultListenerMap.computeIfAbsent(eventClass, ec -> new DefaultEventManager<T>());
	}

	public <T extends IEvent> void register(Class<T> eventClass, EventListener<T> eventListener) {
		register(eventClass, eventListener, false);
	}

	public <T extends IEvent> void registerImmediate(Class<T> eventClass, EventListener<T> eventListener) {
		register(eventClass, eventListener, true);
	}

	private <T extends IEvent> void register(Class<T> eventClass, EventListener<T> eventListener, boolean isImmediate) {
		DefaultEventManager<T> defaultEventManager = getDefaultEventManager(eventClass);
		defaultEventManager.register(eventListener);
		isImmediateMap.put(eventListener, isImmediate);
	}

	public <T extends IEvent> void unregister(Class<T> eventClass, EventListener<T> eventListener) {
		DefaultEventManager<T> defaultEventManager = getDefaultEventManager(eventClass);

		defaultEventManager.unregister(eventListener);

		isImmediateMap.remove(eventListener);
	}

	/**
	 * All events triggered in this EventManager will also be triggered in all children
	 */
	public void addTriggerChild(EventManager child) {
		children.add(child);
	}

	public void removeTriggerChild(EventManager child) {
		children.remove(child);
	}

	public <T extends IEvent> void trigger(T event) {
		for (int i = 0; i < children.size(); i++) {
			children.get(i).trigger(event);
		}

		// trigger immediate
		triggerSingle(event.getClass(), event, true);

		// trigger dispatched
		eventQueue.add(event);

		if (!triggered) {
			triggered = true;

			while (!eventQueue.isEmpty()) {
				IEvent singleEvent = eventQueue.poll();
				triggerSingle(singleEvent.getClass(), singleEvent, false);
			}

			triggered = false;
		}

	}

	@SuppressWarnings("unchecked")
	private <T extends IEvent> void triggerSingle(Class<? extends IEvent> eventClass, T event, boolean isImmediate) {

		DefaultEventManager<T> defaultEventManager = getDefaultEventManager(eventClass);

		for (EventListener<T> entry : defaultEventManager.listeners) {
			if (isImmediateMap.get(entry) == isImmediate) {
				entry.trigger(event);
			}
		}

		// trigger for all super types
		Class<?> superClass = eventClass.getSuperclass();
		if (superClass != null && IEvent.class.isAssignableFrom(superClass)) triggerSingle((Class<? extends IEvent>) superClass, event, isImmediate);

		for (Class<?> superInterface : eventClass.getInterfaces()) {
			if (IEvent.class.isAssignableFrom(superInterface)) triggerSingle((Class<? extends IEvent>) superInterface, event, isImmediate);
		}

	}

}
