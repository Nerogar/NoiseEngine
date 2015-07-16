package de.nerogar.noise.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import de.nerogar.noise.network.Packets.PacketContainer;
import de.nerogar.noise.network.packets.Packet;

public class ReceiverThread extends Thread {

	private Socket socket;
	private SenderThread send;
	private ArrayList<Packet> packets = new ArrayList<Packet>();
	private ArrayList<Packet> polledPackets = new ArrayList<Packet>();

	public ReceiverThread(Socket socket, SenderThread send) {
		setName("Reveiver Thread for " + socket.toString());
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
					System.out.println("received invalid packet id: " + packetId + ", ignored.");
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
			// System.err.println("SocketException in ReceiverThread");
			// e.printStackTrace();
		} catch (IOException e) {
			// System.err.println("ReceiverThread crashed (maybe due to connection abort)");
			// e.printStackTrace();
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
