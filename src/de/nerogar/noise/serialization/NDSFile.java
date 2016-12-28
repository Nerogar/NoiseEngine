package de.nerogar.noise.serialization;

import static de.nerogar.noise.serialization.NDSConstants.*;

public class NDSFile {

	private String type;
	private String asciiHeader;

	private final NDSNodeRoot data;

	private int dataCompressionFlags;
	private int rawCompressionFlags;
	private int sectionFlags;
	private int featureFlags;

	public NDSFile() {
		setType("");
		setAsciiHeader("");

		this.data = new NDSNodeRoot("data");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (!checkAscii(type)) {
			throw new IllegalArgumentException(ERROR_ONLY_ASCII);
		} else if (type.length() > MAX_TYPE_STRING_LENGTH) {
			throw new IllegalArgumentException(ERROR_MAX_TYPE_STRING_LENGTH);
		} else {
			this.type = setAsciiLength(type, NDSConstants.MAX_TYPE_STRING_LENGTH);
		}
	}

	public String getAsciiHeader() {
		return asciiHeader;
	}

	public void setAsciiHeader(String asciiHeader) {
		if (!checkAscii(asciiHeader)) {
			throw new IllegalArgumentException(ERROR_ONLY_ASCII);
		}

		this.asciiHeader = asciiHeader;
	}

	protected int getDataCompressionFlags() {
		return dataCompressionFlags;
	}

	protected void setDataCompressionFlags(int dataCompressionFlags) {
		this.dataCompressionFlags = dataCompressionFlags;
	}

	protected int getRawCompressionFlags() {
		return rawCompressionFlags;
	}

	protected void setRawCompressionFlags(int rawCompressionFlags) {
		this.rawCompressionFlags = rawCompressionFlags;
	}

	protected int getSectionFlags() {
		return sectionFlags;
	}

	protected void setSectionFlags(int sectionFlags) {
		this.sectionFlags = sectionFlags;
	}

	public int getFeatureFlags() {
		return featureFlags;
	}

	protected void setFeatureFlags(int featureFlags) {
		this.featureFlags = featureFlags;
	}

	public NDSNodeRoot getData() {
		return data;
	}

	protected void prepareWrite() {
		data.resetDataPointer();
		data.resetRawNodes();

		sectionFlags = 0;
		if (!asciiHeader.isEmpty()) sectionFlags |= HAS_ASCII_HEADER;
		if (!data.isEmpty()) sectionFlags |= HAS_TREE_DATA;
		if (data.hasRawNode()) sectionFlags |= HAS_RAW_DATA;
	}

	protected static boolean checkAscii(String string) {
		for (int i = 0; i < string.length(); i++) {
			if ((string.codePointAt(i) & ~0x7F) != 0) {
				return false;
			}
		}

		return true;
	}

	protected static String setAsciiLength(String string, int length) {
		if (string.length() == length) return string;
		else if (string.length() > length) return string.substring(0, length);
		else {
			while (string.length() < length) string = string + '\0';
			return string;
		}
	}

}
