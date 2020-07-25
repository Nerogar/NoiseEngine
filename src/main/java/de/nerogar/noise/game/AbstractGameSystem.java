package de.nerogar.noise.game;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noiseInterface.game.IGameSystem;

public class AbstractGameSystem implements IGameSystem {

	private EventManager        eventManager;
	private GameSystemContainer gameSystemContainer;

	public AbstractGameSystem(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public void setSystemContainer(GameSystemContainer gameSystemContainer) {
		this.gameSystemContainer = gameSystemContainer;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	protected GameSystemContainer getGameSystemContainer() {
		return gameSystemContainer;
	}
}
