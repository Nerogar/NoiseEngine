package de.nerogar.noise.network;

import de.nerogar.noise.Noise;
import de.nerogar.noise.network.packets.PacketConnectionInfo;
import de.nerogar.noise.util.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Connection {

	private Socket         socket;
	private ReceiverThread receiver;
	private SenderThread   sender;

	public Connection(Socket socket) {
		if (socket == null) { return; }
		this.socket = socket;
		this.sender = new SenderThread(socket);
		this.receiver = new ReceiverThread(socket, sender);
		sender.send(new PacketConnectionInfo(PacketInfo.NETWORKING_VERSION));
		flushPackets();
	}

	public void send(Packet packet) {
		sender.send(packet);
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

	public ArrayList<Packet> getPackets(int channelID) {
		ArrayList<Packet> packets = new ArrayList<>();
		ArrayList<Packet> availablePackets = receiver.getPackets();
		synchronized (availablePackets) {
			for (Iterator<Packet> iter = availablePackets.iterator(); iter.hasNext(); ) {
				Packet p = iter.next();
				if (p.getChannel() == channelID) {
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
