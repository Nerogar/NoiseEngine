package de.nerogar.noise.oldGame.core.systems;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.oldGame.LogicSystem;
import de.nerogar.noise.oldGame.core.events.UpdateEvent;

public abstract class OnUpdateSystem extends LogicSystem {

	private EventListener<UpdateEvent> updateListener;

	@Override
	public void init() {
		updateListener = this::onUpdate;
		getEventManager().register(UpdateEvent.class, updateListener);
	}

	protected abstract void onUpdate(UpdateEvent event);

	@Override
	public void cleanup() {
		getEventManager().unregister(UpdateEvent.class, updateListener);
	}

}
