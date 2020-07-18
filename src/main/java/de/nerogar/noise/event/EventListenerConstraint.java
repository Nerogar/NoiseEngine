package de.nerogar.noise.event;

public interface EventListenerConstraint<T extends EventConstraint> {

	public boolean isValidGeneric(T event);

	public default boolean getDefault() {
		return false;
	}

	public Class<T> getEventConstraintClass();

	public default boolean isValid(IEvent event) {
		Class<T> eventConstraintClass = getEventConstraintClass();
		if (eventConstraintClass.isAssignableFrom(event.getClass())) {
			return isValidGeneric(eventConstraintClass.cast(event));
		} else {
			return getDefault();
		}
	}

}
