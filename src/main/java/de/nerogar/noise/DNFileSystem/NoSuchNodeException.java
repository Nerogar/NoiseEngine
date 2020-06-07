package de.nerogar.noise.DNFileSystem;

public class NoSuchNodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static final int NODE_NOT_FOUND = 0;
	public static final int WRONG_NODE_TYPE = 1;

	private int errorType;

	public NoSuchNodeException(int errorType) {
		this.errorType = errorType;
	}

	public int getErrorType() {
		return errorType;
	}

}
