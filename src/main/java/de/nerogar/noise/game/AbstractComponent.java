package de.nerogar.noise.game;

import de.nerogar.noiseInterface.game.IComponent;

public class AbstractComponent implements IComponent {

	private long entity;

	@Override
	public void setEntity(long entity) {
		this.entity = entity;
	}

	@Override
	public long getEntity() {
		return entity;
	}

}
