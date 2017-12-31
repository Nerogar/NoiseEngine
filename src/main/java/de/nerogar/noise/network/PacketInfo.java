package de.nerogar.noise.network;

import de.nerogar.noise.Noise;
import de.nerogar.noise.network.packets.PacketConnectionInfo;
import de.nerogar.noise.network.packets.PacketPacketIdInfo;
import de.nerogar.noise.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class PacketInfo {

	public static final int SYSTEM_PACKET_CHANNEL = -1;
	public static final int NETWORKING_VERSION    = 2000;

	private List<PacketContainer> packets;

	public PacketInfo() {
		packets = new ArrayList<PacketContainer>();

		addPacket(SYSTEM_PACKET_CHANNEL, PacketConnectionInfo.class);
		addPacket(SYSTEM_PACKET_CHANNEL, PacketPacketIdInfo.class);
	}

	public boolean contains(Class<? extends Packet> packetClass) {
		for (PacketContainer pc : packets) {
			if (pc.packetClass.equals(packetClass)) { return true; }
		}
		return false;
	}

	public PacketContainer byId(int id) {
		for (PacketContainer pc : packets) {
			if (pc.id == id) { return pc; }
		}
		return null;
	}

	public PacketContainer byClass(Class<? extends Packet> packetClass) {
		for (PacketContainer pc : packets) {
			if (pc.packetClass.equals(packetClass)) { return pc; }
		}
		return null;
	}

	private int MAX_ID = 0;

	private int getNextPacketID() {
		return MAX_ID++;
	}

	protected PacketContainer addPacket(int channelID, Class<? extends Packet> packetClass) {
		PacketContainer packetContainer = new PacketContainer(getNextPacketID(), channelID, packetClass);
		packets.add(packetContainer);
		return packetContainer;
	}

	public void addPacket(PacketPacketIdInfo packet) {
		try {
			Packet newPacket = (Packet) Class.forName(packet.getPacketClassName()).newInstance();
			addPacket(newPacket.getChannel(), newPacket.getClass());
		} catch (InstantiationException e) {
			Noise.getLogger().log(Logger.ERROR, "Could not instantiate packet for class: " + packet.getPacketClassName() + ". Make sure every Packet has a default (empty) constructor!");
			e.printStackTrace(Noise.getLogger().getErrorStream());
		} catch (IllegalAccessException e) {
			Noise.getLogger().log(Logger.ERROR, "Could not instantiate packet, make sure the class and constructor are public.");
			e.printStackTrace(Noise.getLogger().getErrorStream());
		} catch (ClassNotFoundException e) {
			Noise.getLogger().log(Logger.ERROR, "Could not instantiate packet, class " + packet.getPacketClassName() + " could not be found.");
			e.printStackTrace(Noise.getLogger().getErrorStream());
		}
	}

}
