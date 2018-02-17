package de.nerogar.noise.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AggregateNetworkAdapter implements INetworkAdapter {

	private List<INetworkAdapter> networkAdapters;

	public AggregateNetworkAdapter() {
		this.networkAdapters = new ArrayList<>();
	}

	public AggregateNetworkAdapter(Collection<INetworkAdapter> networkAdapters) {
		this.networkAdapters = new ArrayList<>(networkAdapters);
	}

	public void addNetworkAdapter(INetworkAdapter networkAdapter) {
		networkAdapters.add(networkAdapter);
	}

	public void removeNetworkAdapter(INetworkAdapter networkAdapter) {
		networkAdapters.remove(networkAdapter);
	}

	@Override
	public void send(Packet packet) {
		for (int i = 0; i < networkAdapters.size(); i++) {
			networkAdapters.get(i).send(packet);
		}
	}

	@Override
	public ArrayList<Packet> getPackets(int channelID) {
		ArrayList<Packet> packets = new ArrayList<>();
		for (int i = 0; i < networkAdapters.size(); i++) {
			packets.addAll(networkAdapters.get(i).getPackets(channelID));
		}
		return packets;
	}
}
