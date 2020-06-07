package de.nerogar.noise.DNFileSystem;

import java.io.IOException;

public class DNTagWriter {

	protected static boolean writeFolderTag(DNByteBuffer out, String name, int length) throws IOException {
		out.writeString(name);
		out.writeByte(DNHelper.FOLDER);
		out.writeInt(length);
		return true;
	}

	protected static boolean writeIntTag(DNByteBuffer out, String name, int[] values) throws IOException {
		out.writeString(name);
		out.writeByte(DNHelper.INTEGER);
		if (values != null) {
			out.writeInt(values.length);
			for (int value : values) {
				out.writeInt(value);
			}
		} else {
			out.writeInt(-1);
		}
		return true;
	}

	protected static boolean writeFloatTag(DNByteBuffer out, String name, float[] values) throws IOException {
		out.writeString(name);
		out.writeByte(DNHelper.FLOAT);
		if (values != null) {
			out.writeInt(values.length);
			for (float value : values) {
				out.writeFloat(value);
			}
		} else {
			out.writeInt(-1);
		}
		return true;
	}

	protected static boolean writeDoubleTag(DNByteBuffer out, String name, double[] values) throws IOException {
		out.writeString(name);
		out.writeByte(DNHelper.DOUBLE);
		if (values != null) {
			out.writeInt(values.length);
			for (double value : values) {
				out.writeDouble(value);
			}
		} else {
			out.writeInt(-1);
		}
		return true;
	}

	protected static boolean writeLongTag(DNByteBuffer out, String name, long[] values) throws IOException {
		out.writeString(name);
		out.writeByte(DNHelper.LONG);
		if (values != null) {
			out.writeInt(values.length);
			for (long value : values) {
				out.writeLong(value);
			}
		} else {
			out.writeInt(-1);
		}
		return true;
	}

	protected static boolean writeByteTag(DNByteBuffer out, String name, byte[] values) throws IOException {
		out.writeString(name);
		out.writeByte(DNHelper.BYTE);
		if (values != null) {
			out.writeInt(values.length);
			for (byte value : values) {
				out.writeByte(value);
			}
		} else {
			out.writeInt(-1);
		}
		return true;
	}

	protected static boolean writeCharTag(DNByteBuffer out, String name, char[] values) throws IOException {
		out.writeString(name);
		out.writeByte(DNHelper.CHAR);
		if (values != null) {
			out.writeInt(values.length);
			for (char value : values) {
				out.writeChar(value);
			}
		} else {
			out.writeInt(-1);
		}
		return true;
	}

	protected static boolean writeBoolTag(DNByteBuffer out, String name, boolean[] values) throws IOException {
		out.writeString(name);
		out.writeByte(DNHelper.BOOLEAN);
		if (values != null) {
			out.writeInt(values.length);
			for (boolean value : values) {
				out.writeBool(value);
			}
		} else {
			out.writeInt(-1);
		}
		return true;
	}

	protected static boolean writeStringTag(DNByteBuffer out, String name, String[] values) throws IOException {
		out.writeString(name);
		out.writeByte(DNHelper.STRING);
		if (values != null) {
			out.writeInt(values.length);
			for (String value : values) {
				out.writeString(value);
			}
		} else {
			out.writeInt(-1);
		}
		return true;
	}
}
