package de.nerogar.noise.game;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.game.events.UpdateEvent;

public abstract class UpdateGameSystem extends AbstractGameSystem {

	private EventListener<UpdateEvent> onUpdate;

	public UpdateGameSystem(EventManager eventManager) {
		super(eventManager);
		this.onUpdate = this::onUpdate;
		eventManager.register(UpdateEvent.class, this.onUpdate);
	}

	protected abstract void onUpdate(UpdateEvent event);

}
