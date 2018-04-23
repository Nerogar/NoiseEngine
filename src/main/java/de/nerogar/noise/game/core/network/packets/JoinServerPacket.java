package de.nerogar.noise.game.core.network.packets;

import de.nerogar.noise.game.NoiseGame;
import de.nerogar.noise.network.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JoinServerPacket extends Packet {

	private int faction;

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		faction = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(faction);
	}

	public int getFaction()             { return faction; }

	public void setFaction(int faction) { this.faction = faction; }

	@Override
	public int getChannel() {
		return NoiseGame.CONTROL_PACKET_CHANNEL;
	}

}
