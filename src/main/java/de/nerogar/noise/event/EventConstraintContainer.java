package de.nerogar.noise.event;

public class EventConstraintContainer<C extends EventListenerConstraint> {

	private EventListener listener;
	private C constraint;

	public EventConstraintContainer(EventListener listener, C constraint) {
		this.listener = listener;
		this.constraint = constraint;
	}

	public EventListener getListener() {
		return listener;
	}

	public void setConstraint(C constraint) {
		this.constraint = constraint;
	}

	public C getConstraint() {
		return constraint;
	}


}
