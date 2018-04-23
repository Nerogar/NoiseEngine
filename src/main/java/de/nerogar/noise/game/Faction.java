package de.nerogar.noise.game;

import de.nerogar.noise.network.AggregateNetworkAdapter;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.network.Packet;
import de.nerogar.noise.util.Color;

import java.util.ArrayList;

public class Faction implements INetworkAdapter {

	/**
	 *
	 */
	private int   id;
	private Color color;

	private FactionSystemContainer systemContainer;
	private GameSystemContainer    gameSystemContainer;

	private AggregateNetworkAdapter networkAdapter;

	public Faction(int id, Color color) {
		if (Integer.bitCount(id) != 1) throw new RuntimeException("invalid faction id");

		this.id = id;
		this.color = color;

		networkAdapter = new AggregateNetworkAdapter();
	}

	public void setSystemContainer(FactionSystemContainer systemContainer, GameSystemContainer gameSystemContainer) {
		this.systemContainer = systemContainer;
		this.gameSystemContainer = gameSystemContainer;
	}

	@SuppressWarnings("unchecked")
	public <C extends LogicSystem> C getSystem(Class<C> systemClass) {
		return systemContainer.getSystem(systemClass);
	}

	@SuppressWarnings("unchecked")
	public <C extends LogicSystem> C getGameSystem(Class<C> systemClass) {
		return gameSystemContainer.getSystem(systemClass);
	}

	public int getID() {
		return id;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void send(Packet packet) {
		networkAdapter.send(packet);
	}

	@Override
	public ArrayList<Packet> getPackets(int channelID) {
		return networkAdapter.getPackets(channelID);
	}

	public void addNetworkAdapter(INetworkAdapter networkAdapter) {
		this.networkAdapter.addNetworkAdapter(networkAdapter);
	}
}
