package de.nerogar.noise.event;

import java.util.*;

public class EventManager {

	/*
	local events
	timed events
	counted events (max trigger count)
	 */

	private Map<Class<? extends Event>, DefaultEventManager<? extends Event>>                                       defaultListenerMap;
	private Map<Class<? extends Event>, DefaultEventManager<? extends Event>>                                       defaultConstraintListenerMap;
	private Map<Class<? extends Event>, ConstraintEventManager<? extends Event, ? extends EventListenerConstraint>> constraintListenerMap;

	private Queue<Event> eventQueue;
	boolean triggered;

	public EventManager() {
		defaultListenerMap = new HashMap<>();
		defaultConstraintListenerMap = new HashMap<>();
		constraintListenerMap = new HashMap<>();
		eventQueue = new ArrayDeque<>();
	}

	@SuppressWarnings("unchecked")
	private <T extends Event> DefaultEventManager<T> getDefaultEventManager(Class<? extends Event> eventClass) {
		return (DefaultEventManager<T>) defaultListenerMap.computeIfAbsent(eventClass, ec -> new DefaultEventManager<T>());
	}

	public <T extends Event> void register(Class<T> eventClass, EventListener<T> eventListener) {
		DefaultEventManager<T> defaultEventManager = getDefaultEventManager(eventClass);
		defaultEventManager.register(eventListener);
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> void register(Class<T> eventClass, EventListener<T> eventListener, EventListenerConstraint... eventListenerConstraints) {

		DefaultEventManager<T> defaultEventManager = getDefaultEventManager(eventClass);
		ConstraintEventManager constraintEventManager = constraintListenerMap.get(eventClass);
		DefaultEventManager<T> defaultConstraintEventManager = (DefaultEventManager<T>) defaultConstraintListenerMap.get(eventClass);

		if (constraintEventManager != null) {
			EventListenerConstraint specialConstraint = null;

			for (EventListenerConstraint eventListenerConstraint : eventListenerConstraints) {
				if (constraintEventManager.getEventListenerConstraintClass().isAssignableFrom(eventListener.getClass())) {
					specialConstraint = eventListenerConstraint;
					break;
				}
			}

			if (specialConstraint != null) {
				constraintEventManager.register(eventListener, specialConstraint);
				defaultConstraintEventManager.register(eventListener, eventListenerConstraints);
			} else {
				defaultEventManager.register(eventListener, eventListenerConstraints);
			}

		} else {
			defaultEventManager.register(eventListener, eventListenerConstraints);
		}

	}

	@SuppressWarnings("unchecked")
	public <T extends Event> void unregister(Class<T> eventClass, EventListener<T> eventListener) {
		DefaultEventManager<T> defaultEventManager = getDefaultEventManager(eventClass);
		ConstraintEventManager constraintEventManager = constraintListenerMap.get(eventClass);
		DefaultEventManager<T> defaultConstraintEventManager = (DefaultEventManager<T>) defaultConstraintListenerMap.get(eventClass);

		if (constraintEventManager != null) {
			constraintEventManager.unregister(eventListener);
			defaultConstraintEventManager.unregister(eventListener);
		}

		defaultEventManager.unregister(eventListener);

	}

	@SuppressWarnings("unchecked")
	public <T extends Event> void updateConstraints(Class<T> eventClass, EventListener<T> eventListener, EventListenerConstraint... eventListenerConstraints) {
		DefaultEventManager<T> defaultEventManager = getDefaultEventManager(eventClass);
		ConstraintEventManager constraintEventManager = constraintListenerMap.get(eventClass);
		DefaultEventManager<T> defaultConstraintEventManager = (DefaultEventManager<T>) defaultConstraintListenerMap.get(eventClass);

		if (constraintEventManager != null) {
			EventListenerConstraint specialConstraint = null;

			for (EventListenerConstraint eventListenerConstraint : eventListenerConstraints) {
				if (constraintEventManager.getEventListenerConstraintClass().isAssignableFrom(eventListener.getClass())) {
					specialConstraint = eventListenerConstraint;
					break;
				}
			}

			if (specialConstraint != null) {
				constraintEventManager.updateConstraint(eventListener, specialConstraint);
			}

			defaultConstraintEventManager.updateConstraints(eventListener, eventListenerConstraints);
		}

		defaultEventManager.updateConstraints(eventListener, eventListenerConstraints);

	}

	@SuppressWarnings("unchecked")
	public <T extends Event> void trigger(T event) {
		eventQueue.add(event);

		if (!triggered) {
			triggered = true;

			while (!eventQueue.isEmpty()) {
				Event singleEvent = eventQueue.poll();
				triggerSingle(singleEvent.getClass(), singleEvent);
			}

			triggered = false;
		}

	}

	@SuppressWarnings("unchecked")
	private <T extends Event> void triggerSingle(Class<? extends Event> eventClass, T event) {

		DefaultEventManager<T> defaultEventManager = getDefaultEventManager(eventClass);

		// if the constraintEventManager is not yet created, create constraintEventManager and register events with constraints
		if (event.getSpecialConstraintClass() != null && !constraintListenerMap.containsKey(eventClass)) {

			ConstraintEventManager constraintEventManager = event.getNewEventManager();
			DefaultEventManager<T> defaultConstraintEventManager = new DefaultEventManager<>();

			Iterator<Map.Entry<EventListener<T>, List<EventListenerConstraint>>> iterator = defaultEventManager.listenerMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<EventListener<T>, List<EventListenerConstraint>> entry = iterator.next();

				EventListenerConstraint specialConstraint = null;

				if (entry.getValue() != null) {
					for (EventListenerConstraint constraint : entry.getValue()) {
						if (constraintEventManager.getEventListenerConstraintClass().isAssignableFrom(constraint.getClass())) {
							specialConstraint = constraint;
							break;
						}
					}
				}

				if (specialConstraint != null) {
					iterator.remove();
					defaultConstraintEventManager.register(entry.getKey(), entry.getValue());
					constraintEventManager.register(entry.getKey(), specialConstraint);
				}

			}

			constraintListenerMap.put(eventClass, constraintEventManager);
			defaultConstraintListenerMap.put(eventClass, defaultConstraintEventManager);

		}

		ConstraintEventManager constraintEventManager = constraintListenerMap.get(eventClass);

		if (constraintEventManager != null) {

			DefaultEventManager<T> defaultConstraintEventManager = (DefaultEventManager<T>) defaultConstraintListenerMap.get(eventClass);

			Set<EventConstraintContainer> listener = constraintEventManager.getFiltered(event);

			for (EventConstraintContainer l : listener) {
				if (defaultConstraintEventManager.isValid(event, l.getListener())) {
					l.getListener().trigger(event);
				}
			}

		}

		defaultEventManager.triggerAll(event);

		// trigger for all super types
		Class<?> superClass = eventClass.getSuperclass();
		if (superClass != null && Event.class.isAssignableFrom(superClass)) triggerSingle((Class<? extends Event>) superClass, event);

		for (Class<?> superInterface : eventClass.getInterfaces()) {
			if (Event.class.isAssignableFrom(superInterface)) triggerSingle((Class<? extends Event>) superInterface, event);
		}

	}

}
