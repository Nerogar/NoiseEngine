package de.nerogar.noise.game.core.network.packets;

import de.nerogar.noise.game.NoiseGame;
import de.nerogar.noise.game.SynchronizedSystem;
import de.nerogar.noise.network.Packet;
import de.nerogar.noise.serialization.NDSDataInputStream;
import de.nerogar.noise.serialization.NDSDataOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InitSystemContainerPacket extends Packet {

	private String             containerName;
	private Map<String, Short> systemIdMap;

	public InitSystemContainerPacket() {
	}

	public InitSystemContainerPacket(String containerName, Map<Short, SynchronizedSystem> systemIdMap) {
		this.containerName = containerName;

		this.systemIdMap = new HashMap<>();
		for (Map.Entry<Short, SynchronizedSystem> systemEntry : systemIdMap.entrySet()) {
			this.systemIdMap.put(systemEntry.getValue().getName(), systemEntry.getKey());
		}
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		NDSDataInputStream ndsIn = new NDSDataInputStream(in);

		containerName = ndsIn.readUTF8String(false, true);

		systemIdMap = new HashMap<>();
		int count = ndsIn.readInt();
		for (int i = 0; i < count; i++) {
			systemIdMap.put(ndsIn.readUTF8String(false, true), ndsIn.readShort());
		}
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		NDSDataOutputStream ndsOut = new NDSDataOutputStream(out);

		ndsOut.writeUTF8String(containerName, false, true);

		ndsOut.writeInt(systemIdMap.size());
		for (Map.Entry<String, Short> entry : systemIdMap.entrySet()) {
			ndsOut.writeUTF8String(entry.getKey(), false, true);
			ndsOut.writeShort(entry.getValue());
		}
	}

	public String getContainerName() {
		return containerName;
	}

	public Map<String, Short> getSystemIdMap() {
		return systemIdMap;
	}

	@Override
	public int getChannel() {
		return NoiseGame.CONTROL_PACKET_CHANNEL;
	}

}
