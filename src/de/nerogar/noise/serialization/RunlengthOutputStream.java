package de.nerogar.noise.serialization;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RunlengthOutputStream extends FilterOutputStream {

	private static final int STATUS_UNDEFINED = -1;
	private static final int STATUS_RUN       = 0;
	private static final int STATUS_NO_RUN    = 1;

	private static final byte RUN_BIT = (byte) 0x80;

	private int   bufferHead;
	private int[] buffer;

	private int runLength;

	private int status;

	public RunlengthOutputStream(OutputStream out) {
		super(out);

		buffer = new int[128];
		bufferHead = -1;

		status = STATUS_UNDEFINED;

	}

	private boolean sameAsLast(int b) {
		if (bufferHead < 0) return false;
		return b == buffer[bufferHead];
	}

	@Override
	public void write(int b) throws IOException {

		boolean same = sameAsLast(b);

		if (bufferHead == -1) {
			buffer[++bufferHead] = b;
		} else if (bufferHead == 0) {
			buffer[++bufferHead] = b;
			if (!same) {
				status = STATUS_NO_RUN;
			}
		} else if (bufferHead == 1) {
			buffer[++bufferHead] = b;

			if (status == STATUS_UNDEFINED) {
				status = same ? STATUS_RUN : STATUS_NO_RUN;
			}
		} else {
			if (status == STATUS_RUN) {
				if (!same) flushRun();
				buffer[++bufferHead] = b;
			} else {
				if (same) {
					runLength++;
				} else {
					runLength = 1;
				}

				if (runLength == 4) {
					bufferHead -= 3;
					flushRun();
					buffer[++bufferHead] = b;
					buffer[++bufferHead] = b;
					buffer[++bufferHead] = b;
					status = STATUS_RUN;
				}

				buffer[++bufferHead] = b;

			}
		}

		if (bufferHead == 127) {
			flushRun();
		}
	}

	@Override
	public void flush() throws IOException {
		flushRun();
		super.flush();
	}

	private void flushRun() throws IOException {
		if (bufferHead < 0) return;

		if (status == STATUS_RUN) {
			super.write(RUN_BIT | bufferHead);
			super.write(buffer[0]);
		} else {
			super.write(bufferHead);
			for (int i = 0; i <= bufferHead; i++) {
				super.write(buffer[i]);
			}
		}

		bufferHead = -1;
		status = STATUS_UNDEFINED;
		runLength = 0;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			write(b[off + i]);
		}
	}

}
