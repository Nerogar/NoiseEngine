package de.nerogar.noise.network;

import java.util.ArrayList;

public interface INetworkAdapter {

	void send(Packet packet);

	ArrayList<Packet> getPackets(int channelID);

}
