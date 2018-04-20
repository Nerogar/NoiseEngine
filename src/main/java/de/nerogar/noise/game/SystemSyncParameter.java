package de.nerogar.noise.game;

import de.nerogar.noise.network.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class SystemSyncParameter extends Packet {

	private short systemId;

	public void setSystemId(short systemId) {
		this.systemId = systemId;
	}

	public short getSystemId() {
		return systemId;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		systemId = in.readShort();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeShort(systemId);
	}

	@Override
	public int getChannel() {
		return NoiseGame.SYSTEMS_PACKET_CHANNEL;
	}

}
