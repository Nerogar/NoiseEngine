package de.nerogar.noise.game;

import de.nerogar.noise.event.EventHub;
import de.nerogar.noiseInterface.game.IGameSystem;

public class AbstractGameSystem implements IGameSystem {

	private GameSystemContainer gameSystemContainer;
	private EventHub eventHub;

	public AbstractGameSystem(EventHub eventHub) {
		this.eventHub = eventHub;
	}

	@Override
	public void setSystemContainer(GameSystemContainer gameSystemContainer) {
		this.gameSystemContainer = gameSystemContainer;
	}

	public EventHub getEventHub() {
		return eventHub;
	}

	protected GameSystemContainer getGameSystemContainer() {
		return gameSystemContainer;
	}
}
