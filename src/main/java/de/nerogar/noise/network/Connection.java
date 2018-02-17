package de.nerogar.noise.network;

import de.nerogar.noise.Noise;
import de.nerogar.noise.network.packets.PacketConnectionInfo;
import de.nerogar.noise.util.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Connection{

	private Socket         socket;
	private ReceiverThread receiver;
	private SenderThread   sender;

	private INetworkAdapter[] networkAdapters = new INetworkAdapter[256];
	private INetworkAdapter wildcardNetworkAdapter;

	public Connection(Socket socket) {
		if (socket == null) { return; }
		this.socket = socket;
		this.sender = new SenderThread(socket);
		this.receiver = new ReceiverThread(socket, sender);
		wildcardNetworkAdapter = new WildcardNetworkAdapter(this);
		getNetworkAdapter(0).send(new PacketConnectionInfo(PacketInfo.NETWORKING_VERSION));
		flushPackets();
	}

	/**
	 * returns the {@link INetworkAdapter} with the specified id
	 *
	 * @param id the adapter id
	 * @return the network adapter
	 * @throws IllegalArgumentException when the id is over 64
	 */
	public INetworkAdapter getNetworkAdapter(int id) {
		if (id > 255) throw new IllegalArgumentException("id has to fit in 8 bits (0 <= id <= 255)");

		if (networkAdapters[id] == null) {
			networkAdapters[id] = new SimpleNetworkAdapter(this, id);
		}

		return networkAdapters[id];
	}

	/**
	 * returns an {@link INetworkAdapter} that aggregates all network adapters
	 *
	 * @return the network adapter
	 */
	public INetworkAdapter getWildcardNetworkAdapter() {
		return wildcardNetworkAdapter;
	}

	void send(int adapterId, Packet packet) {
		sender.send(adapterId, packet);
	}

	/**
	 * flush the packet list.
	 * (actually send it)
	 */
	public void flushPackets() {
		sender.flush();
	}

	/**
	 * @param discardOld discard previously polled packets
	 */
	public void pollPackets(boolean discardOld) {
		if (discardOld) discardPackets();
		receiver.pollPackets();
	}

	private void discardPackets() {
		receiver.discardPackets();
	}

	ArrayList<Packet> getPackets(int adapterId, int channelID) {
		ArrayList<Packet> packets = new ArrayList<>();
		ArrayList<Packet> availablePackets = receiver.getPackets();
		synchronized (availablePackets) {
			for (Iterator<Packet> iter = availablePackets.iterator(); iter.hasNext(); ) {
				Packet p = iter.next();
				if (p.getChannel() == channelID && (adapterId < 0 || p.getAdapterId() == adapterId)) {
					packets.add(p);
					iter.remove();
				}
			}
		}
		return packets;
	}

	public synchronized void close() {
		// recv.stopThread();
		// send.stopThread();
		try {
			socket.close();
		} catch (IOException e) {
			Noise.getLogger().log(Logger.WARNING, "Could not close socket in Client.");
			e.printStackTrace();
		}
		Noise.getLogger().log(Logger.INFO, "SHUTDOWN: Connection - " + socket.toString());
	}

	public boolean isClosed() {
		if (socket == null) { return true; }
		return socket.isClosed();
	}

}
