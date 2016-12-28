package de.nerogar.noise.serialization;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static de.nerogar.noise.serialization.NDSConstants.*;

public class NDSNodeObject extends NDSNode {

	private Map<String, NDSNode> children;
	private boolean              isNull;

	public NDSNodeObject(String name) {
		this(name, false);
	}

	public NDSNodeObject(String name, boolean isNull) {
		super(name);

		this.isNull = isNull;

		children = new HashMap<>();
		type = SINGLE_VALUE | OBJECT;
	}

	private void addChild(NDSNode child) {
		children.put(child.getName(), child);
	}

	protected void removeChild(String name) {
		children.remove(name);
	}

	protected NDSNodeValue getChildValue(String name) {
		return (NDSNodeValue) children.get(name);
	}

	protected NDSNodeObject getChildObject(String name) {
		return (NDSNodeObject) children.get(name);
	}

	// values

	// --- [ single

	// nibble
	public void addNibble(String childName, byte value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setNibble(value);
		addChild(c);
	}

	public byte getNibble(String childName) {
		return getChildValue(childName).getNibble();
	}

	// unsignedNibble
	public void addUnsignedNibble(String childName, byte value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedNibble(value);
		addChild(c);
	}

	public byte getUnsignedNibble(String childName) {
		return getChildValue(childName).getUnsignedNibble();
	}

	// byte
	public void addByte(String childName, byte value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setByte(value);
		addChild(c);
	}

	public byte getByte(String childName) {
		return getChildValue(childName).getByte();
	}

	// unsignedByte
	public void addUnsignedByte(String childName, short value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedByte(value);
		addChild(c);
	}

	public short getUnsignedByte(String childName) {
		return getChildValue(childName).getUnsignedByte();
	}

	// short
	public void addShort(String childName, short value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setShort(value);
		addChild(c);
	}

	public short getShort(String childName) {
		return getChildValue(childName).getShort();
	}

	// unsignedShort
	public void addUnsignedShort(String childName, int value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedShort(value);
		addChild(c);
	}

	public int getUnsignedShort(String childName) {
		return getChildValue(childName).getUnsignedShort();
	}

	// int
	public void addInt(String childName, int value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setInt(value);
		addChild(c);
	}

	public int getInt(String childName) {
		return getChildValue(childName).getInt();
	}

	// unsignedInt
	public void addUnsignedInt(String childName, long value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedInt(value);
		addChild(c);
	}

	public long getUnsignedInt(String childName) {
		return getChildValue(childName).getUnsignedInt();
	}

	// long
	public void addLong(String childName, long value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setLong(value);
		addChild(c);
	}

	public long getLong(String childName) {
		return getChildValue(childName).getLong();
	}

	// unsignedLong
	public void addUnsignedLong(String childName, long value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedLong(value);
		addChild(c);
	}

	public long getUnsignedLong(String childName) {
		return getChildValue(childName).getUnsignedLong();
	}

	// float
	public void addFloat(String childName, float value) {
		NDSNodeValue c = new NDSNodeFloat(childName);
		c.setFloat(value);
		addChild(c);
	}

	public float getFloat(String childName) {
		return getChildValue(childName).getFloat();
	}

	// double
	public void addDouble(String childName, double value) {
		NDSNodeValue c = new NDSNodeFloat(childName);
		c.setDouble(value);
		addChild(c);
	}

	public double getDouble(String childName) {
		return getChildValue(childName).getDouble();
	}

	// bigInt
	public void addBigInt(String childName, BigInteger value) {
		NDSNodeValue c = new NDSNodeSpecial(childName);
		c.setBigInt(value);
		addChild(c);
	}

	public BigInteger getBigInt(String childName) {
		return getChildValue(childName).getBigInt();
	}

	// boolean
	public void addBoolean(String childName, boolean value) {
		NDSNodeValue c = new NDSNodeSpecial(childName);
		c.setBoolean(value);
		addChild(c);
	}

	public boolean getBoolean(String childName) {
		return getChildValue(childName).getBoolean();
	}

	// string
	public void addStringUTF8(String childName, String value) {
		NDSNodeValue c = new NDSNodeSpecial(childName);
		c.setStringUTF8(value);
		addChild(c);
	}

	public String getStringUTF8(String childName) {
		return getChildValue(childName).getStringUTF8();
	}

	// --- [ array

	// nibble
	public void addNibbleArray(String childName, byte[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setNibbleArray(value);
		addChild(c);
	}

	public byte[] getNibbleArray(String childName) {
		return getChildValue(childName).getNibbleArray();
	}

	// unsignedNibble
	public void addUnsignedNibbleArray(String childName, byte[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedNibbleArray(value);
		addChild(c);
	}

	public byte[] getUnsignedNibbleArray(String childName) {
		return getChildValue(childName).getUnsignedNibbleArray();
	}

	// byte
	public void addByteArray(String childName, byte[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setByteArray(value);
		addChild(c);
	}

	public byte[] getByteArray(String childName) {
		return getChildValue(childName).getByteArray();
	}

	// unsignedByte
	public void addUnsignedByteArray(String childName, short[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedByteArray(value);
		addChild(c);
	}

	public short[] getUnsignedByteArray(String childName) {
		return getChildValue(childName).getUnsignedByteArray();
	}

	// short
	public void addShortArray(String childName, short[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setShortArray(value);
		addChild(c);
	}

	public short[] getShortArray(String childName) {
		return getChildValue(childName).getShortArray();
	}

	// unsignedShort
	public void addUnsignedShortArray(String childName, int[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedShortArray(value);
		addChild(c);
	}

	public int[] getUnsignedShortArray(String childName) {
		return getChildValue(childName).getUnsignedShortArray();
	}

	// int
	public void addIntArray(String childName, int[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setIntArray(value);
		addChild(c);
	}

	public int[] getIntArray(String childName) {
		return getChildValue(childName).getIntArray();
	}

	// unsignedInt
	public void addUnsignedIntArray(String childName, long[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedIntArray(value);
		addChild(c);
	}

	public long[] getUnsignedIntArray(String childName) {
		return getChildValue(childName).getUnsignedIntArray();
	}

	// long
	public void addLongArray(String childName, long[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setLongArray(value);
		addChild(c);
	}

	public long[] getLongArray(String childName) {
		return getChildValue(childName).getLongArray();
	}

	// unsignedLong
	public void addUnsignedLongArray(String childName, long[] value) {
		NDSNodeValue c = new NDSNodeInteger(childName);
		c.setUnsignedLongArray(value);
		addChild(c);
	}

	public long[] getUnsignedLongArray(String childName) {
		return getChildValue(childName).getUnsignedLongArray();
	}

	// float
	public void addFloatArray(String childName, float[] value) {
		NDSNodeValue c = new NDSNodeFloat(childName);
		c.setFloatArray(value);
		addChild(c);
	}

	public float[] getFloatArray(String childName) {
		return getChildValue(childName).getFloatArray();
	}

	// double
	public void addDoubleArray(String childName, double[] value) {
		NDSNodeValue c = new NDSNodeFloat(childName);
		c.setDoubleArray(value);
		addChild(c);
	}

	public double[] getDoubleArray(String childName) {
		return getChildValue(childName).getDoubleArray();
	}

	// bigInt
	public void addBigIntArray(String childName, BigInteger[] value) {
		NDSNodeValue c = new NDSNodeSpecial(childName);
		c.setBigIntArray(value);
		addChild(c);
	}

	public BigInteger[] getBigIntArray(String childName) {
		return getChildValue(childName).getBigIntArray();
	}

	// boolean
	public void addBooleanArray(String childName, boolean[] value) {
		NDSNodeValue c = new NDSNodeSpecial(childName);
		c.setBooleanArray(value);
		addChild(c);
	}

	public boolean[] getBooleanArray(String childName) {
		return getChildValue(childName).getBooleanArray();
	}

	// string
	public void addStringUTF8Array(String childName, String[] value) {
		NDSNodeValue c = new NDSNodeSpecial(childName);
		c.setStringUTF8Array(value);
		addChild(c);
	}

	public String[] getStringUTF8Array(String childName) {
		return getChildValue(childName).getStringUTF8Array();
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	// object

	public void addObject(NDSNodeObject node) {
		addChild(node);
	}

	public NDSNodeObject getObject(String childName) {
		return getChildObject(childName);
	}

	public void addObjectArray(String childName, NDSNodeObject[] value) {
		NDSNodeValue c = new NDSNodeSpecial(childName);
		c.setObjectArray(value);
		addChild(c);
	}

	public NDSNodeObject[] getObjectArray(String childName) {
		return getChildValue(childName).getObjectArray();
	}

	public Collection<NDSNode> getChildren() {
		return children.values();
	}

	public void removeNode(String name) {
		removeChild(name);
	}

	// --- [ write

	@Override
	protected void writeContent(NDSDataOutputStream out, NDSNodeRoot root) throws IOException {
		if (isNull()) {
			out.writeInt(-1);
		} else {
			out.writeInt(children.size());

			for (NDSNode c : children.values()) {
				c.writeTree(out, root, true);
			}
		}
	}

	// --- [ read

	@Override
	protected void readContent(NDSDataInputStream in, NDSNodeRoot root) throws IOException {
		int numChildren = in.readInt();

		if (numChildren == -1) {
			isNull = true;
			return;
		}

		for (int i = 0; i < numChildren; i++) {
			NDSNode cNode;
			int cType = in.readUnsignedByte();
			String cName = in.readASCIIString();

			if (cType == (SINGLE_VALUE | OBJECT)) {
				cNode = new NDSNodeObject(cName);
			} else if ((cType & TYPE_MASK) == TYPE_INTEGER) {
				cNode = new NDSNodeInteger(cName);
			} else if ((cType & TYPE_MASK) == TYPE_FLOAT) {
				cNode = new NDSNodeFloat(cName);
			} else if ((cType & TYPE_MASK) == TYPE_SPECIAL) {
				cNode = new NDSNodeSpecial(cName);
			} else {
				throw new NDSException(ERROR_UNKNOWN_TYPE);
			}

			cNode.type = cType;

			cNode.readTree(in, root, false);

			children.put(cName, cNode);
		}
	}

	// --- [ util

	@Override
	public boolean isNull() {
		return isNull;
	}

	@Override
	protected boolean isRawNode() {
		return false;
	}

	@Override
	protected boolean hasRawNode() {
		if (isRawNode()) return true;

		for (NDSNode c : children.values()) {
			if (c.hasRawNode()) return true;
		}
		return false;
	}

	@Override
	protected long getContentSizeInBytes() {
		// object nodes are always saved in the tree, no need to calculate the size
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (name != null) {
			sb.append(name).append(": ");
		}

		if (isNull()) {
			sb.append("null");
		} else {
			sb.append('{');
			int childCount = children.size();
			for (NDSNode ndsNode : getChildren()) {
				sb.append(ndsNode.toString());

				if (--childCount > 0) {
					sb.append(", ");
				}
			}

			sb.append('}');
		}

		return sb.toString();

	}

}
