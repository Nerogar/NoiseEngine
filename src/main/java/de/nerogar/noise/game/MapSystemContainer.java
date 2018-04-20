package de.nerogar.noise.game;

import de.nerogar.noise.game.core.systems.PositionLookupSystem;

public abstract class MapSystemContainer<T extends CoreMap> extends SystemContainer {

	private T map;

	public MapSystemContainer(T map) {
		super(map.getEventManager(), map.getNetworkAdapter());

		this.map = map;
	}

	public T getMap() {
		if (map == null) throw new NullPointerException();
		return map;
	}

	@Override
	protected void addSystems() {
		addSystem(new PositionLookupSystem());
	}

	@Override
	public final String getName() {
		return "map " + map.getId();
	}

}
