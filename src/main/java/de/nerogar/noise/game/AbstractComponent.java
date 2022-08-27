package de.nerogar.noise.game;

import de.nerogar.noiseInterface.game.IComponent;

public class AbstractComponent implements IComponent {

	private int entityId;

	@Override
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	@Override
	public int getEntityId() {
		return entityId;
	}

}
