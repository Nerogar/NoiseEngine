package de.nerogar.noise.sound;

import de.nerogar.noise.util.NoiseResource;

import static org.lwjgl.openal.AL10.*;

public abstract class Sound extends NoiseResource {

	protected int alSourceHandle;

	protected float volume;
	protected float pitch;
	protected float x, y, z;

	protected boolean playing;
	protected boolean loop;

	protected int channels;
	protected int sampleRate;
	protected int samples;
	protected int format;

	protected boolean playbackStopped;

	public Sound() {
		volume = 1.0f;
		pitch = 1.0f;

		alSourceHandle = alGenSources();
	}

	public int getAlSourceHandle() {
		return alSourceHandle;
	}

	public int getChannels() {
		return channels;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getSamples() {
		return samples;
	}

	protected int getFormat(int channels) {
		return channels == 2 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16;
	}

	protected void setInfo(int channels, int sampleRate, int samples, int format) {
		this.channels = channels;
		this.sampleRate = sampleRate;
		this.samples = samples;
		this.format = format;
	}

	public boolean getLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
		alSourcef(alSourceHandle, AL_GAIN, volume);
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
		alSourcef(alSourceHandle, AL_PITCH, pitch);
	}

	public void setPosition(float x, float y, float z) {
		alSource3f(alSourceHandle, AL_POSITION, x, y, z);
	}

	public void play() {
		if (playing) return;

		alSourcePlay(alSourceHandle);
		playing = true;
	}

	public void pause() {
		if (!playing) return;

		alSourcePause(alSourceHandle);
		playing = false;
	}

	public void stop() {
		alSourceStop(alSourceHandle);
		playing = false;
	}

	public abstract void update();

	public abstract boolean isDone();

	@Deprecated
	public static void checkError() {
		int err;
		while ((err = alGetError()) != 0) {
			System.err.println("error: " + err);
		}
	}

}
