package de.nerogar.noise.sound;

import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.AL.createCapabilities;
import static org.lwjgl.openal.ALC.createCapabilities;
import static org.lwjgl.openal.ALC10.*;

public class ALContext {

	private long alContextPointer;

	private ALCapabilities capabilities;

	public ALContext() {
		long device = alcOpenDevice((ByteBuffer) null);
		ALCCapabilities alcCapabilities = createCapabilities(device);

		alContextPointer = alcCreateContext(device, (IntBuffer) null);
		alcMakeContextCurrent(alContextPointer);
		this.capabilities = createCapabilities(alcCapabilities);
	}

	public void destroy() {
		alcDestroyContext(alContextPointer);
	}

	public ALCapabilities getCapabilities() {
		return capabilities;
	}
}
