package de.nerogar.noise.serialization;

import java.io.IOException;

import static de.nerogar.noise.serialization.NDSConstants.*;

/*package private*/ class NDSNodeInteger extends NDSNodeValue {

	public NDSNodeInteger(String name) {
		super(name);
	}

	// --- [ single

	// nibble
	@Override
	public void setNibble(byte value) {
		this.type = SINGLE_VALUE | INTEGER_SIGNED | LENGTH_4;
		this.value = value;
	}

	@Override
	public byte getNibble() {
		if (type == (SINGLE_VALUE | INTEGER_SIGNED | LENGTH_4)) {
			return (byte) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedNibble
	@Override
	public void setUnsignedNibble(byte value) {
		this.type = SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_4;
		this.value = value;
	}

	@Override
	public byte getUnsignedNibble() {
		if (type == (SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_4)) {
			return (byte) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// byte
	@Override
	public void setByte(byte value) {
		this.type = SINGLE_VALUE | INTEGER_SIGNED | LENGTH_8;
		this.value = value;
	}

	@Override
	public byte getByte() {
		if (type == (SINGLE_VALUE | INTEGER_SIGNED | LENGTH_8)) {
			return (byte) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedByte
	@Override
	public void setUnsignedByte(short value) {
		this.type = SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_8;
		this.value = value;
	}

	@Override
	public short getUnsignedByte() {
		if (type == (SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_8)) {
			return (short) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// short
	@Override
	public void setShort(short value) {
		this.type = SINGLE_VALUE | INTEGER_SIGNED | LENGTH_16;
		this.value = value;
	}

	@Override
	public short getShort() {
		if (type == (SINGLE_VALUE | INTEGER_SIGNED | LENGTH_16)) {
			return (short) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedShort
	@Override
	public void setUnsignedShort(int value) {
		this.type = SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_16;
		this.value = value;
	}

	@Override
	public int getUnsignedShort() {
		if (type == (SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_16)) {
			return (int) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// int
	@Override
	public void setInt(int value) {
		this.type = SINGLE_VALUE | INTEGER_SIGNED | LENGTH_32;
		this.value = value;
	}

	@Override
	public int getInt() {
		if (type == (SINGLE_VALUE | INTEGER_SIGNED | LENGTH_32)) {
			return (int) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedInt
	@Override
	public void setUnsignedInt(long value) {
		this.type = SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_32;
		this.value = value;
	}

	@Override
	public long getUnsignedInt() {
		if (type == (SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_32)) {
			return (long) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// long
	@Override
	public void setLong(long value) {
		this.type = SINGLE_VALUE | INTEGER_SIGNED | LENGTH_64;
		this.value = value;
	}

	@Override
	public long getLong() {
		if (type == (SINGLE_VALUE | INTEGER_SIGNED | LENGTH_64)) {
			return (long) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedLong
	@Override
	public void setUnsignedLong(long value) {
		this.type = SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_64;
		this.value = value;
	}

	@Override
	public long getUnsignedLong() {
		if (type == (SINGLE_VALUE | INTEGER_UNSIGNED | LENGTH_64)) {
			return (long) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// --- [ array

	// nibble
	@Override
	public void setNibbleArray(byte[] value) {
		this.type = ARRAY_VALUE | INTEGER_SIGNED | LENGTH_4;
		this.value = value;
	}

	@Override
	public byte[] getNibbleArray() {
		if (type == (ARRAY_VALUE | INTEGER_SIGNED | LENGTH_4)) {
			return (byte[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedNibble
	@Override
	public void setUnsignedNibbleArray(byte[] value) {
		this.type = ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_4;
		this.value = value;
	}

	@Override
	public byte[] getUnsignedNibbleArray() {
		if (type == (ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_4)) {
			return (byte[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// byte
	@Override
	public void setByteArray(byte[] value) {
		this.type = ARRAY_VALUE | INTEGER_SIGNED | LENGTH_8;
		this.value = value;
	}

	@Override
	public byte[] getByteArray() {
		if (type == (ARRAY_VALUE | INTEGER_SIGNED | LENGTH_8)) {
			return (byte[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedByte
	@Override
	public void setUnsignedByteArray(short[] value) {
		this.type = ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_8;
		this.value = value;
	}

	@Override
	public short[] getUnsignedByteArray() {
		if (type == (ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_8)) {
			return (short[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// short
	@Override
	public void setShortArray(short[] value) {
		this.type = ARRAY_VALUE | INTEGER_SIGNED | LENGTH_16;
		this.value = value;
	}

	@Override
	public short[] getShortArray() {
		if (type == (ARRAY_VALUE | INTEGER_SIGNED | LENGTH_16)) {
			return (short[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedShort
	@Override
	public void setUnsignedShortArray(int[] value) {
		this.type = ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_16;
		this.value = value;
	}

	@Override
	public int[] getUnsignedShortArray() {
		if (type == (ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_16)) {
			return (int[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// int
	@Override
	public void setIntArray(int[] value) {
		this.type = ARRAY_VALUE | INTEGER_SIGNED | LENGTH_32;
		this.value = value;
	}

	@Override
	public int[] getIntArray() {
		if (type == (ARRAY_VALUE | INTEGER_SIGNED | LENGTH_32)) {
			return (int[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedInt
	@Override
	public void setUnsignedIntArray(long[] value) {
		this.type = ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_32;
		this.value = value;
	}

	@Override
	public long[] getUnsignedIntArray() {
		if (type == (ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_32)) {
			return (long[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// long
	@Override
	public void setLongArray(long[] value) {
		this.type = ARRAY_VALUE | INTEGER_SIGNED | LENGTH_64;
		this.value = value;
	}

	@Override
	public long[] getLongArray() {
		if (type == (ARRAY_VALUE | INTEGER_SIGNED | LENGTH_64)) {
			return (long[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// unsignedLong
	@Override
	public void setUnsignedLongArray(long[] value) {
		this.type = ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_64;
		this.value = value;
	}

	@Override
	public long[] getUnsignedLongArray() {
		if (type == (ARRAY_VALUE | INTEGER_UNSIGNED | LENGTH_64)) {
			return (long[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// --- [ write

	@Override
	protected void writeSingle(NDSDataOutputStream out, NDSNodeRoot root) throws IOException {
		if ((type & INTEGER_SIGN_MASK) == INTEGER_SIGNED) {
			switch (type & LENGTH_MASK) {
				case LENGTH_4:
					out.writeNibble(getNibble());
					break;
				case LENGTH_8:
					out.writeByte(getByte());
					break;
				case LENGTH_16:
					out.writeShort(getShort());
					break;
				case LENGTH_32:
					out.writeInt(getInt());
					break;
				case LENGTH_64:
					out.writeLong(getLong());
					break;
				default:
					throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
			}
		} else {
			switch (type & LENGTH_MASK) {
				case LENGTH_4:
					out.writeUnsignedNibble(getUnsignedNibble());
					break;
				case LENGTH_8:
					out.writeUnsignedByte(getUnsignedByte());
					break;
				case LENGTH_16:
					out.writeUnsignedShort(getUnsignedShort());
					break;
				case LENGTH_32:
					out.writeUnsignedInt(getUnsignedInt());
					break;
				case LENGTH_64:
					out.writeUnsignedLong(getUnsignedLong());
					break;
				default:
					throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
			}
		}
	}

	@Override
	protected void writeArray(NDSDataOutputStream out, NDSNodeRoot root) throws IOException {
		if ((type & INTEGER_SIGN_MASK) == INTEGER_SIGNED) {
			switch (type & LENGTH_MASK) {
				case LENGTH_4:
					out.writeNibbleArray(getNibbleArray());
					break;
				case LENGTH_8:
					out.writeByteArray(getByteArray());
					break;
				case LENGTH_16:
					out.writeShortArray(getShortArray());
					break;
				case LENGTH_32:
					out.writeIntArray(getIntArray());
					break;
				case LENGTH_64:
					out.writeLongArray(getLongArray());
					break;
				default:
					throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
			}
		} else {
			switch (type & LENGTH_MASK) {
				case LENGTH_4:
					out.writeUnsignedNibbleArray(getUnsignedNibbleArray());
					break;
				case LENGTH_8:
					out.writeUnsignedByteArray(getUnsignedByteArray());
					break;
				case LENGTH_16:
					out.writeUnsignedShortArray(getUnsignedShortArray());
					break;
				case LENGTH_32:
					out.writeUnsignedIntArray(getUnsignedIntArray());
					break;
				case LENGTH_64:
					out.writeUnsignedLongArray(getUnsignedLongArray());
					break;
				default:
					throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
			}
		}
	}

	// --- [ read

	@Override
	protected void readSingle(NDSDataInputStream in, NDSNodeRoot root) throws IOException {
		if ((type & INTEGER_SIGN_MASK) == INTEGER_SIGNED) {
			switch (type & LENGTH_MASK) {
				case LENGTH_4:
					setNibble(in.readNibble());
					break;
				case LENGTH_8:
					setByte(in.readByte());
					break;
				case LENGTH_16:
					setShort(in.readShort());
					break;
				case LENGTH_32:
					setInt(in.readInt());
					break;
				case LENGTH_64:
					setLong(in.readLong());
					break;
				default:
					throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
			}
		} else {
			switch (type & LENGTH_MASK) {
				case LENGTH_4:
					setUnsignedNibble(in.readUnsignedNibble());
					break;
				case LENGTH_8:
					setUnsignedByte(in.readUnsignedByte());
					break;
				case LENGTH_16:
					setUnsignedShort(in.readUnsignedShort());
					break;
				case LENGTH_32:
					setUnsignedInt(in.readUnsignedInt());
					break;
				case LENGTH_64:
					setUnsignedLong(in.readUnsignedLong());
					break;
				default:
					throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
			}
		}
	}

	@Override
	protected void readArray(NDSDataInputStream in, NDSNodeRoot root) throws IOException {
		if ((type & INTEGER_SIGN_MASK) == INTEGER_SIGNED) {
			switch (type & LENGTH_MASK) {
				case LENGTH_4:
					setNibbleArray(in.readNibbleArray());
					break;
				case LENGTH_8:
					setByteArray(in.readByteArray());
					break;
				case LENGTH_16:
					setShortArray(in.readShortArray());
					break;
				case LENGTH_32:
					setIntArray(in.readIntArray());
					break;
				case LENGTH_64:
					setLongArray(in.readLongArray());
					break;
				default:
					throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
			}
		} else {
			switch (type & LENGTH_MASK) {
				case LENGTH_4:
					setUnsignedNibbleArray(in.readUnsignedNibbleArray());
					break;
				case LENGTH_8:
					setUnsignedByteArray(in.readUnsignedByteArray());
					break;
				case LENGTH_16:
					setUnsignedShortArray(in.readUnsignedShortArray());
					break;
				case LENGTH_32:
					setUnsignedIntArray(in.readUnsignedIntArray());
					break;
				case LENGTH_64:
					setUnsignedLongArray(in.readUnsignedLongArray());
					break;
				default:
					throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
			}
		}
	}

	// --- [ util

	@Override
	protected long getContentSizeInBytes() {
		long elementLength = NDSUtil.getLengthInBits(type);
		long arrayBytes = (elementLength * arrayLength() + 7L) / 8L;
		return Integer.BYTES + arrayBytes; // element count + array
	}

}
