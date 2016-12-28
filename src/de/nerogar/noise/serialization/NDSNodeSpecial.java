package de.nerogar.noise.serialization;

import java.io.IOException;
import java.math.BigInteger;

import static de.nerogar.noise.serialization.NDSConstants.*;

public class NDSNodeSpecial extends NDSNodeValue {

	public NDSNodeSpecial(String name) {
		super(name);
	}

	// --- [ single

	@Override
	public void setBigInt(BigInteger value) {
		this.type = SINGLE_VALUE | BIG_INT;
		this.value = value;
	}

	@Override
	public BigInteger getBigInt() {
		if (type == (SINGLE_VALUE | BIG_INT)) {
			return (BigInteger) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	@Override
	public void setBoolean(boolean value) {
		this.type = SINGLE_VALUE | BOOLEAN;
		this.value = value;
	}

	@Override
	public boolean getBoolean() {
		if (type == (SINGLE_VALUE | BOOLEAN)) {
			return (boolean) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	@Override
	public void setStringUTF8(String value) {
		this.type = SINGLE_VALUE | STRING_UTF8;
		this.value = value;
	}

	@Override
	public String getStringUTF8() {
		if (type == (SINGLE_VALUE | STRING_UTF8)) {
			return (String) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// --- [ array

	@Override
	public void setBigIntArray(BigInteger[] value) {
		this.type = ARRAY_VALUE | BIG_INT;
		this.value = value;
	}

	@Override
	public BigInteger[] getBigIntArray() {
		if (type == (ARRAY_VALUE | BIG_INT)) {
			return (BigInteger[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	@Override
	public void setBooleanArray(boolean[] value) {
		this.type = ARRAY_VALUE | BOOLEAN;
		this.value = value;
	}

	@Override
	public boolean[] getBooleanArray() {
		if (type == (ARRAY_VALUE | BOOLEAN)) {
			return (boolean[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	@Override
	public void setStringUTF8Array(String[] value) {
		this.type = ARRAY_VALUE | STRING_UTF8;
		this.value = value;
	}

	@Override
	public String[] getStringUTF8Array() {
		if (type == (ARRAY_VALUE | STRING_UTF8)) {
			return (String[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	@Override
	public void setObjectArray(NDSNodeObject[] value) {
		this.type = ARRAY_VALUE | OBJECT;
		this.value = value;
	}

	@Override
	public NDSNodeObject[] getObjectArray() {
		if (type == (ARRAY_VALUE | OBJECT)) {
			return (NDSNodeObject[]) value;
		} else {
			throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// --- [ write

	@Override
	protected void writeSingle(NDSDataOutputStream out, NDSNodeRoot root) throws IOException {
		switch (type & (TYPE_MASK | TYPE_SUB_MASK)) {
			case BOOLEAN:
				out.writeBoolean(getBoolean());
				break;
			case BIG_INT:
				out.writeBigInt(getBigInt());
				break;
			case STRING_UTF8:
				out.writeUTF8String(getStringUTF8(), true, true);
				break;
			default:
				throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	@Override
	protected void writeArray(NDSDataOutputStream out, NDSNodeRoot root) throws IOException {
		switch (type & (TYPE_MASK | TYPE_SUB_MASK)) {
			case BOOLEAN:
				out.writeBooleanArray(getBooleanArray());
				break;
			case BIG_INT:
				out.writeBigIntArray(getBigIntArray());
				break;
			case STRING_UTF8:
				out.writeUTF8StringArray(getStringUTF8Array(), true, true);
				break;
			case OBJECT:
				NDSNodeObject[] array = getObjectArray();
				if (isNull()) {
					out.writeInt(-1);
				} else {
					out.writeInt(array.length);
					for (NDSNodeObject ndsNodeObject : array) {
						ndsNodeObject.writeTree(out, root, false);
					}
				}
				break;

			default:
				throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// --- [ read

	@Override
	protected void readSingle(NDSDataInputStream in, NDSNodeRoot root) throws IOException {
		switch (type & (TYPE_MASK | TYPE_SUB_MASK)) {
			case BOOLEAN:
				setBoolean(in.readBoolean());
				break;
			case BIG_INT:
				setBigInt(in.readBigInt());
				break;
			case STRING_UTF8:
				setStringUTF8(in.readUTF8String(true, true));
				break;
			default:
				throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	@Override
	protected void readArray(NDSDataInputStream in, NDSNodeRoot root) throws IOException {
		switch (type & (TYPE_MASK | TYPE_SUB_MASK)) {
			case BOOLEAN:
				setBooleanArray(in.readBooleanArray());
				break;
			case BIG_INT:
				setBigIntArray(in.readBigIntArray());
				break;
			case STRING_UTF8:
				setStringUTF8Array(in.readUTF8StringArray(true, true));
				break;
			case OBJECT:
				int length = in.readInt();
				NDSNodeObject[] array = null;

				if (length >= 0) {
					array = new NDSNodeObject[length];
					for (int i = 0; i < length; i++) {
						// don't use any name, array objects are anonymous
						NDSNodeObject ndsNodeObject = new NDSNodeObject(null);
						ndsNodeObject.readTree(in, root, false);
						array[i] = ndsNodeObject;
					}
				}

				setObjectArray(array);

				break;
			default:
				throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}
	}

	// --- [ util

	private long utf8ByteLength(String s) {
		final long[] bytes = { 0 };

		s.codePoints().forEach((codepoint) -> {
			if ((codepoint & ~0x7F) == 0) {
				bytes[0] += 1;
			} else if ((codepoint & ~0x7FF) == 0) {
				bytes[0] += 2;
			} else if ((codepoint & ~0xFFFF) == 0) {
				bytes[0] += 3;
			} else if ((codepoint & ~0x1FFFFF) == 0) {
				bytes[0] += 4;
			}
		});

		return bytes[0];
	}

	@Override
	protected boolean isRawNode() {
		return super.isRawNode() && ((type & TYPE_SUB_MASK) != (OBJECT & TYPE_SUB_MASK));
	}

	@Override
	protected boolean hasRawNode() {
		if (type == (ARRAY_VALUE | OBJECT)) {
			for (NDSNodeObject ndsNodeObject : getObjectArray()) {
				if (ndsNodeObject.hasRawNode()) return true;
			}
			return isRawNode();
		} else {
			return super.hasRawNode();
		}
	}

	@Override
	protected long getContentSizeInBytes() {
		long length = Integer.BYTES; // length of the element count

		switch (type & (TYPE_MASK | TYPE_SUB_MASK)) {
			case BOOLEAN:
				length += getBooleanArray().length;
				break;
			case BIG_INT:
				for (BigInteger bigInt : getBigIntArray()) {
					length += 1; // length
					length += bigInt.toByteArray().length;
				}
				break;
			case STRING_UTF8:
				for (String s : getStringUTF8Array()) {
					length += Integer.BYTES; // length
					length += utf8ByteLength(s);
					length += 1; // \0 terminator
				}
				break;
			case OBJECT:
				return 0; // object arrays are saved in the tree
			default:
				throw new NDSException(ERROR_WRONG_DATA_TYPE);
		}

		return length;
	}

}
