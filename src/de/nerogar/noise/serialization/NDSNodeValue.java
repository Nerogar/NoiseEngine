package de.nerogar.noise.serialization;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.math.BigInteger;

import static de.nerogar.noise.serialization.NDSConstants.*;

public abstract class NDSNodeValue extends NDSNode {

	protected Object value;

	public NDSNodeValue(String name) {
		super(name);
	}

	// --- [ single

	// nibble
	public void setNibble(byte value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public byte getNibble() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedNibble
	public void setUnsignedNibble(byte value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public byte getUnsignedNibble() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// byte
	public void setByte(byte value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public byte getByte() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedByte
	public void setUnsignedByte(short value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public short getUnsignedByte() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// short
	public void setShort(short value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public short getShort() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedShort
	public void setUnsignedShort(int value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public int getUnsignedShort() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// int
	public void setInt(int value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public int getInt() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedInt
	public void setUnsignedInt(long value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public long getUnsignedInt() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// long
	public void setLong(long value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public long getLong() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedLong
	public void setUnsignedLong(long value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public long getUnsignedLong() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// float
	public void setFloat(float value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public float getFloat() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// double
	public void setDouble(double value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public double getDouble() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// bigInt
	public void setBigInt(BigInteger value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public BigInteger getBigInt() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// boolean
	public void setBoolean(boolean value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public boolean getBoolean() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// string
	public void setStringUTF8(String value) {
		throw new NDSException(ERROR_WRONG_DATA_TYPE);
	}

	public String getStringUTF8() {
		throw new NDSException(ERROR_WRONG_DATA_TYPE);
	}

	// --- [ array

	// nibble
	public void setNibbleArray(byte[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public byte[] getNibbleArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedNibble
	public void setUnsignedNibbleArray(byte[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public byte[] getUnsignedNibbleArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// byte
	public void setByteArray(byte[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public byte[] getByteArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedByte
	public void setUnsignedByteArray(short[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public short[] getUnsignedByteArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// short
	public void setShortArray(short[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public short[] getShortArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedShort
	public void setUnsignedShortArray(int[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public int[] getUnsignedShortArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// int
	public void setIntArray(int[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public int[] getIntArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedInt
	public void setUnsignedIntArray(long[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public long[] getUnsignedIntArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// long
	public void setLongArray(long[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public long[] getLongArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// unsignedLong
	public void setUnsignedLongArray(long[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public long[] getUnsignedLongArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// float
	public void setFloatArray(float[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public float[] getFloatArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// double
	public void setDoubleArray(double[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public double[] getDoubleArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// bigInt
	public void setBigIntArray(BigInteger[] value) {
		this.type = ARRAY_VALUE | BIG_INT;
		this.value = value;
	}

	public BigInteger[] getBigIntArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// boolean
	public void setBooleanArray(boolean[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public boolean[] getBooleanArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// string
	public void setStringUTF8Array(String[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public String[] getStringUTF8Array() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// object
	public void setObjectArray(NDSNodeObject[] value) {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	public NDSNodeObject[] getObjectArray() {
		throw new NDSException(ERROR_WRONG_NODE_TYPE);
	}

	// --- [ write

	protected abstract void writeSingle(NDSDataOutputStream out, NDSNodeRoot root) throws IOException;

	protected abstract void writeArray(NDSDataOutputStream out, NDSNodeRoot root) throws IOException;

	protected final void writeMultiDimArray(NDSDataOutputStream out, NDSNodeRoot root) throws IOException {
		// TODO write multi dim array
		throw new NotImplementedException();
	}

	@Override
	protected void writeContent(NDSDataOutputStream out, NDSNodeRoot root) throws IOException {
		if (isSingle()) writeSingle(out, root);
		else if (isArray()) writeArray(out, root);
		else if (isMultiDimArray()) writeMultiDimArray(out, root);
	}

	// --- [ read

	protected abstract void readSingle(NDSDataInputStream in, NDSNodeRoot root) throws IOException;

	protected abstract void readArray(NDSDataInputStream in, NDSNodeRoot root) throws IOException;

	protected final void readMultiDimArray(NDSDataInputStream in, NDSNodeRoot root) throws IOException {
		// TODO read multi dim array
		throw new NotImplementedException();
	}

	@Override
	protected void readContent(NDSDataInputStream in, NDSNodeRoot root) throws IOException {
		if (isSingle()) readSingle(in, root);
		else if (isArray()) readArray(in, root);
		else if (isMultiDimArray()) readMultiDimArray(in, root);
	}

	// --- [ util

	public boolean isSingle() {
		return (type & ARRAY_MASK) == SINGLE_VALUE;
	}

	public boolean isArray() {
		return (type & ARRAY_MASK) == ARRAY_VALUE;
	}

	public boolean isMultiDimArray() {
		return (type & ARRAY_MASK) == MULTIDIM_ARRAY_VALUE;
	}

	protected int arrayLength() {
		if (value instanceof byte[]) {
			return ((byte[]) value).length;
		} else if (value instanceof short[]) {
			return ((short[]) value).length;
		} else if (value instanceof int[]) {
			return ((int[]) value).length;
		} else if (value instanceof long[]) {
			return ((long[]) value).length;
		} else if (value instanceof float[]) {
			return ((float[]) value).length;
		} else if (value instanceof double[]) {
			return ((double[]) value).length;
		} else if (value instanceof boolean[]) {
			return ((boolean[]) value).length;
		} else if (value instanceof char[]) {
			return ((char[]) value).length;
		} else if (value instanceof Object[]) {
			return ((Object[]) value).length;
		}

		throw new NDSException(ERROR_NOT_AN_ARRAY);
	}

	@Override
	public boolean isNull() {
		return value == null;
	}

	@Override
	protected boolean isRawNode() {
		return isArray();
	}

	@Override
	protected boolean hasRawNode() {
		return isRawNode();
	}

	@Override
	public String toString() {
		String ret0;
		String ret1;

		if (name == null) {
			ret0 = "";
		} else {
			ret0 = name + ": ";
		}

		if (value == null) {
			ret1 = "null";
		} else if (isArray()) {
			ret1 = NDSUtil.arrayToString(value);
		} else {
			ret1 = value.toString();
		}

		return ret0 + ret1;
	}

}
