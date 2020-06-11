package de.nerogar.noise.oldGame.core.network.packets;

import de.nerogar.noise.oldGame.NoiseGame;
import de.nerogar.noise.oldGame.SynchronizedSystem;
import de.nerogar.noise.network.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InitSystemPacket extends Packet {

	private short              systemId;
	private SynchronizedSystem system;

	private DataInputStream input;

	public InitSystemPacket() {
	}

	public InitSystemPacket(SynchronizedSystem system) {
		this.systemId = system.getId();
		this.system = system;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		systemId = in.readShort();

		this.input = in;
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeShort(systemId);
		system.sendNetworkInit(out);
	}

	public short getSystemId()        { return systemId; }

	public DataInputStream getInput() { return input; }

	@Override
	public int getChannel() {
		return NoiseGame.CONTROL_PACKET_CHANNEL;
	}

}
