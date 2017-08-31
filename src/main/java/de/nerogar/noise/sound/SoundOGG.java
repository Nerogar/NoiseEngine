package de.nerogar.noise.sound;

import de.nerogar.noise.Noise;
import de.nerogar.noise.util.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;

public class SoundOGG extends Sound {

	/** The buffer size in samples. */
	private static final int BUFFER_SIZE = 1024 * 32;

	protected int[]       alBufferHandles;
	protected ShortBuffer sampleBuffer;

	private long decoderPointer;

	private IntBuffer processedBuffer;

	private boolean decodingStopped;

	public SoundOGG(ByteBuffer vorbisData) {
		IntBuffer error = BufferUtils.createIntBuffer(1);
		decoderPointer = stb_vorbis_open_memory(vorbisData, error, null);

		if (decoderPointer == 0) {
			Noise.getLogger().log(Logger.ERROR, "Could not read Vorbis data.");

			super.cleanup();
			return;
		}

		STBVorbisInfo vorbisInfo = STBVorbisInfo.create();
		stb_vorbis_get_info(decoderPointer, vorbisInfo);

		setInfo(
				vorbisInfo.channels(),
				vorbisInfo.sample_rate(),
				stb_vorbis_stream_length_in_samples(decoderPointer),
				getFormat(vorbisInfo.channels())
		       );

		sampleBuffer = BufferUtils.createShortBuffer(BUFFER_SIZE);

		alBufferHandles = new int[2];

		alBufferHandles[0] = alGenBuffers();
		alBufferHandles[1] = alGenBuffers();

		stream(alBufferHandles[0]);
		stream(alBufferHandles[1]);

		processedBuffer = BufferUtils.createIntBuffer(alBufferHandles.length);
	}

	private boolean stream(int alBuffer) {
		int decoded = stb_vorbis_get_samples_short_interleaved(decoderPointer, channels, sampleBuffer);

		//System.out.println(alBuffer + " -> " + decoded + " (samples)");

		if (decoded == 0) {

			if (loop) {
				stb_vorbis_seek_start(decoderPointer);
			} else {
				decodingStopped = true;
			}
			return false;
		}

		alBufferData(alBuffer, format, sampleBuffer, sampleRate);
		checkError();

		alSourceQueueBuffers(alSourceHandle, alBuffer);

		if (decoded == 0) playing = false;

		boolean isPLaying = alGetSourcei(alSourceHandle, AL_SOURCE_STATE) == AL_PLAYING;
		if (playing && !isPLaying) alSourcePlay(alSourceHandle);

		return true;
	}

	@Override
	public void update() {
		if (super.isCleaned()) return;

		int processed = alGetSourcei(alSourceHandle, AL_BUFFERS_PROCESSED);

		processedBuffer.limit(processed);
		alSourceUnqueueBuffers(alSourceHandle, processedBuffer);

		if (!decodingStopped) {
			for (int i = 0; i < processedBuffer.limit(); i++) {

				int alBuffer = processedBuffer.get(i);

				//System.out.println(alBuffer + " (unque)");

				checkError();

				stream(alBuffer);
			}
		} else {
			if (alGetSourcei(alSourceHandle, AL_SOURCE_STATE) == AL_STOPPED) cleanup();
		}
	}

	@Override
	public boolean cleanup() {
		if (!super.cleanup()) return false;

		alDeleteSources(alSourceHandle);

		for (int i = 0; i < alBufferHandles.length; i++) {
			alDeleteBuffers(alBufferHandles[i]);
		}

		stb_vorbis_close(decoderPointer);

		return true;
	}

}
