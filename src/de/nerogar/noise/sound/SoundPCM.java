package de.nerogar.noise.sound;

import static org.lwjgl.openal.AL10.*;

import java.nio.ShortBuffer;

public class SoundPCM extends Sound {

	protected int alBufferHandle;

	public SoundPCM(ShortBuffer pcmData, int channels, int sampleRate, int samples) {
		super();

		setInfo(channels,
				sampleRate,
				samples,
				getFormat(channels));

		alBufferHandle = alGenBuffers();

		alBufferData(alBufferHandle, format, pcmData, sampleRate);
		alSourceQueueBuffers(alSourceHandle, alBufferHandle);
	}

	@Override
	public void update() {

	}

	@Override
	public void cleanup() {
		if (cleaned) return;

		alDeleteSources(alSourceHandle);

		alDeleteBuffers(alBufferHandle);

		cleaned = true;
	}

}
