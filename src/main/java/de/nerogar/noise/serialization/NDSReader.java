package de.nerogar.noise.serialization;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import static de.nerogar.noise.serialization.NDSConstants.*;

public class NDSReader {

	private static void readFromStream(NDSFile ndsFile, InputStream in, boolean readShallow) throws IOException {
		NDSDataInputStream headerIn = getInputStream(in, NDSConstants.COMPRESSION_METHOD_NONE);

		// NDS string
		String ndsString = headerIn.readASCIIString(3);
		if (!ndsString.equals(NDS_FILE_SPECIFIER)) {
			throw new NDSException(ERROR_NOT_A_VALID_NDS_FILE);
		}
		headerIn.readByte(); // \n

		// version
		byte majorVersion = headerIn.readByte();
		byte minorVersion = headerIn.readByte();
		headerIn.readByte(); // \n

		if (majorVersion != MAJOR_VERSION | minorVersion > MINOR_VERSION) {
			throw new NDSException(ERROR_UNSUPPORTED_FILE_VERSION);
		}

		// type
		ndsFile.setType(headerIn.readASCIIString(8));

		// compression
		int compression = headerIn.readUnsignedByte();
		ndsFile.setDataCompressionFlags((compression >> 4) & 0xF);
		ndsFile.setRawCompressionFlags(compression & 0xF);

		// 3 reserved bytes
		headerIn.readByte();
		headerIn.readByte();
		headerIn.readByte();

		// bitflags
		ndsFile.setFeatureFlags(headerIn.readByte());
		headerIn.readByte();
		headerIn.readByte();
		ndsFile.setSectionFlags(headerIn.readByte());

		if ((ndsFile.getFeatureFlags() & ~NDSConstants.SUPPORTED_FEATURES) != 0) throw new NDSException(NDSConstants.ERROR_UNSUPPORTED_FEATURE);

		// ascii header
		if ((ndsFile.getSectionFlags() & NDSConstants.HAS_ASCII_HEADER) != 0) {
			ndsFile.setAsciiHeader(headerIn.readASCIIString());
		}

		// tree data
		if ((ndsFile.getSectionFlags() & NDSConstants.HAS_TREE_DATA) != 0) {
			NDSDataInputStream dataIn = getInputStream(in, ndsFile.getDataCompressionFlags());
			ndsFile.getData().readTree(dataIn);
		}

		// raw data
		if (!readShallow) {
			if ((ndsFile.getSectionFlags() & NDSConstants.HAS_RAW_DATA) != 0) {
				NDSDataInputStream rawIn = getInputStream(in, ndsFile.getRawCompressionFlags());
				ndsFile.getData().readRaw(rawIn);
			}
		}
	}

	private static NDSDataInputStream getInputStream(InputStream in, int compression) throws IOException {
		switch (compression) {
			default:
			case NDSConstants.COMPRESSION_METHOD_NONE:
				return new NDSDataInputStream(in);
			case NDSConstants.COMPRESSION_METHOD_RUNLENGTH_1:
				return new NDSDataInputStream(new RunlengthInputStream(in));
			case NDSConstants.COMPRESSION_METHOD_GZIP:
				return new NDSDataInputStream(new BufferedInputStream(new GZIPInputStream(in)));
		}
	}

	private static NDSFile readInternal(InputStream in, boolean readShallow) {

		try {
			NDSFile ndsFile = new NDSFile();
			readFromStream(ndsFile, in, readShallow);
			return ndsFile;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static NDSFile read(InputStream in) {
		return readInternal(in, false);
	}

	public static NDSFile readFile(String filename) throws FileNotFoundException {
		return readInternal(new BufferedInputStream(new FileInputStream(new File(filename))), false);
	}

	public static NDSFile readFileShallow(String filename) throws FileNotFoundException {
		return readInternal(new BufferedInputStream(new FileInputStream(new File(filename))), true);
	}

	public static NDSFile readJson(Reader reader) {

		try {
			NDSjsonReader jsonReader = new NDSjsonReader(reader);
			return jsonReader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static NDSFile readJsonFile(String filename) throws FileNotFoundException {
		FileInputStream in = new FileInputStream(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		return readJson(reader);
	}

}
