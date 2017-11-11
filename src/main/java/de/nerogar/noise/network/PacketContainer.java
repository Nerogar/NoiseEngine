package de.nerogar.noise.network;

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PacketContainer {

	public int                     channelID;
	public Class<? extends Packet> packetClass;
	public int                     id;

	public PacketContainer(int id, int channelID, Class<? extends Packet> packetClass) {
		this.id = id;
		this.channelID = channelID;
		this.packetClass = packetClass;
	}

	public Packet load(byte[] data) {
		Packet p = null;
		try {
			p = packetClass.newInstance();
			p.fromStream(new DataInputStream(new ByteArrayInputStream(data)));
		} catch (InstantiationException | IllegalAccessException e) {
			Noise.getLogger().log(Logger.ERROR, "Error calling constructor of packet class. Make sure every Packet has a default (empty) constructor!");
			e.printStackTrace(Noise.getLogger().getErrorStream());
		} catch (IOException e) {
			Noise.getLogger().log(Logger.ERROR, "Error reading packet from stream: " + p);
			e.printStackTrace(Noise.getLogger().getErrorStream());
		}
		return p;
	}

	public int getID() {
		return id;
	}

	public int getChannel() {
		return channelID;
	}
}
