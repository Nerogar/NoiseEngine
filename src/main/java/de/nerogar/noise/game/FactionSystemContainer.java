package de.nerogar.noise.game;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;

public abstract class FactionSystemContainer extends SystemContainer {

	private Faction faction;

	public FactionSystemContainer(EventManager eventManager, INetworkAdapter networkAdapter, Faction faction) {
		super(eventManager, networkAdapter);
		this.faction = faction;
	}

	public Faction getFaction() { return faction; }

	@Override
	protected void addSystems() {

	}

	@Override
	public final String getName() {
		return "faction " + faction.getID();
	}

}
