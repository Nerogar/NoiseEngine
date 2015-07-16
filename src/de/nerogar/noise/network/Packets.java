package de.nerogar.noise.network;

import java.util.ArrayList;
import java.util.List;

import de.nerogar.noise.network.packets.Packet;
import de.nerogar.noise.network.packets.PacketConnectionInfo;

public class Packets {
	
	public static final int SYSTEM_PACKET_CHANNEL = -1;
	
	public static class PacketContainer {
		public int channelID;
		public Class<? extends Packet> packetClass;
		public int id;

		public PacketContainer(int id, int channelID, Class<? extends Packet> packetClass) {
			this.id = id;
			this.channelID = channelID;
			this.packetClass = packetClass;
		}

		public Packet load(byte[] data) {
			Packet p = null;
			try {
				p = packetClass.newInstance();
				p.fromByteArray(data);
			} catch (InstantiationException | IllegalAccessException e) {
				System.err.println("Error calling constructor of packet class. Make sure every Packet has a default (empty) constructor!");
				e.printStackTrace();
			}
			return p;
		}

		public int getID() {
			return id;
		}

		public int getChannel() {
			return channelID;
		}
	}

	private static List<PacketContainer> packets;

	public static final int NETWORKING_VERSION = 1000;

	public static PacketContainer byId(int id) {
		for (PacketContainer pc : packets) {
			if (pc.id == id) { return pc; }
		}
		return null;
	}

	public static PacketContainer byClass(Class<? extends Packet> packetClass) {
		for (PacketContainer pc : packets) {
			if (pc.packetClass.equals(packetClass)) { return pc; }
		}
		return null;
	}

	private static int MAX_ID = 0;

	private static int getNextPacketID() {
		return MAX_ID++;
	}

	public static void addPacket(int channelID, Class<? extends Packet> packetClass) {
		packets.add(new PacketContainer(getNextPacketID(), channelID, packetClass));
	}

	static {
		packets = new ArrayList<PacketContainer>();

		addPacket(SYSTEM_PACKET_CHANNEL, PacketConnectionInfo.class);
	}

}
