package de.nerogar.noise.event;

import java.util.*;

public class EventManager {

	/*
	local events
	timed events
	counted events (max trigger count)
	 */

	private String name;

	private List<EventManager> children;

	private Map<Class<? extends Event>, DefaultEventManager<? extends Event>>                                       defaultListenerMap;
	private Map<Class<? extends Event>, DefaultEventManager<? extends Event>>                                       defaultConstraintListenerMap;
	private Map<Class<? extends Event>, ConstraintEventManager<? extends Event, ? extends EventListenerConstraint>> constraintListenerMap;

	private Map<EventListener<?>, Boolean> isImmediateMap;

	private Queue<Event> eventQueue;
	boolean triggered;

	public EventManager(String name) {
		this.name = name;

		children = new ArrayList<>();

		defaultListenerMap = new HashMap<>();
		defaultConstraintListenerMap = new HashMap<>();
		constraintListenerMap = new HashMap<>();

		isImmediateMap = new HashMap<>();

		eventQueue = new ArrayDeque<>();
	}

	@SuppressWarnings("unchecked")
	private <T extends Event> DefaultEventManager<T> getDefaultEventManager(Class<? extends Event> eventClass) {
		return (DefaultEventManager<T>) defaultListenerMap.computeIfAbsent(eventClass, ec -> new DefaultEventManager<T>());
	}

	public <T extends Event> void register(Class<T> eventClass, EventListener<T> eventListener) {
		register(eventClass, eventListener, false);
	}

	public <T extends Event> void registerImmediate(Class<T> eventClass, EventListener<T> eventListener) {
		register(eventClass, eventListener, true);
	}

	public <T extends Event> void register(Class<T> eventClass, EventListener<T> eventListener, EventListenerConstraint... eventListenerConstraints) {
		register(eventClass, eventListener, false, eventListenerConstraints);
	}

	public <T extends Event> void registerImmediate(Class<T> eventClass, EventListener<T> eventListener, EventListenerConstraint... eventListenerConstraints) {
		register(eventClass, eventListener, true, eventListenerConstraints);
	}

	private <T extends Event> void register(Class<T> eventClass, EventListener<T> eventListener, boolean isImmediate) {
		DefaultEventManager<T> defaultEventManager = getDefaultEventManager(eventClass);
		defaultEventManager.register(eventListener);
		isImmediateMap.put(eventListener, isImmediate);
	}

	@SuppressWarnings("unchecked")
	private <T extends Event> void register(Class<T> eventClass, EventListener<T> eventListener, boolean isImmediate, EventListenerConstraint... eventListenerConstraints) {

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

		isImmediateMap.put(eventListener, isImmediate);

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

		isImmediateMap.remove(eventListener);
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

	/**
	 * All events triggered in this EventManager will also be triggered in all children
	 */
	public void addTriggerChild(EventManager child){
		children.add(child);
	}

	public void removeTriggerChild(EventManager child){
		children.remove(child);
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> void trigger(T event) {
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
				Event singleEvent = eventQueue.poll();
				triggerSingle(singleEvent.getClass(), singleEvent, false);
			}

			triggered = false;
		}

	}

	@SuppressWarnings("unchecked")
	private <T extends Event> void triggerSingle(Class<? extends Event> eventClass, T event, boolean isImmediate) {

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

		// trigger special constraint listeners
		ConstraintEventManager constraintEventManager = constraintListenerMap.get(eventClass);
		if (constraintEventManager != null) {

			DefaultEventManager<T> defaultConstraintEventManager = (DefaultEventManager<T>) defaultConstraintListenerMap.get(eventClass);

			List<EventConstraintContainer> listener = constraintEventManager.getFiltered(event);

			for (EventConstraintContainer l : listener) {
				if (defaultConstraintEventManager.isValid(event, l.getListener()) && isImmediateMap.get(l.getListener()) == isImmediate) {
					l.getListener().trigger(event);
				}
			}

		}

		// trigger default listeners
		listenerLoop:
		for (Map.Entry<EventListener<T>, List<EventListenerConstraint>> entry : defaultEventManager.listenerMap.entrySet()) {
			if (isImmediateMap.get(entry.getKey()) != isImmediate) {
				continue;
			}

			if (entry.getValue() != null) {
				for (EventListenerConstraint constraint : entry.getValue()) {
					if (!constraint.isValid(event)) continue listenerLoop;
				}
			}

			entry.getKey().trigger(event);
		}

		// trigger for all super types
		Class<?> superClass = eventClass.getSuperclass();
		if (superClass != null && Event.class.isAssignableFrom(superClass)) triggerSingle((Class<? extends Event>) superClass, event, isImmediate);

		for (Class<?> superInterface : eventClass.getInterfaces()) {
			if (Event.class.isAssignableFrom(superInterface)) triggerSingle((Class<? extends Event>) superInterface, event, isImmediate);
		}

	}

}
