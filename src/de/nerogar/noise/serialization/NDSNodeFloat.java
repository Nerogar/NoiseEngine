package de.nerogar.noise.serialization;

import java.io.IOException;

import static de.nerogar.noise.serialization.NDSConstants.*;

/*package private*/ class NDSNodeFloat extends NDSNodeValue {

	public NDSNodeFloat(String name) {
		super(name);
	}

	// --- [ single

	// float
	@Override
	public void setFloat(float value) {
		this.type = SINGLE_VALUE | TYPE_FLOAT | LENGTH_32;
		this.value = value;
	}

	@Override
	public float getFloat() {
		if (type == (SINGLE_VALUE | TYPE_FLOAT | LENGTH_32)) {
			return (float) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// double
	@Override
	public void setDouble(double value) {
		this.type = SINGLE_VALUE | TYPE_FLOAT | LENGTH_64;
		this.value = value;
	}

	@Override
	public double getDouble() {
		if (type == (SINGLE_VALUE | TYPE_FLOAT | LENGTH_64)) {
			return (double) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// --- [ array

	// float
	@Override
	public void setFloatArray(float[] value) {
		this.type = ARRAY_VALUE | TYPE_FLOAT | LENGTH_32;
		this.value = value;
	}

	@Override
	public float[] getFloatArray() {
		if (type == (ARRAY_VALUE | TYPE_FLOAT | LENGTH_32)) {
			return (float[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// double
	@Override
	public void setDoubleArray(double[] value) {
		this.type = ARRAY_VALUE | TYPE_FLOAT | LENGTH_64;
		this.value = value;
	}

	@Override
	public double[] getDoubleArray() {
		if (type == (ARRAY_VALUE | TYPE_FLOAT | LENGTH_64)) {
			return (double[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// --- [ write

	@Override
	protected void writeSingle(NDSDataOutputStream out, NDSNodeRoot root) throws IOException {
		switch (type & LENGTH_MASK) {
			case LENGTH_32:
				out.writeFloat(getFloat());
				break;
			case LENGTH_64:
				out.writeDouble(getDouble());
				break;
			default:
				throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
		}
	}

	@Override
	protected void writeArray(NDSDataOutputStream out, NDSNodeRoot root) throws IOException {
		switch (type & LENGTH_MASK) {
			case LENGTH_32:
				out.writeFloatArray(getFloatArray());
				break;
			case LENGTH_64:
				out.writeDoubleArray(getDoubleArray());
				break;
			default:
				throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
		}
	}

	// --- [ read

	@Override
	protected void readSingle(NDSDataInputStream in, NDSNodeRoot root) throws IOException {
		switch (type & LENGTH_MASK) {
			case LENGTH_32:
				setFloat(in.readFloat());
				break;
			case LENGTH_64:
				setDouble(in.readDouble());
				break;
			default:
				throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
		}
	}

	@Override
	protected void readArray(NDSDataInputStream in, NDSNodeRoot root) throws IOException {
		switch (type & LENGTH_MASK) {
			case LENGTH_32:
				setFloatArray(in.readFloatArray());
				break;
			case LENGTH_64:
				setDoubleArray(in.readDoubleArray());
				break;
			default:
				throw new NDSException(ERROR_UNSUPPORTED_LENGTH);
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

