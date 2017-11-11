package de.nerogar.noise.network;

import de.nerogar.noise.Noise;
import de.nerogar.noise.network.packets.PacketPacketIdInfo;
import de.nerogar.noise.util.Logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SenderThread extends Thread {

	private Socket socket;
	private final ArrayList<Packet> packetQueue = new ArrayList<>();

	private boolean shouldFlush;

	private PacketInfo packetInfo;

	public SenderThread(Socket socket) {
		setName("Sender Thread for " + socket.toString());
		this.socket = socket;

		packetInfo = new PacketInfo();

		this.setDaemon(true);
		this.start();
	}

	public void flush() {
		shouldFlush = true;

		synchronized (packetQueue) {
			packetQueue.notify();
		}
	}

	@Override
	public void run() {
		try {
			DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			while (!isInterrupted()) {

				List<Packet> sendPackets = new ArrayList<>();
				synchronized (packetQueue) {

					if (packetQueue.isEmpty()) {
						if (shouldFlush) {
							stream.flush();
							shouldFlush = false;
						}
						packetQueue.wait();
					}

					for (Packet packet : packetQueue) {
						sendPackets.add(packet);
					}
					packetQueue.clear();
				}

				for (Packet packet : sendPackets) {
					try {
						if (!packetInfo.contains(packet.getClass())) {
							PacketContainer packetContainer = packetInfo.addPacket(packet.getChannel(), packet.getClass());

							Packet packetPacketIdInfo = new PacketPacketIdInfo(packetContainer.packetClass.getName(), packetContainer.id);
							stream.writeInt(packetInfo.byClass(packetPacketIdInfo.getClass()).getID());
							byte[] data = packetPacketIdInfo.getBuffer();
							stream.writeInt(data.length);
							stream.write(data);
						}

						stream.writeInt(packetInfo.byClass(packet.getClass()).getID());

						byte[] data = packet.getBuffer();

						stream.writeInt(data.length);
						stream.write(data);
					} catch (IOException e) {
						Noise.getLogger().log(Logger.ERROR, "Could not send packet " + packet);
						e.printStackTrace(Noise.getLogger().getErrorStream());
					}

				}

			}

			stream.close();

		} catch (InterruptedException e) {
			interrupt();
			// System.err.println("InterruptedException in SenderThread");
			// e.printStackTrace();
		} catch (IOException e) {
			// System.err.println("DataOutputStream is unavailable in SenderThread.");
			// e.printStackTrace();
		}

	}

	public void send(Packet packet) {
		synchronized (packetQueue) {
			packetQueue.add(packet);
			packetQueue.notify();
		}
	}

}
