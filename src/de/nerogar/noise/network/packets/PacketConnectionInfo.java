package de.nerogar.noise.network.packets;

import java.nio.ByteBuffer;

public class PacketConnectionInfo extends Packet {

	public int version;

	public PacketConnectionInfo() {
	}

	public PacketConnectionInfo(int version) {
		this.version = version;
	}

	@Override
	public void fromByteArray(byte[] data) {
		version = ByteBuffer.wrap(data).getInt();
	}

	@Override
	public byte[] toByteArray() {
		return ByteBuffer.allocate(4).putInt(version).array();
	}

}
