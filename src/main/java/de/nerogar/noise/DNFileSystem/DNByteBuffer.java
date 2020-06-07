package de.nerogar.noise.DNFileSystem;

import java.nio.ByteBuffer;

public class DNByteBuffer {
	protected ByteBuffer byteBuffer;

	protected DNByteBuffer(int length) {
		byteBuffer = ByteBuffer.allocate(length);
	}

	protected DNByteBuffer(byte[] data) {
		byteBuffer = ByteBuffer.allocate(data.length);
		byteBuffer.put(data);
	}

	protected int readInt() {
		return byteBuffer.getInt();
	}

	protected float readFloat() {
		return byteBuffer.getFloat();
	}

	protected double readDouble() {
		return byteBuffer.getDouble();
	}

	protected long readLong() {
		return byteBuffer.getLong();
	}

	protected byte readByte() {
		return byteBuffer.get();
	}

	protected char readChar() {
		return byteBuffer.getChar();
	}

	protected boolean readBool() {
		return byteBuffer.get() == 0x01;
	}

	protected String readString() {
		int length = byteBuffer.getInt();
		if (length < 0) return null;
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < length; i++) {
			text.append(byteBuffer.getChar());
		}
		return text.toString();
	}

	protected void writeInt(int value) {
		byteBuffer.putInt(value);
	}

	protected void writeFloat(float value) {
		byteBuffer.putFloat(value);
	}

	protected void writeDouble(double value) {
		byteBuffer.putDouble(value);
	}

	protected void writeLong(long value) {
		byteBuffer.putLong(value);
	}

	protected void writeByte(byte value) {
		byteBuffer.put(value);
	}

	protected void writeChar(char value) {
		byteBuffer.putChar(value);
	}

	protected void writeBool(boolean value) {
		if (value) {
			byteBuffer.put((byte) 0x01);
		} else {
			byteBuffer.put((byte) 0x00);
		}
	}

	protected void writeString(String value) {
		if (value != null) {
			byteBuffer.putInt(value.length());
			for (int i = 0; i < value.length(); i++) {
				byteBuffer.putChar(value.charAt(i));
			}
		} else {
			byteBuffer.putInt(-1);
		}
	}

	protected int size() {
		return byteBuffer.limit();
	}

	protected int available() {
		return byteBuffer.limit() - byteBuffer.position();
	}
}
