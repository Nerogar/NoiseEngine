package de.nerogar.noise.game;

import de.nerogar.noiseInterface.game.IComponent;
import de.nerogar.noiseInterface.game.IEntity;

public class AbstractComponent implements IComponent {

	private IEntity entity;

	@Override
	public void setEntity(IEntity entity) {
		this.entity = entity;
	}

	@Override
	public IEntity getEntity() {
		return entity;
	}
}
