package de.nerogar.noise.network;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.nerogar.noise.network.packets.Packet;

public class SenderThread extends Thread {

	private Socket socket;
	private ArrayList<Packet> packets = new ArrayList<Packet>();

	private DataOutputStream stream;

	private boolean shouldFlush;

	public SenderThread(Socket socket) {
		setName("Sender Thread for " + socket.toString());
		this.socket = socket;
		this.setDaemon(true);
		this.start();
	}

	public void flush() {
		shouldFlush = true;

		synchronized (packets) {
			packets.notify();
		}
	}

	@Override
	public void run() {
		try {
			stream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			while (!isInterrupted()) {

				List<Packet> sendPackets = new ArrayList<Packet>();
				synchronized (packets) {

					if (packets.isEmpty()) {
						if (shouldFlush) {
							stream.flush();
							shouldFlush = false;
						}
						packets.wait();
					}

					for (Packet packet : packets) {
						sendPackets.add(packet);
					}
					packets.clear();
				}

				for (Packet packet : sendPackets) {
					try {
						stream.writeInt(Packets.byClass(packet.getClass()).getID());
						byte[] data = packet.toByteArray();
						stream.writeInt(data.length);
						stream.write(data);
					} catch (IOException e) {
						System.err.println("Could not send packet " + packet.toString());
						// e.printStackTrace();
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
		synchronized (packets) {
			packets.add(packet);
			packets.notify();
		}
	}

}
