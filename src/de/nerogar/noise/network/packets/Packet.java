package de.nerogar.noise.network.packets;

import de.nerogar.noise.network.Loadable;

public abstract class Packet implements Loadable {

	public abstract void fromByteArray(byte[] data);

	public abstract byte[] toByteArray();

}
