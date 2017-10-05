package de.nerogar.noise.serialization;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import static de.nerogar.noise.serialization.NDSConstants.NDS_FILE_SPECIFIER;

public class NDSWriter {

	private static void writeToStream(NDSFile ndsFile, OutputStream out) throws IOException {
		NDSDataOutputStream headerOut = new NDSDataOutputStream(getOutputStream(out, NDSConstants.COMPRESSION_METHOD_NONE));

		// NDS string
		headerOut.writeASCIIString(NDS_FILE_SPECIFIER, false);
		headerOut.write('\n');

		// version
		headerOut.write(1);
		headerOut.write(0);
		headerOut.write('\n');

		// type string
		headerOut.writeASCIIString(ndsFile.getType(), false);

		// compression
		int dataCompression = ndsFile.getDataCompressionFlags();
		int rawCompression = ndsFile.getRawCompressionFlags();
		headerOut.write((dataCompression << 4) | rawCompression);

		// 3 reserved bytes
		headerOut.write(0);
		headerOut.write(0);
		headerOut.write(0);

		// bit flags
		headerOut.write(ndsFile.getFeatureFlags());
		headerOut.write(0);
		headerOut.write(0);
		headerOut.write(ndsFile.getSectionFlags());

		// ascii header
		if ((ndsFile.getSectionFlags() & NDSConstants.HAS_ASCII_HEADER) != 0) {
			headerOut.writeASCIIString(ndsFile.getAsciiHeader(), true);
		}

		headerOut.flush();

		// tree data
		OutputStream dataOut0 = getOutputStream(out, ndsFile.getDataCompressionFlags());
		NDSDataOutputStream dataOut = new NDSDataOutputStream(dataOut0);
		if ((ndsFile.getSectionFlags() & NDSConstants.HAS_TREE_DATA) != 0) {
			ndsFile.getData().writeTree(dataOut);
		}

		dataOut.flush();
		finish(dataOut0);

		// raw data
		OutputStream rawOut0 = getOutputStream(out, ndsFile.getRawCompressionFlags());
		NDSDataOutputStream rawOut = new NDSDataOutputStream(rawOut0);
		if ((ndsFile.getSectionFlags() & NDSConstants.HAS_RAW_DATA) != 0) {
			ndsFile.getData().writeRaw(rawOut);
		}

		rawOut.flush();
		finish(rawOut0);
	}

	private static OutputStream getOutputStream(OutputStream out, int compression) throws IOException {
		switch (compression) {
			default:
			case NDSConstants.COMPRESSION_METHOD_NONE:
				return out;
			case NDSConstants.COMPRESSION_METHOD_RUNLENGTH_1:
				return new RunlengthOutputStream(out);
			case NDSConstants.COMPRESSION_METHOD_GZIP:
				return new BufferedOutputStream(new GZIPOutputStream(out));
		}
	}

	private static void finish(OutputStream out) throws IOException {
		if (out instanceof GZIPOutputStream) {
			((GZIPOutputStream) out).finish();
		}
	}

	public static void write(NDSFile ndsFile, OutputStream out, int dataCompressionFlags, int rawCompressionFlags) {
		try {
			// setup flags
			ndsFile.setDataCompressionFlags(dataCompressionFlags);
			ndsFile.setRawCompressionFlags(rawCompressionFlags);

			// prepare the file
			ndsFile.prepareWrite();

			// write
			writeToStream(ndsFile, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void write(NDSFile ndsFile, OutputStream out) {
		write(ndsFile, out, NDSConstants.COMPRESSION_METHOD_RUNLENGTH_1, NDSConstants.COMPRESSION_METHOD_RUNLENGTH_1);
	}

	public static void writeFile(NDSFile ndsFile, String filename, int dataCompressionFlags, int rawCompressionFlags) {
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(filename)), 1024 * 64);
			write(ndsFile, out, dataCompressionFlags, rawCompressionFlags);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(NDSFile ndsFile, String filename) {
		writeFile(ndsFile, filename, NDSConstants.COMPRESSION_METHOD_RUNLENGTH_1, NDSConstants.COMPRESSION_METHOD_RUNLENGTH_1);
	}

	public static void writeJson(NDSFile ndsFile, OutputStream out) throws FileNotFoundException {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
			NDSjsonWriter jsonWriter = new NDSjsonWriter(writer);
			jsonWriter.write(ndsFile);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeJsonFile(NDSFile ndsFile, String filename) throws FileNotFoundException {
		try {
			writeJson(ndsFile, new FileOutputStream(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
