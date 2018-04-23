package de.nerogar.noise.game;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.game.core.systems.GameObjectsSystem;
import de.nerogar.noise.network.INetworkAdapter;

public abstract class GameSystemContainer extends SystemContainer {

	public GameSystemContainer(EventManager eventManager, INetworkAdapter networkAdapter) {
		super(eventManager, networkAdapter);
	}

	@Override
	protected void addSystems() {
		addSystem(new GameObjectsSystem());
	}

	@Override
	public final String getName() {
		return "game";
	}

}
