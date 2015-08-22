package de.nerogar.noise.DNFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class DNFile extends DNNodePath {

	public DNFile() {
		super(null);
	}

	/**
	 * Loads the DNFile
	 * @param filename the filename of the source file.
	 */
	public boolean load(String filename) throws IOException {
		nodes = new HashMap<String, DNNode>();
		paths = new HashMap<String, DNNodePath>();

		File file = new File(filename);
		FileInputStream f = new FileInputStream(file);
		DNByteBuffer in = new DNByteBuffer((int) f.getChannel().size());
		f.getChannel().read(in.byteBuffer);
		in.byteBuffer.flip();

		readFile(in, this, -1);

		f.close();
		return true;

	}

	/**
	 * Creates the DNFile object from a byte array
	 * @param input the byte array to create the DNFile object
	 */
	public void fromByteArray(byte[] input) throws IOException {
		nodes = new HashMap<String, DNNode>();
		paths = new HashMap<String, DNNodePath>();

		DNByteBuffer in = new DNByteBuffer(input);
		in.byteBuffer.flip();
		readFile(in, this, -1);
	}

	private void readFile(DNByteBuffer in, DNNodePath path, int length) throws IOException {

		int item = 0;
		while (in.available() > 0 && (item < length || length < 0)) {
			DNNode dnNode = readNextNode(in);
			if (dnNode.typ != DNHelper.FOLDER) {
				path.addNode(dnNode.name, dnNode.typ, dnNode.length, dnNode.value);
			} else {
				readFile(in, path.getPath(dnNode.name), dnNode.length);
			}
			item++;
		}
	}

	private DNNode readNextNode(DNByteBuffer in) throws IOException {
		String name = in.readString();
		byte typ = in.readByte();
		int length = in.readInt();
		Object values = null;

		switch (typ) {
		case DNHelper.FOLDER:
			return new DNNode(name, typ, length, null);
		case DNHelper.INTEGER:
			if (length >= 0) {
				int[] tempValues = new int[length];
				for (int i = 0; i < length; i++) {
					tempValues[i] = in.readInt();
				}
				values = tempValues;
			}
			break;
		case DNHelper.LONG:
			if (length >= 0) {
				long[] tempValues = new long[length];
				for (int i = 0; i < length; i++) {
					tempValues[i] = in.readLong();
				}
				values = tempValues;
			}
			break;
		case DNHelper.BYTE:
			if (length >= 0) {
				byte[] tempValues = new byte[length];
				for (int i = 0; i < length; i++) {
					tempValues[i] = in.readByte();
				}
				values = tempValues;
			}
			break;
		case DNHelper.CHAR:
			if (length >= 0) {
				char[] tempValues = new char[length];
				for (int i = 0; i < length; i++) {
					tempValues[i] = in.readChar();
				}
				values = tempValues;
			}
			break;
		case DNHelper.STRING:
			if (length >= 0) {
				String[] tempValues = new String[length];
				for (int i = 0; i < length; i++) {
					tempValues[i] = in.readString();
				}
				values = tempValues;
			}
			break;

		case DNHelper.FLOAT:
			if (length >= 0) {
				float[] tempValues = new float[length];
				for (int i = 0; i < length; i++) {
					tempValues[i] = in.readFloat();
				}
				values = tempValues;
			}
			break;
		case DNHelper.DOUBLE:
			if (length >= 0) {
				double[] tempValues = new double[length];
				for (int i = 0; i < length; i++) {
					tempValues[i] = in.readDouble();
				}
				values = tempValues;
			}
			break;
		case DNHelper.BOOLEAN:
			if (length >= 0) {
				boolean[] tempValues = new boolean[length];
				for (int i = 0; i < length; i++) {
					tempValues[i] = in.readBool();
				}
				values = tempValues;
			}
			break;
		}

		return new DNNode(name, typ, length, values);

	}

	/**
	 * Saves the DNFile
	 * @param filename the filename of the target file.
	 */
	public void save(String filename) throws IOException {

		File file = new File(filename);
		FileOutputStream f = new FileOutputStream(file);
		DNByteBuffer out = getAsBuffer();

		out.byteBuffer.flip();
		f.getChannel().write(out.byteBuffer);

		f.close();

	}

	/**
	 * Returns a byte array containing the DNFile content
	 */
	public byte[] toByteArray() {
		return getAsBuffer().byteBuffer.array();
	}

	private DNByteBuffer getAsBuffer() {
		DNByteBuffer out = new DNByteBuffer(calcSize());

		try {
			writeFile(out, this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return out;
	}

	private void writeFile(DNByteBuffer out, DNNodePath path) throws IOException {

		for (DNNode node : path.nodes.values()) {
			writeNode(out, node);
		}

		for (DNNodePath node : path.paths.values()) {
			DNTagWriter.writeFolderTag(out, node.name, node.getNodes().size() + node.getPaths().size());
			writeFile(out, node);
		}
	}

	private void writeNode(DNByteBuffer out, DNNode dnNode) throws IOException {
		Object value = dnNode.value;
		byte type = dnNode.typ;
		String name = dnNode.name;

		switch (type) {
		case (DNHelper.INTEGER):
			DNTagWriter.writeIntTag(out, name, (int[]) value);
			break;
		case (DNHelper.LONG):
			DNTagWriter.writeLongTag(out, name, (long[]) value);
			break;
		case (DNHelper.BYTE):
			DNTagWriter.writeByteTag(out, name, (byte[]) value);
			break;
		case (DNHelper.CHAR):
			DNTagWriter.writeCharTag(out, name, (char[]) value);
			break;
		case (DNHelper.STRING):
			DNTagWriter.writeStringTag(out, name, (String[]) value);
			break;
		case (DNHelper.FLOAT):
			DNTagWriter.writeFloatTag(out, name, (float[]) value);
			break;
		case (DNHelper.DOUBLE):
			DNTagWriter.writeDoubleTag(out, name, (double[]) value);
			break;
		case (DNHelper.BOOLEAN):
			DNTagWriter.writeBoolTag(out, name, (boolean[]) value);
			break;
		}
	}
}
