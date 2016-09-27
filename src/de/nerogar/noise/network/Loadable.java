package de.nerogar.noise.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Loadable {

	void fromStream(DataInputStream in) throws IOException;

	void toStream(DataOutputStream out) throws IOException;

}
