package de.nerogar.noise.game.core.network.packets;

import de.nerogar.noise.game.NoiseGame;
import de.nerogar.noise.network.Packet;
import de.nerogar.noise.serialization.NDSDataInputStream;
import de.nerogar.noise.serialization.NDSDataOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MapInfoPacket extends Packet {

	private String mapID;

	public MapInfoPacket() {
	}

	public MapInfoPacket(String mapID) {
		this.mapID = mapID;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		NDSDataInputStream ndsIn = new NDSDataInputStream(in);

		mapID = ndsIn.readUTF8String(-1, true);
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		NDSDataOutputStream ndsOut = new NDSDataOutputStream(out);

		ndsOut.writeUTF8String(mapID, false, true);
	}

	public String getMapID() { return mapID; }

	@Override
	public int getChannel() {
		return NoiseGame.CONTROL_PACKET_CHANNEL;
	}

}
