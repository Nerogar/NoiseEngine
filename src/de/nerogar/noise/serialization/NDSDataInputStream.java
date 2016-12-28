package de.nerogar.noise.serialization;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class NDSDataInputStream extends FilterInputStream {

	private static final String ERROR_BYTE_INDEX_TOO_FAR = "input stream is ahead of target byte index";

	private long byteIndex = 0;

	public NDSDataInputStream(InputStream in) {
		super(in);
	}

	public void resetByteIndex() {
		byteIndex = 0;
	}

	public long getByteIndex() {
		return byteIndex;
	}

	public long skipToByteIndex(long targetByteIndex) throws IOException {
		if (targetByteIndex < byteIndex) throw new IOException(ERROR_BYTE_INDEX_TOO_FAR);

		return skip(targetByteIndex - byteIndex);
	}

	@Override
	public long skip(long n) throws IOException {
		long skipped = super.skip(n);
		byteIndex += skipped;
		return skipped;
	}

	@Override
	public int read() throws IOException {
		int next = super.read();
		if (next < 0) throw new EOFException();
		byteIndex++;
		return next;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int readBytes = super.read(b, off, len);

		byteIndex += readBytes;

		return readBytes;
	}

	// big int

	public BigInteger readBigInt() throws IOException {
		int length = readUnsignedByte();
		byte[] array = new byte[length];

		for (int i = 0; i < length; i++) {
			array[i] = readByte();
		}

		return new BigInteger(array);
	}

	public BigInteger[] readBigIntArray() throws IOException {
		BigInteger[] bigInts = new BigInteger[readInt()];

		for (int i = 0; i < bigInts.length; i++) {
			bigInts[i] = readBigInt();
		}

		return bigInts;
	}

	// boolean

	public boolean readBoolean() throws IOException {
		int next = read();
		return next != 0;
	}

	public boolean[] readBooleanArray() throws IOException {
		boolean[] boolArray = new boolean[readInt()];

		for (int i = 0; i < boolArray.length; i++) {
			boolArray[i] = readBoolean();
		}

		return boolArray;
	}

	// float

	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	// signed

	public byte readNibble() throws IOException {
		byte b = readByte();

		if ((b & 0x08) != 0) {
			b |= 0xF0; // correct sign
		}

		return b;
	}

	public byte readByte() throws IOException {
		return (byte) read();
	}

	public short readShort() throws IOException {
		int b0 = read();
		int b1 = read();
		return (short) ((b0 << 8) | b1);
	}

	public int readInt() throws IOException {
		int b0 = read();
		int b1 = read();
		int b2 = read();
		int b3 = read();
		return ((b0 << 24) | (b1 << 16) | (b2 << 8) | b3);
	}

	public long readLong() throws IOException {
		long b0 = read();
		long b1 = read();
		long b2 = read();
		long b3 = read();

		long b4 = read();
		long b5 = read();
		long b6 = read();
		long b7 = read();

		return ((b0 << 56) | (b1 << 48) | (b2 << 40) | (b3 << 32) | (b4 << 24) | (b5 << 16) | (b6 << 8) | b7);
	}

	// unsigned

	public byte readUnsignedNibble() throws IOException {
		return readByte();
	}

	public short readUnsignedByte() throws IOException {
		return (short) read();
	}

	public int readUnsignedShort() throws IOException {
		return ((int) readShort()) & 0xFFFF; // undo sign extension
	}

	public long readUnsignedInt() throws IOException {
		return ((long) readInt()) & 0xFFFFFFFFL; // undo sign extension
	}

	public long readUnsignedLong() throws IOException {
		return readLong(); // we don't have a bigger datatype, so just return a normal long
	}

	// --- [ array

	public byte[] readNibbleArray() throws IOException {
		int length = readInt();
		byte[] array = new byte[length];

		int bytes = (length + 1) >> 1;

		for (int i = 0; i < bytes; i++) {
			int b = readUnsignedByte();

			array[i * 2] = (byte) ((b >> 4) & 0x0F);
			if ((array[i * 2] & 0x8) != 0) {
				array[i * 2] |= 0xF0; // correct sign
			}

			if ((i * 2 + 1) < length) {
				array[i * 2 + 1] = (byte) (b & 0x0F);
				if ((array[i * 2 + 1] & 0x8) != 0) {
					array[i * 2 + 1] |= 0xF0;  // correct sign
				}
			}
		}

		return array;
	}

	public byte[] readUnsignedNibbleArray() throws IOException {
		int length = readInt();
		byte[] array = new byte[length];

		int bytes = (length + 1) >> 1;

		for (int i = 0; i < bytes; i++) {
			int b = readUnsignedByte();

			array[i * 2] = (byte) ((b >> 4) & 0x0F);

			if ((i * 2 + 1) < length) {
				array[i * 2 + 1] = (byte) (b & 0x0F);
			}
		}

		return array;
	}

	public byte[] readByteArray() throws IOException {
		int length = readInt();
		byte[] array = new byte[length];

		for (int i = 0; i < array.length; i++) {
			array[i] = readByte();
		}

		return array;
	}

	public short[] readUnsignedByteArray() throws IOException {
		int length = readInt();
		short[] array = new short[length];

		for (int b = 0; b < 1; b++) {
			for (int i = 0; i < array.length; i++) {
				array[i] <<= 8;
				array[i] |= (short) read();
			}
		}

		return array;
	}

	public short[] readShortArray() throws IOException {
		int length = readInt();
		short[] array = new short[length];

		for (int b = 0; b < 2; b++) {
			for (int i = 0; i < array.length; i++) {
				array[i] <<= 8;
				array[i] |= (short) read();
			}
		}

		return array;
	}

	public int[] readUnsignedShortArray() throws IOException {
		int length = readInt();
		int[] array = new int[length];

		for (int b = 0; b < 2; b++) {
			for (int i = 0; i < array.length; i++) {
				array[i] <<= 8;
				array[i] |= read();
			}
		}

		return array;
	}

	public int[] readIntArray() throws IOException {
		int length = readInt();
		int[] array = new int[length];

		for (int b = 0; b < 4; b++) {
			for (int i = 0; i < array.length; i++) {
				array[i] <<= 8;
				array[i] |= read();
			}
		}

		return array;
	}

	public long[] readUnsignedIntArray() throws IOException {
		int length = readInt();
		long[] array = new long[length];

		for (int b = 0; b < 4; b++) {
			for (int i = 0; i < array.length; i++) {
				array[i] <<= 8;
				array[i] |= (long) read();
			}
		}

		return array;
	}

	public long[] readLongArray() throws IOException {
		int length = readInt();
		long[] array = new long[length];

		for (int b = 0; b < 8; b++) {
			for (int i = 0; i < array.length; i++) {
				array[i] <<= 8;
				array[i] |= (long) read();
			}
		}

		return array;
	}

	public long[] readUnsignedLongArray() throws IOException {
		int length = readInt();
		long[] array = new long[length];

		for (int b = 0; b < 8; b++) {
			for (int i = 0; i < array.length; i++) {
				array[i] <<= 8;
				array[i] |= (long) read();
			}
		}

		return array;
	}

	public float[] readFloatArray() throws IOException {
		int length = readInt();
		float[] array = new float[length];

		for (int b = 0; b < 4; b++) {
			for (int i = 0; i < length; i++) {
				int raw = Float.floatToRawIntBits(array[i]);

				raw <<= 8;
				raw |= read();

				array[i] = Float.intBitsToFloat(raw);
			}
		}

		return array;
	}

	public double[] readDoubleArray() throws IOException {
		int length = readInt();
		double[] array = new double[length];

		for (int b = 0; b < 8; b++) {
			for (int i = 0; i < length; i++) {
				long raw = Double.doubleToRawLongBits(array[i]);

				raw <<= 8;
				raw |= (long) read();

				array[i] = Double.longBitsToDouble(raw);
			}
		}

		return array;
	}

	// string

	public String readASCIIString() throws IOException {
		return readUTF8String(false, true);
	}

	public String readASCIIString(int exactLength) throws IOException {
		return readUTF8String(exactLength, false);
	}

	public String readUTF8String(boolean readLength, boolean nullTerminated) throws IOException {
		if (readLength) {
			return readUTF8String(readInt(), nullTerminated);
		} else {
			return readUTF8String(-1, nullTerminated);
		}
	}

	public String readUTF8String(int exactLength, boolean nullTerminated) throws IOException {
		StringBuilder sb = new StringBuilder();

		if (exactLength < 0) {
			int codepoint = -1;

			while (codepoint != 0) {
				codepoint = readUTF8Codepoint();
				if (codepoint != 0) sb.appendCodePoint(codepoint);
			}
		} else {
			boolean endReached = false;
			for (int i = 0; i < exactLength; i++) {
				int codepoint = readUTF8Codepoint();
				if (codepoint != 0 && !endReached) {
					sb.appendCodePoint(codepoint);
				} else {
					endReached = true;
				}
			}

			if (nullTerminated) read();
		}

		return sb.toString();
	}

	public String[] readUTF8StringArray(boolean readLength, boolean nullTerminated) throws IOException {
		int length = readInt();
		String[] strings = new String[length];

		for (int i = 0; i < strings.length; i++) {
			strings[i] = readUTF8String(readLength, nullTerminated);
		}

		return strings;
	}

	public int readUTF8Codepoint() throws IOException {
		int codepoint;

		codepoint = readUnsignedByte();

		if ((codepoint & 0x80) != 0) {
			int bytes = 0;

			if ((codepoint & 0x20) == 0) {
				bytes = 1;
			} else if ((codepoint & 0x10) == 0) {
				bytes = 2;
			} else if ((codepoint & 0x08) == 0) {
				bytes = 3;
			}

			while (bytes > 0) {
				codepoint <<= 6;
				codepoint |= (readUnsignedByte() & 0x3F);
				bytes--;
			}

		}

		return codepoint;

	}

	// --- [ multi dim array

	// TODO implement this
}
