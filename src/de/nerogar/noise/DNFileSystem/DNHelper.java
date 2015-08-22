package de.nerogar.noise.DNFileSystem;

public class DNHelper {

	protected final static byte FOLDER = 0;
	protected final static byte INTEGER = 1;
	protected final static byte LONG = 2;
	protected final static byte BYTE = 3;
	protected final static byte CHAR = 4;
	protected final static byte STRING = 5;
	protected final static byte FLOAT = 6;
	protected final static byte DOUBLE = 7;
	protected final static byte BOOLEAN = 8;

	protected final static int FOLDERSIZE = 9; //+name * 2
	protected final static byte INTEGERSIZE = 4;
	protected final static byte LONGSIZE = 8;
	protected final static byte BYTESIZE = 1;
	protected final static byte CHARSIZE = 2;
	protected final static byte STRINGSIZE = 4; //+l�nge * 2
	protected final static byte FLOATSIZE = 4;
	protected final static byte DOUBLESIZE = 8;
	protected final static byte BOOLEANSIZE = 1;
}
