package de.nerogar.noise.serialization;

public class NDSConstants {

	// version
	public static final    byte   MAJOR_VERSION      = 1;
	public static final    byte   MINOR_VERSION      = 0;
	protected static final String NDS_FILE_SPECIFIER = "NDS";

	// compression
	public static final int COMPRESSION_METHOD_NONE        = 0b0000;
	public static final int COMPRESSION_METHOD_RUNLENGTH_1 = 0b0001;
	public static final int COMPRESSION_METHOD_GZIP        = 0b0010;

	// file
	protected static final String ERROR_UNSUPPORTED_FILE_VERSION = "unsupported file version";
	protected static final String ERROR_UNSUPPORTED_FEATURE      = "unsupported feature";
	protected static final String ERROR_NOT_A_VALID_NDS_FILE     = "not a valid nds file";
	protected static final String ERROR_ONLY_ASCII               = "only ascii characters are allowed";
	protected static final int    MAX_TYPE_STRING_LENGTH         = 8;
	protected static final String ERROR_MAX_TYPE_STRING_LENGTH   = "only " + MAX_TYPE_STRING_LENGTH + " characters are allowed";

	// node
	protected static final String ERROR_NO_SUCH_NODE       = "no such node";
	protected static final String ERROR_WRONG_DATA_TYPE    = "wrong datatype";
	protected static final String ERROR_WRONG_NODE_TYPE    = "wrong node type";
	protected static final String ERROR_NOT_AN_ARRAY       = "value is not an array type";
	protected static final String ERROR_UNSUPPORTED_LENGTH = "unsupported length";
	protected static final String ERROR_UNKNOWN_TYPE       = "unknown type";

	// BigInteger node
	protected static final int    BIG_INTEGER_BYTE_LIMIT       = 255;
	protected static final String ERROR_BIG_INTEGER_SIZE_LIMIT = "big integers with more than " + BIG_INTEGER_BYTE_LIMIT + " bytes are not supported";

	// --- [ types
	// array
	protected static final byte ARRAY_MASK           = (byte) 0b1100_0000;
	protected static final byte SINGLE_VALUE         = (byte) 0b0000_0000;
	protected static final byte ARRAY_VALUE          = (byte) 0b0100_0000;
	protected static final byte MULTIDIM_ARRAY_VALUE = (byte) 0b1000_0000;
	// mask
	protected static final byte TYPE_MASK            = (byte) 0b0011_0000;
	protected static final byte TYPE_SUB_MASK        = (byte) 0b0000_1111;
	// int
	protected static final byte TYPE_INTEGER         = (byte) 0b0000_0000;
	protected static final byte INTEGER_SIGN_MASK    = (byte) 0b0000_1000;
	protected static final byte INTEGER_UNSIGNED     = (byte) 0b0000_0000;
	protected static final byte INTEGER_SIGNED       = (byte) 0b0000_1000;
	// float
	protected static final byte TYPE_FLOAT           = (byte) 0b0001_0000;
	// length
	protected static final byte LENGTH_MASK          = (byte) 0b0000_0111;
	protected static final byte LENGTH_1             = (byte) 0b0000_0000;
	protected static final byte LENGTH_2             = (byte) 0b0000_0001;
	protected static final byte LENGTH_4             = (byte) 0b0000_0010;
	protected static final byte LENGTH_8             = (byte) 0b0000_0011;
	protected static final byte LENGTH_16            = (byte) 0b0000_0100;
	protected static final byte LENGTH_32            = (byte) 0b0000_0101;
	protected static final byte LENGTH_64            = (byte) 0b0000_0110;
	protected static final byte LENGTH_128           = (byte) 0b0000_0111;
	// special
	protected static final byte TYPE_SPECIAL         = (byte) 0b0011_0000;
	protected static final byte OBJECT               = (byte) 0b0011_0000;
	protected static final byte BIG_INT              = (byte) 0b0011_0001;
	protected static final byte BOOLEAN              = (byte) 0b0011_0010;
	protected static final byte STRING_UTF8          = (byte) 0b0011_0011;

	// --- [ header flags
	// sections
	protected static final int HAS_ASCII_HEADER              = 0x01;
	protected static final int HAS_TREE_DATA                 = 0x02;
	protected static final int HAS_RAW_DATA                  = 0x04;
	// features
	protected static final int USE_FULL_COMPRESSION_FEATURES = 0x01;
	protected static final int USE_FULL_INT_FEATURES         = 0x02;
	protected static final int USE_FULL_FLOAT_FEATURES       = 0x04;
	protected static final int SUPPORTED_FEATURES            = USE_FULL_COMPRESSION_FEATURES;
}
