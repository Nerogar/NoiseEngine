package de.nerogar.noise.oldGame.core.systems;

import de.nerogar.noiseInterface.event.IEventListener;
import de.nerogar.noise.oldGame.LogicSystem;
import de.nerogar.noise.oldGame.core.events.UpdateEvent;

public abstract class OnUpdateSystem extends LogicSystem {

	private IEventListener<UpdateEvent> updateListener;

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
