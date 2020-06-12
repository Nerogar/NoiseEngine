package de.nerogar.noise.game;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noiseInterface.game.IGameSystem;
import de.nerogar.noiseInterface.game.IGameSystemContainer;

import java.util.HashMap;
import java.util.Map;

public class GameSystemContainer implements IGameSystemContainer {

	private final EventManager                                   eventManager;
	private final Map<Class<? extends IGameSystem>, IGameSystem> gameSystems;

	public GameSystemContainer(EventManager eventManager) {
		this.eventManager = eventManager;
		gameSystems = new HashMap<>();
	}

	public void addGameSystem(IGameSystem gameSystem) {
		gameSystems.put(gameSystem.getClass(), gameSystem);
		gameSystem.setData(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends IGameSystem> T getGameSystem(Class<T> gameSystemClass) {
		return (T) gameSystems.get(gameSystemClass);
	}

	public EventManager getEventManager() {
		return eventManager;
	}

}
