package de.nerogar.noise.serialization;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RunlengthInputStream extends FilterInputStream {

	private int     runByte;
	private int     length;
	private boolean run;

	public RunlengthInputStream(InputStream in) {
		super(in);
	}

	@Override
	public int read() throws IOException {
		if (length == 0) {
			int runIndicatorByte = nextByte();

			run = (runIndicatorByte & 0x80) != 0;
			length = (runIndicatorByte & 0x7F) + 1;

			if (run) runByte = nextByte();
		}

		length--;
		if (run) {
			return runByte;
		} else {
			int read = nextByte();
			return read;
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {

		int read = 0;

		for (int i = 0; i < len; i++) {
			int readByte = read();
			if (readByte < 0) {
				break;
			} else {
				b[off + i] = (byte) readByte;
				read++;
			}
		}

		return read;

	}

	@Override
	public long skip(long n) throws IOException {
		int skipped = 0;

		for (long i = 0; i < n; i++) {
			if (read() < 0) break;
			skipped++;
		}

		return skipped;
	}

	private int nextByte() throws IOException {
		int read = super.read();

		if (read < 0) throw new EOFException();
		return read;
	}

}
