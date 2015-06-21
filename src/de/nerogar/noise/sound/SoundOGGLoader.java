package de.nerogar.noise.sound;

import static org.lwjgl.stb.STBVorbis.*;

import java.io.IOException;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import de.nerogar.noise.util.Logger;

public class SoundOGGLoader {

	private static class PCMDataContainer {
		public ShortBuffer pcmData;
		public STBVorbisInfo vorbisInfo;
		public int samples;
	}

	private static HashMap<String, ByteBuffer> vorbisMap = new HashMap<String, ByteBuffer>();

	private static HashMap<String, PCMDataContainer> pcmMap = new HashMap<String, PCMDataContainer>();

	public static Sound loadSound(boolean stream, String filename) {
		ByteBuffer vorbisData = vorbisMap.get(filename);

		if (vorbisData == null) {
			vorbisData = getFileAsBuffer(filename);
			vorbisMap.put(filename, vorbisData);
		}

		if (stream) {
			return new SoundOGG(vorbisData);
		} else {
			PCMDataContainer pcmContainer = pcmMap.get(filename);
			if (pcmContainer == null) {
				pcmContainer = decodeFully(vorbisData);
				pcmMap.put(filename, pcmContainer);
			}

			if (pcmContainer != null) {
				return new SoundPCM(pcmContainer.pcmData, pcmContainer.vorbisInfo.getChannels(), pcmContainer.vorbisInfo.getSampleRate(), pcmContainer.samples);
			} else {
				return null;
			}
		}

	}

	private static ByteBuffer getFileAsBuffer(String filename) {
		byte[] data = null;

		try {
			data = Files.readAllBytes(Paths.get(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	private static PCMDataContainer decodeFully(ByteBuffer vorbisData) {
		PCMDataContainer pcmContainer = new PCMDataContainer();

		IntBuffer error = BufferUtils.createIntBuffer(1);
		long decoderPointer = stb_vorbis_open_memory(vorbisData, error, null);

		if (decoderPointer == 0) {
			Logger.log(Logger.ERROR, "Could not read Vorbis data.");
			return null;
		}

		pcmContainer.vorbisInfo = new STBVorbisInfo();
		stb_vorbis_get_info(decoderPointer, pcmContainer.vorbisInfo.buffer());

		pcmContainer.samples = stb_vorbis_stream_length_in_samples(decoderPointer);
		pcmContainer.pcmData = BufferUtils.createShortBuffer(pcmContainer.samples * pcmContainer.vorbisInfo.getChannels());

		stb_vorbis_get_samples_short_interleaved(decoderPointer, pcmContainer.vorbisInfo.getChannels(), pcmContainer.pcmData);

		stb_vorbis_close(decoderPointer);

		return pcmContainer;
	}
}
