package de.nerogar.noise.network;

public interface Loadable {

	public void fromByteArray(byte[] data);

	public byte[] toByteArray();

}
