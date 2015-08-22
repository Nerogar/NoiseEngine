package de.nerogar.noise.DNFileSystem;

public class DNNode {
	protected String name;
	protected byte typ;
	protected int length;
	protected Object value;

	protected DNNode(String name, byte typ, int length, Object value) {
		this.name = name;
		this.typ = typ;
		this.length = length;
		this.value = value;
	}

	protected int calcSize() {
		int size = 0;

		switch (typ) {
		case DNHelper.INTEGER:
			if (length >= 0) {
				size += ((int[]) value).length * DNHelper.INTEGERSIZE;
			}
			break;
		case DNHelper.LONG:
			if (length >= 0) {
				size += ((long[]) value).length * DNHelper.LONGSIZE;
			}
			break;
		case DNHelper.BYTE:
			if (length >= 0) {
				size += ((byte[]) value).length * DNHelper.BYTESIZE;
			}
			break;
		case DNHelper.CHAR:
			if (length >= 0) {
				size += ((char[]) value).length * DNHelper.CHARSIZE;
			}
			break;
		case DNHelper.STRING:
			if (length >= 0) {
				for (String tempString : (String[]) value) {
					if (tempString != null) {
						size += tempString.length() * 2;
					}
					size += DNHelper.STRINGSIZE;
				}
			}
			break;

		case DNHelper.FLOAT:
			if (length >= 0) {
				size += ((float[]) value).length * DNHelper.FLOATSIZE;
			}
			break;

		case DNHelper.DOUBLE:
			if (length >= 0) {
				size += ((double[]) value).length * DNHelper.DOUBLESIZE;
			}
			break;

		case DNHelper.BOOLEAN:
			if (length >= 0) {
				size += ((boolean[]) value).length * DNHelper.BOOLEANSIZE;
			}
			break;
		}

		size += name.length() * 2 + 4; //length of name
		size += 1 + 4; //type + lengthNum
		return size;
	}
}
