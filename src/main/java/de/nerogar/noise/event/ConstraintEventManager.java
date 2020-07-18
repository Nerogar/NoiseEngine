package de.nerogar.noise.event;

import java.util.List;

/**
 * Manager for a single event type.
 *
 * @param <E> the event type
 * @param <C> the constraint to optimize for
 */
public interface ConstraintEventManager<E extends IEvent, C extends EventListenerConstraint> {

	public void register(EventListener<E> listener, C constraint);

	public void unregister(EventListener<E> listener);

	public void updateConstraint(EventListener<E> listener, C constraint);

	public List<EventConstraintContainer<EventListenerPositionConstraint>> getFiltered(E event);

	public Class<C> getEventListenerConstraintClass();

}
