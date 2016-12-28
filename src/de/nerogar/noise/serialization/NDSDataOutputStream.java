package de.nerogar.noise.serialization;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class NDSDataOutputStream extends FilterOutputStream {

	private static final String ERROR_BYTE_INDEX_TOO_FAR = "input stream is ahead of target byte index";

	private long byteIndex = 0;

	public NDSDataOutputStream(OutputStream out) {
		super(out);
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

	private long skip(long n) {
		long skipped = 0;

		for (long i = 0; i < n; i++) {
			try {
				write(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		byteIndex += skipped;
		return skipped;
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b & 0xFF);
		byteIndex++;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);

		byteIndex += len;
	}

	// big int

	public void writeBigInt(BigInteger bigInt) throws IOException {
		byte[] byteArray = bigInt.toByteArray();

		if (byteArray.length > 0xFF) {
			throw new IOException(NDSConstants.ERROR_BIG_INTEGER_SIZE_LIMIT);
		}

		writeUnsignedByte((short) byteArray.length);
		for (byte b : byteArray) {
			writeByte(b);
		}
	}

	public void writeBigIntArray(BigInteger[] value) throws IOException {
		writeInt(value.length);

		for (BigInteger bigInt : value) {
			writeBigInt(bigInt);
		}
	}

	// boolean

	public void writeBoolean(boolean value) throws IOException {
		write(value ? 1 : 0);
	}

	public void writeBooleanArray(boolean[] value) throws IOException {
		writeInt(value.length);

		for (boolean b : value) {
			writeBoolean(b);
		}
	}

	// float

	public void writeFloat(float value) throws IOException {
		writeInt(Float.floatToRawIntBits(value));
	}

	public void writeDouble(double value) throws IOException {
		writeLong(Double.doubleToRawLongBits(value));
	}

	// signed

	public void writeNibble(byte value) throws IOException {
		write(value & 0xF);
	}

	public void writeByte(byte value) throws IOException {
		write(value);
	}

	public void writeShort(short value) throws IOException {
		write((value >> 8) & 0xFF);
		write((value >> 0) & 0xFF);
	}

	public void writeInt(int value) throws IOException {
		write((value >> 24) & 0xFF);
		write((value >> 16) & 0xFF);
		write((value >> 8) & 0xFF);
		write((value >> 0) & 0xFF);
	}

	public void writeLong(long value) throws IOException {
		write((int) (value >> 56) & 0xFF);
		write((int) (value >> 48) & 0xFF);
		write((int) (value >> 40) & 0xFF);
		write((int) (value >> 32) & 0xFF);
		write((int) (value >> 24) & 0xFF);
		write((int) (value >> 16) & 0xFF);
		write((int) (value >> 8) & 0xFF);
		write((int) (value >> 0) & 0xFF);
	}

	// unsigned

	public void writeUnsignedNibble(byte value) throws IOException {
		write(value & 0x0F);
	}

	public void writeUnsignedByte(short value) throws IOException {
		write(value);
	}

	public void writeUnsignedShort(int value) throws IOException {
		writeShort((short) value);
	}

	public void writeUnsignedInt(long value) throws IOException {
		writeInt((int) value);
	}

	public void writeUnsignedLong(long value) throws IOException {
		writeLong(value);
	}

	// --- [ array

	public void writeNibbleArray(byte[] value) throws IOException {
		writeInt(value.length);

		int bytes = (value.length + 1) >> 1;

		for (int i = 0; i < bytes; i++) {
			int b = (value[i * 2] & 0x0F);
			b <<= 4;

			if ((i * 2 + 1) < value.length) {
				b |= (value[i * 2 + 1] & 0x0F);
			}

			write(b);
		}
	}

	public void writeUnsignedNibbleArray(byte[] value) throws IOException {
		writeNibbleArray(value);
	}

	public void writeByteArray(byte[] value) throws IOException {
		writeInt(value.length);

		for (byte aValue : value) {
			writeByte(aValue);
		}
	}

	public void writeUnsignedByteArray(short[] value) throws IOException {
		writeInt(value.length);

		for (short aValue : value) {
			write(aValue);
		}
	}

	public void writeShortArray(short[] value) throws IOException {
		writeInt(value.length);

		for (int b = 8; b >= 0; b -= 8) {
			for (short aValue : value) {
				write(aValue >> b);
			}
		}
	}

	public void writeUnsignedShortArray(int[] value) throws IOException {
		writeInt(value.length);

		for (int b = 8; b >= 0; b -= 8) {
			for (int aValue : value) {
				write(aValue >> b);
			}
		}
	}

	public void writeIntArray(int[] value) throws IOException {
		writeInt(value.length);

		for (int b = 24; b >= 0; b -= 8) {
			for (int aValue : value) {
				write(aValue >> b);
			}
		}
	}

	public void writeUnsignedIntArray(long[] value) throws IOException {
		writeInt(value.length);

		for (int b = 24; b >= 0; b -= 8) {
			for (long aValue : value) {
				write((int) (aValue >> b));
			}
		}
	}

	public void writeLongArray(long[] value) throws IOException {
		writeInt(value.length);

		for (int b = 56; b >= 0; b -= 8) {
			for (long aValue : value) {
				write((int) (aValue >> b));
			}
		}
	}

	public void writeUnsignedLongArray(long[] value) throws IOException {
		writeLongArray(value);
	}

	public void writeFloatArray(float[] value) throws IOException {
		writeInt(value.length);

		for (int b = 24; b >= 0; b -= 8) {
			for (float aValue : value) {
				int raw = Float.floatToRawIntBits(aValue);
				write(raw >> b);
			}
		}
	}

	public void writeDoubleArray(double[] value) throws IOException {
		writeInt(value.length);

		for (int b = 56; b >= 0; b -= 8) {
			for (double aValue : value) {
				long raw = Double.doubleToRawLongBits(aValue);
				write((int) (raw >> b ));
			}
		}
	}

	// string

	public void writeASCIIString(String string, boolean nullTerminated) throws IOException {

		for (int i = 0; i < string.length(); i++) {
			int c = string.codePointAt(i);

			if ((c & 0x80) == 0) {
				write(c);
			}
		}

		if (nullTerminated) {
			write(0);
		}
	}

	public void writeUTF8String(String string, boolean includeLength, boolean nullTerminated) throws IOException {
		if (includeLength) {
			int codepoints = string.codePointCount(0, string.length());
			writeInt(codepoints);
		}

		for (int offset = 0; offset < string.length(); ) {
			int codepoint = string.codePointAt(offset);

			writeUTF8Codepoint(codepoint);

			offset += Character.charCount(codepoint);
		}

		if (nullTerminated) {
			write(0);
		}
	}

	public void writeUTF8StringArray(String[] value, boolean includeLength, boolean nullTerminated) throws IOException {
		writeInt(value.length);

		for (String s : value) {
			writeUTF8String(s, includeLength, nullTerminated);
		}
	}

	public void writeUTF8Codepoint(int codepoint) throws IOException {
		if ((codepoint & ~0x7F) == 0) {
			write(codepoint);
		} else if ((codepoint & ~0x7FF) == 0) {
			write(0xC0 | ((codepoint >> 6) & 0x1F));
			write(0x80 | (codepoint & 0x3F));
		} else if ((codepoint & ~0xFFFF) == 0) {
			write(0xE0 | ((codepoint >> 12) & 0x0F));
			write(0x80 | ((codepoint >> 6) & 0x3F));
			write(0x80 | (codepoint & 0x3F));
		} else if ((codepoint & ~0x1FFFFF) == 0) {
			write(0xF0 | ((codepoint >> 28) & 0x07));
			write(0x80 | ((codepoint >> 12) & 0x3F));
			write(0x80 | ((codepoint >> 6) & 0x3F));
			write(0x80 | (codepoint & 0x3F));
		}
	}

	// --- [ multi dim array

	private byte getDepth(Object[] array) {
		byte dim = 2;

		while (array instanceof Object[][]) {
			dim++;
			array = (Object[]) array[0];
		}

		return dim;
	}

	public void writeNibbleMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeNibbleMultiDimArrayInner(array, depth);
	}

	public void writeUnsignedNibbleMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeUnsignedNibbleMultiDimArrayInner(array, depth);
	}

	public void writeByteMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeByteMultiDimArrayInner(array, depth);
	}

	public void writeUnsignedByteMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeUnsignedByteMultiDimArrayInner(array, depth);
	}

	public void writeShortMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeShortMultiDimArrayInner(array, depth);
	}

	public void writeUnsignedShortMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeUnsignedShortMultiDimArrayInner(array, depth);
	}

	public void writeIntMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeIntMultiDimArrayInner(array, depth);
	}

	public void writeUnsignedIntMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeUnsignedIntMultiDimArrayInner(array, depth);
	}

	public void writeLongMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeLongMultiDimArrayInner(array, depth);
	}

	public void writeUnsignedLongMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeUnsignedLongMultiDimArrayInner(array, depth);
	}

	public void writeFloatMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeFloatMultiDimArrayInner(array, depth);
	}

	public void writeDoubleMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeDoubleMultiDimArrayInner(array, depth);
	}

	public void writeUTF8StringMultiDimArray(Object[] array) throws IOException {
		byte depth = getDepth(array);
		writeByte(depth);
		writeUTF8StringMultiDimArrayInner(array, depth);
	}

	private void writeNibbleMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeNibbleMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeNibbleArray((byte[]) array);
		}
	}

	private void writeUnsignedNibbleMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeUnsignedNibbleMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeUnsignedNibbleArray((byte[]) array);
		}
	}

	private void writeByteMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeByteMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeByteArray((byte[]) array);
		}
	}

	private void writeUnsignedByteMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeUnsignedByteMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeUnsignedByteArray((short[]) array);
		}
	}

	private void writeShortMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeShortMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeShortArray((short[]) array);
		}
	}

	private void writeUnsignedShortMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeUnsignedShortMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeUnsignedShortArray((int[]) array);
		}
	}

	private void writeIntMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeIntMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeIntArray((int[]) array);
		}
	}

	private void writeUnsignedIntMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeUnsignedIntMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeUnsignedIntArray((long[]) array);
		}
	}

	private void writeLongMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeLongMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeLongArray((long[]) array);
		}
	}

	private void writeUnsignedLongMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeUnsignedLongMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeUnsignedLongArray((long[]) array);
		}
	}

	private void writeFloatMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeFloatMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeFloatArray((float[]) array);
		}
	}

	private void writeDoubleMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeDoubleMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeDoubleArray((double[]) array);
		}
	}

	private void writeUTF8StringMultiDimArrayInner(Object array, int depth) throws IOException {
		if (depth > 1) {
			Object[] oArray = (Object[]) array;
			writeInt(oArray.length);
			for (Object o : oArray) {
				writeUTF8StringMultiDimArrayInner(o, depth - 1);
			}
		} else {
			writeUTF8StringArray((String[]) array, true, true);
		}
	}

}
