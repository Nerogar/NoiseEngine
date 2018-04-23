package de.nerogar.noise.game.core.network.packets;

import de.nerogar.noise.game.NoiseGame;
import de.nerogar.noise.network.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerPacket extends Packet {

	public static final int COMMAND_CREATE_SYSTEMS  = 1;
	public static final int COMMAND_LOAD_MAP        = 2;
	public static final int COMMAND_INIT_CONTROLLER = 3;
	public static final int COMMAND_START           = 4;

	private int command;

	public ServerPacket() {
	}

	public ServerPacket(int command) {
		this.command = command;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		command = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(command);
	}

	public int getCommand() { return command; }

	@Override
	public int getChannel() {
		return NoiseGame.CONTROL_PACKET_CHANNEL;
	}

}
