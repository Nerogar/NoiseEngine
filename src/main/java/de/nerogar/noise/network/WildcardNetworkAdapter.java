package de.nerogar.noise.network;

import java.util.ArrayList;

public class WildcardNetworkAdapter implements INetworkAdapter {

	protected final Connection connection;

	WildcardNetworkAdapter(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void send(Packet packet) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayList<Packet> getPackets(int channelID) {
		return connection.getPackets(-1, channelID);
	}
}
