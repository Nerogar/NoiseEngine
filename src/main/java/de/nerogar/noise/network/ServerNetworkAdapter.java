package de.nerogar.noise.network;

import java.util.ArrayList;

public class ServerNetworkAdapter implements INetworkAdapter {

	protected final int        id;
	protected final ServerThread serverThread;

	ServerNetworkAdapter(ServerThread serverThread, int id) {
		this.serverThread = serverThread;
		this.id = id;
	}

	@Override
	public void send(Packet packet) {
		serverThread.broadcast(id, packet);
	}

	@Override
	public ArrayList<Packet> getPackets(int channelID) {
		return serverThread.getPackets(id, channelID);
	}
}
