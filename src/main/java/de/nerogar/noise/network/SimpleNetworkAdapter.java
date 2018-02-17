package de.nerogar.noise.network;

import java.util.ArrayList;

public class SimpleNetworkAdapter implements INetworkAdapter {

	protected final int        id;
	protected final Connection connection;

	SimpleNetworkAdapter(Connection connection, int id) {
		this.connection = connection;
		this.id = id;
	}

	@Override
	public void send(Packet packet) {
		connection.send(id, packet);
	}

	@Override
	public ArrayList<Packet> getPackets(int channelID) {
		return connection.getPackets(id, channelID);
	}
}
