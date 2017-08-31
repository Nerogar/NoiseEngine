package de.nerogar.noise.sound;

import static org.lwjgl.openal.AL10.*;

import java.nio.ShortBuffer;

public class SoundPCM extends Sound {

	protected int alBufferHandle;

	public SoundPCM(ShortBuffer pcmData, int channels, int sampleRate, int samples) {
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
	public boolean cleanup() {
		if(!super.cleanup()) return false;

		alDeleteSources(alSourceHandle);

		alDeleteBuffers(alBufferHandle);

		return true;
	}

}
