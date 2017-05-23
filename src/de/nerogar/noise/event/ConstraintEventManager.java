package de.nerogar.noise.event;

import java.util.Set;

/**
 * Manager for a single event type.
 *
 * @param <E> the event type
 * @param <C> the constraint to optimize for
 */
public interface ConstraintEventManager<E extends Event, C extends EventListenerConstraint> {

	public void register(EventListener<E> listener, C constraint);

	public void unregister(EventListener<E> listener);

	public void updateConstraint(EventListener<E> listener, C constraint);

	public Set<EventConstraintContainer<C>> getFiltered(E event);

	public Class<C> getEventListenerConstraintClass();

}
