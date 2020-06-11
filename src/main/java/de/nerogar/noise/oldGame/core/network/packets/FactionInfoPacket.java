package de.nerogar.noise.oldGame.core.network.packets;

import de.nerogar.noise.oldGame.NoiseGame;
import de.nerogar.noise.network.Packet;
import de.nerogar.noise.serialization.NDSDataInputStream;
import de.nerogar.noise.serialization.NDSDataOutputStream;
import de.nerogar.noise.oldGame.Faction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class FactionInfoPacket extends Packet {

	private int[] factionIDs;
	private int[]  factionColors;
	private byte   ownFaction;

	public FactionInfoPacket() {
	}

	public FactionInfoPacket(List<Faction> factions, byte ownFaction) {
		factionIDs = new int[factions.size()];
		factionColors = new int[factions.size()];
		for (int i = 0; i < factions.size(); i++) {
			factionIDs[i] = factions.get(i).getID();
			factionColors[i] = factions.get(i).getColor().getARGB();
		}
		this.ownFaction = ownFaction;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		NDSDataInputStream ndsIn = new NDSDataInputStream(in);

		factionIDs = ndsIn.readUnsignedShortArray();
		factionColors = ndsIn.readIntArray();
		ownFaction = ndsIn.readByte();

	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		NDSDataOutputStream ndsOut = new NDSDataOutputStream(out);

		ndsOut.writeUnsignedShortArray(factionIDs);
		ndsOut.writeIntArray(factionColors);
		ndsOut.writeByte(ownFaction);

	}

	public int[] getFactionIDs()   { return factionIDs; }

	public int[] getFactionColors() { return factionColors; }

	public byte getOwnFaction()     { return ownFaction; }

	@Override
	public int getChannel() {
		return NoiseGame.CONTROL_PACKET_CHANNEL;
	}

}
