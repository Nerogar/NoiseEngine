package de.nerogar.noise.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEventManager<E extends Event> {

	protected Map<EventListener<E>, List<EventListenerConstraint>> listenerMap;

	public DefaultEventManager() {
		listenerMap = new HashMap<>();
	}

	public void register(EventListener<E> listener) {
		listenerMap.put(listener, null);
	}

	public void register(EventListener<E> listener, EventListenerConstraint... constraints) {
		if (constraints == null || constraints.length == 0) {
			register(listener);
		} else {
			List<EventListenerConstraint> constraintsList = new ArrayList<>(constraints.length);
			for (EventListenerConstraint constraint : constraints) {
				constraintsList.add(constraint);
			}
			listenerMap.put(listener, constraintsList);
		}
	}

	public void register(EventListener<E> listener, List<EventListenerConstraint> constraints) {
		if (constraints == null || constraints.isEmpty()) {
			register(listener);
		} else {
			List<EventListenerConstraint> constraintsList = new ArrayList<>(constraints);
			listenerMap.put(listener, constraintsList);
		}
	}

	public void unregister(EventListener<E> listener) {
		listenerMap.remove(listener);
	}

	public void updateConstraints(EventListener<E> listener, EventListenerConstraint... constraints) {
		if (!listenerMap.containsKey(listener)) return;

		for (EventListenerConstraint constraint : constraints) {
			updateConstraint(listener, constraint);
		}

	}

	public void updateConstraint(EventListener<E> listener, EventListenerConstraint constraint) {
		if (!listenerMap.containsKey(listener)) return;

		List<EventListenerConstraint> constraints = listenerMap.computeIfAbsent(listener, k -> new ArrayList<>());

		for (int i = 0; i < constraints.size(); i++) {
			if (constraints.get(i).getClass() == constraint.getClass()) {
				constraints.set(i, constraint);
				return;
			}
		}

		// if no compatible constraint was found, add at the end of the list
		constraints.add(constraint);
	}

	public boolean isValid(E event, EventListener<E> listener) {
		for (EventListenerConstraint constraint : listenerMap.get(listener)) {
			if (!constraint.isValid(event)) return false;
		}

		return true;
	}

	public void triggerAll(E event) {
		entryLoop:
		for (Map.Entry<EventListener<E>, List<EventListenerConstraint>> entry : listenerMap.entrySet()) {
			if (entry.getValue() != null) {
				for (EventListenerConstraint constraint : entry.getValue()) {
					if (!constraint.isValid(event)) continue entryLoop;
				}
			}

			entry.getKey().trigger(event);
		}
	}

}
