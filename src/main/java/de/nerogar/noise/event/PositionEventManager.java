package de.nerogar.noise.event;

import de.nerogar.noise.util.SpaceOctree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionEventManager<E extends Event & EventPositionConstraint> implements ConstraintEventManager<E, EventListenerPositionConstraint> {

	private SpaceOctree<EventConstraintContainer<EventListenerPositionConstraint>>           octree;
	private Map<EventListener<E>, EventConstraintContainer<EventListenerPositionConstraint>> listenerMap;

	public PositionEventManager() {
		octree = new SpaceOctree<>(c -> c.getConstraint().getBounding());
		listenerMap = new HashMap<>();
	}

	@Override
	public void register(EventListener<E> listener, EventListenerPositionConstraint constraint) {
		EventConstraintContainer<EventListenerPositionConstraint> container = new EventConstraintContainer<>(listener, constraint);

		octree.add(container);
		listenerMap.put(listener, container);
	}

	@Override
	public void unregister(EventListener<E> listener) {
		EventConstraintContainer<EventListenerPositionConstraint> container = listenerMap.get(listener);

		octree.remove(container);
		listenerMap.remove(listener);
	}

	@Override
	public void updateConstraint(EventListener<E> listener, EventListenerPositionConstraint constraint) {
		EventConstraintContainer<EventListenerPositionConstraint> container = listenerMap.get(listener);

		container.setConstraint(constraint);
		octree.update(container);
	}

	@Override
	public List<EventConstraintContainer<EventListenerPositionConstraint>> getFiltered(E event) {
		return octree.getFiltered(new ArrayList<>(), event.getBounding());
	}

	@Override
	public Class<EventListenerPositionConstraint> getEventListenerConstraintClass() {
		return EventListenerPositionConstraint.class;
	}

}
