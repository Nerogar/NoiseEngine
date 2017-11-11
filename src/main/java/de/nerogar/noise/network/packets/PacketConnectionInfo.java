package de.nerogar.noise.network.packets;

import de.nerogar.noise.network.Packet;
import de.nerogar.noise.network.PacketInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketConnectionInfo extends Packet {

	public int version;

	public PacketConnectionInfo() {
	}

	public PacketConnectionInfo(int version) {
		this.version = version;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		version = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(version);
	}

	@Override
	public int getChannel() {
		return PacketInfo.SYSTEM_PACKET_CHANNEL;
	}

}
