package de.nerogar.noise.game.core.network.packets;

import de.nerogar.noise.game.NoiseGame;
import de.nerogar.noise.network.Packet;
import de.nerogar.noise.serialization.NDSDataInputStream;
import de.nerogar.noise.serialization.NDSDataOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientPacket extends Packet {

	public static final int COMMAND_START  = 1;
	public static final int COMMAND_PAUSE  = 2;
	public static final int COMMAND_RESUME = 3;
	public static final int COMMAND_STOP   = 4;

	private int    command;
	private String mapID;

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		NDSDataInputStream ndsIn = new NDSDataInputStream(in);

		command = ndsIn.readInt();

		if (command == COMMAND_START) {
			mapID = ndsIn.readUTF8String(-1, true);
		}
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		NDSDataOutputStream ndsOut = new NDSDataOutputStream(out);

		ndsOut.writeInt(command);

		if (command == COMMAND_START) {
			ndsOut.writeUTF8String(mapID, false, true);
		}
	}

	public int getCommand()             { return command; }

	public void setCommand(int command) { this.command = command; }

	public String getMapID()            { return mapID; }

	public void setMapID(String mapID)  { this.mapID = mapID; }

	@Override
	public int getChannel() {
		return NoiseGame.CONTROL_PACKET_CHANNEL;
	}

}
