package de.nerogar.noise.game.systems;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.game.UpdateGameSystem;
import de.nerogar.noise.game.events.UpdateEvent;

public class EntityFactorySystem extends UpdateGameSystem {

	public EntityFactorySystem(EventManager eventManager) {
		super(eventManager);
	}

	private int updates = 100;

	@Override
	protected void onUpdate(UpdateEvent event) {
		if (updates > 0) {
			System.out.println("updated " + updates);
		}
		updates--;
	}

}
