package de.nerogar.noise.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Packet implements Streamable {

	private byte[] buffer;
	private int    adapterId;

	synchronized final byte[] getBuffer() throws IOException {
		if (buffer == null) {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(byteOut);
			toStream(dataOut);

			buffer = byteOut.toByteArray();
		}

		return buffer;
	}

	void setAdapterId(int adapterId) { this.adapterId = adapterId; }

	int getAdapterId()               { return adapterId; }

	public abstract int getChannel();

}
