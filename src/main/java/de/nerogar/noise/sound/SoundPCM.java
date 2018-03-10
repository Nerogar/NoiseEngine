package de.nerogar.noise.sound;

import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;

public class SoundPCM extends Sound {

	private int alBufferHandle;

	public SoundPCM(ShortBuffer pcmData, int channels, int sampleRate, int samples) {
		setInfo(
				channels,
				sampleRate,
				samples,
				getFormat(channels)
		       );

		alBufferHandle = alGenBuffers();

		alBufferData(alBufferHandle, format, pcmData, sampleRate);
		alSourceQueueBuffers(alSourceHandle, alBufferHandle);
	}

	@Override
	public void update() {
		int status = alGetSourcei(alSourceHandle, AL_SOURCE_STATE);

		if (status == AL_STOPPED) {
			playing = false;
			playbackStopped = true;
			cleanup();
		}
	}

	@Override
	public boolean isDone() {
		return playbackStopped;
	}

	@Override
	public boolean cleanup() {
		if (!super.cleanup()) return false;

		alDeleteSources(alSourceHandle);

		alDeleteBuffers(alBufferHandle);

		return true;
	}

}
