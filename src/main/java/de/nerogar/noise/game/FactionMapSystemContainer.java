package de.nerogar.noise.game;

public abstract class FactionMapSystemContainer<T extends CoreMap> extends SystemContainer {

	private Faction faction;
	private T       map;
	private int     mapId;

	public FactionMapSystemContainer(T map, Faction faction, int mapId) {
		super(map.getEventManager(), map.getNetworkAdapter());
		this.map = map;
		this.faction = faction;
		this.mapId = mapId;
	}

	public Faction getFaction() { return faction; }

	public T getMap() {
		if (map == null) throw new NullPointerException();
		return map;
	}

	@Override
	protected void addSystems() {

	}

	@Override
	public final String getName() {
		return "faction " + faction.getID() + ", map " + mapId;
	}

}
