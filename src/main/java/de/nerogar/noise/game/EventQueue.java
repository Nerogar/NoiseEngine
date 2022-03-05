package de.nerogar.noise.game;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noiseInterface.game.*;

import java.util.ArrayList;
import java.util.List;

public class EventQueue<T extends IEvent> implements IEventProducer<T>, IEventConsumer<T> {

	private final List<T> events;

	public EventQueue() {
		this.events = new ArrayList<>();
	}

	@Override
	public void addEvent(T event) {
		events.add(event);
	}

	@Override
	public void triggerEvents(IEventTrigger<T> trigger) {
		int size = events.size();
		for (int i = 0; i < size; i++) {
			trigger.trigger(events.get(i));
		}
	}

	public void resetEvents() {
		events.clear();
	}

}
