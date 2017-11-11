package de.nerogar.noise.network.packets;

import de.nerogar.noise.network.Packet;
import de.nerogar.noise.network.PacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPacketIdInfo extends Packet {

	private String packetClassName;
	private int    packetId;

	public PacketPacketIdInfo() {
	}

	public PacketPacketIdInfo(String packetClassName, int packetId) {
		this.packetClassName = packetClassName;
		this.packetId = packetId;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		packetClassName = in.readUTF();
		packetId = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeUTF(packetClassName);
		out.writeInt(packetId);
	}

	public String getPacketClassName() { return packetClassName; }

	public int getPacketId()           { return packetId; }

	@Override
	public int getChannel() {
		return PacketInfo.SYSTEM_PACKET_CHANNEL;
	}

}
