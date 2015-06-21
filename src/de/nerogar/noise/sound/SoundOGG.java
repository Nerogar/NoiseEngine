package de.nerogar.noise.sound;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;

import java.nio.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import de.nerogar.noise.util.Logger;

public class SoundOGG extends Sound {

	/**The buffer size in samples.*/
	private static final int BUFFER_SIZE = 1024 * 32;

	protected int[] alBufferHandles;
	protected ShortBuffer sampleBuffer;

	private boolean stream;

	private long decoderPointer;

	private IntBuffer processedBuffer;

	public SoundOGG(ByteBuffer vorbisData) {
		super();

		this.stream = true;

		IntBuffer error = BufferUtils.createIntBuffer(1);
		decoderPointer = stb_vorbis_open_memory(vorbisData, error, null);

		if (decoderPointer == 0) {
			Logger.log(Logger.ERROR, "Could not read Vorbis data.");

			cleaned = true;
			return;
		}

		STBVorbisInfo vorbisInfo = new STBVorbisInfo();
		stb_vorbis_get_info(decoderPointer, vorbisInfo.buffer());

		setInfo(vorbisInfo.getChannels(),
				vorbisInfo.getSampleRate(),
				stb_vorbis_stream_length_in_samples(decoderPointer),
				getFormat(vorbisInfo.getChannels()));

		sampleBuffer = BufferUtils.createShortBuffer(BUFFER_SIZE);

		alBufferHandles = new int[2];

		alBufferHandles[0] = alGenBuffers();
		alBufferHandles[1] = alGenBuffers();

		stream(alBufferHandles[0]);
		stream(alBufferHandles[1]);

		processedBuffer = BufferUtils.createIntBuffer(alBufferHandles.length);
	}

	private void stream(int alBuffer) {
		int decoded = stb_vorbis_get_samples_short_interleaved(decoderPointer, channels, sampleBuffer);

		//System.out.println(alBuffer + " -> " + decoded + " (samples)");

		alBufferData(alBuffer, format, sampleBuffer, sampleRate);
		checkError();

		alSourceQueueBuffers(alSourceHandle, alBuffer);

		if (decoded == 0) playing = false;

		boolean isPLaying = alGetSourcei(alSourceHandle, AL_SOURCE_STATE) == AL_PLAYING;
		if (playing && !isPLaying) alSourcePlay(alSourceHandle);

	}

	@Override
	public void update() {
		if (!stream || cleaned) return;

		int processed = alGetSourcei(alSourceHandle, AL_BUFFERS_PROCESSED);

		processedBuffer.limit(processed);
		alSourceUnqueueBuffers(alSourceHandle, processedBuffer);

		for (int i = 0; i < processedBuffer.limit(); i++) {

			int alBuffer = processedBuffer.get(i);

			//System.out.println(alBuffer + " (unque)");

			checkError();

			stream(alBuffer);
		}
	}

	@Override
	public void cleanup() {
		if (cleaned) return;

		alDeleteSources(alSourceHandle);

		for (int i = 0; i < alBufferHandles.length; i++) {
			alDeleteBuffers(alBufferHandles[i]);
		}

		stb_vorbis_close(decoderPointer);

		cleaned = true;
	}

}
