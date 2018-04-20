package de.nerogar.noise.game.core.systems;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.game.LogicSystem;
import de.nerogar.noise.game.core.events.UpdateEvent;

public abstract class OnUpdateSystem extends LogicSystem {

	private EventListener<UpdateEvent> updateListener;

	@Override
	public void init() {
		updateListener = this::updateListenerFunction;
		getEventManager().register(UpdateEvent.class, updateListener);
	}

	protected abstract void updateListenerFunction(UpdateEvent event);

	@Override
	public void cleanup() {
		getEventManager().unregister(UpdateEvent.class, updateListener);
	}

}
