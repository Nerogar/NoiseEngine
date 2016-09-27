package de.nerogar.noise.network;

import de.nerogar.noise.network.packets.Packet;
import de.nerogar.noise.util.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SenderThread extends Thread {

	private Socket socket;
	private final ArrayList<Packet> packets = new ArrayList<>();

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
			DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			while (!isInterrupted()) {

				List<Packet> sendPackets = new ArrayList<>();
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

						ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
						DataOutputStream out = new DataOutputStream(arrayOut);
						packet.toStream(out);
						byte[] data = arrayOut.toByteArray();

						stream.writeInt(data.length);
						stream.write(data);
					} catch (IOException e) {
						Logger.log(Logger.ERROR, "Could not send packet " + packet);
						e.printStackTrace(Logger.getErrorStream());
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
