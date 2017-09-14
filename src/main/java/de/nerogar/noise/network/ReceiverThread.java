package de.nerogar.noise.network;

import de.nerogar.noise.Noise;
import de.nerogar.noise.network.Packets.PacketContainer;
import de.nerogar.noise.util.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ReceiverThread extends Thread {

	private Socket       socket;
	private SenderThread send;
	private final ArrayList<Packet> packets       = new ArrayList<>();
	private       ArrayList<Packet> polledPackets = new ArrayList<>();

	public ReceiverThread(Socket socket, SenderThread send) {
		setName("Receiver Thread for " + socket.toString());
		this.socket = socket;
		this.send = send;
		this.setDaemon(true);
		this.start();
	}

	@Override
	public void run() {

		try {
			DataInputStream stream = new DataInputStream(socket.getInputStream());
			int packetId = 0;
			while (!isInterrupted() && packetId >= 0) {

				packetId = stream.readInt();

				PacketContainer packetContainer = Packets.byId(packetId);
				if (packetContainer == null) {
					Noise.getLogger().log(Logger.WARNING, "received invalid packet id: " + packetId + ", closing connection.");
					socket.close();
				} else {
					int length = stream.readInt();
					int read = 0;

					byte[] buffer = new byte[length];
					while (read < length) {
						read += stream.read(buffer, read, length - read);
					}

					Packet packet;
					packet = packetContainer.load(buffer);
					addPacket(packet);
				}

			}
		} catch (SocketException e) {
			Noise.getLogger().log(Logger.ERROR, "SocketException in ReceiverThread");
			e.printStackTrace(Noise.getLogger().getErrorStream());
		} catch (IOException e) {
			Noise.getLogger().log(Logger.ERROR, "ReceiverThread crashed (maybe due to connection abort)");
			e.printStackTrace(Noise.getLogger().getErrorStream());
		}

		// make sure socket it closed when reaching this point
		try {
			socket.close();
		} catch (IOException e) {
			// already dead, okay
			e.printStackTrace();
		}

		send.interrupt();

	}

	private void addPacket(Packet packet) {
		synchronized (packets) {
			packets.add(packet);
		}
	}

	public void pollPackets() {
		synchronized (packets) {
			polledPackets.addAll(packets);
			packets.clear();
		}
	}

	public ArrayList<Packet> getPackets() {
		return polledPackets;
	}

	public void discardPackets() {
		polledPackets.clear();
	}

}
