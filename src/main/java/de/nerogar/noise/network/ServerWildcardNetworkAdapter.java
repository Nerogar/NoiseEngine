package de.nerogar.noise.network;

import java.util.ArrayList;

public class ServerWildcardNetworkAdapter implements INetworkAdapter {

	protected final ServerThread server;

	ServerWildcardNetworkAdapter(ServerThread server) {
		this.server = server;
	}

	@Override
	public void send(Packet packet) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ArrayList<Packet> getPackets(int channelID) {
		return server.getPackets(-1, channelID);
	}
}
