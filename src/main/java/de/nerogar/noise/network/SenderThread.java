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
	private final ArrayList<PacketTuple> packetQueue = new ArrayList<>();

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

	private void writeSingle(DataOutputStream stream, int adapterId, Packet packet) throws IOException {
		stream.writeInt(packetInfo.byClass(packet.getClass()).getID());

		stream.writeByte(adapterId);

		byte[] data = packet.getBuffer();
		stream.writeInt(data.length);

		stream.write(data);
	}

	@Override
	public void run() {
		try {
			DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			while (!isInterrupted()) {

				List<PacketTuple> sendPackets;
				synchronized (packetQueue) {

					if (packetQueue.isEmpty()) {
						if (shouldFlush) {
							stream.flush();
							shouldFlush = false;
						}
						packetQueue.wait();
					}

					sendPackets = new ArrayList<>(packetQueue);
					packetQueue.clear();
				}

				for (PacketTuple packetTuple : sendPackets) {
					try {
						if (!packetInfo.contains(packetTuple.packet.getClass())) {
							PacketContainer packetContainer = packetInfo.addPacket(packetTuple.packet.getChannel(), packetTuple.packet.getClass());

							Packet packetPacketIdInfo = new PacketPacketIdInfo(packetContainer.packetClass.getName(), packetContainer.id);
							writeSingle(stream, 0, packetPacketIdInfo);
						}
						writeSingle(stream, packetTuple.adapterId, packetTuple.packet);
					} catch (IOException e) {
						Noise.getLogger().log(Logger.ERROR, "Could not send packet " + packetTuple.packet + " over adapter " + packetTuple.adapterId);
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

	public void send(int adapterId, Packet packet) {
		synchronized (packetQueue) {
			packetQueue.add(new PacketTuple(adapterId, packet));
			packetQueue.notify();
		}
	}

	private class PacketTuple {

		private final int    adapterId;
		private final Packet packet;

		public PacketTuple(int adapterId, Packet packet) {
			this.adapterId = adapterId;
			this.packet = packet;
		}
	}

}
