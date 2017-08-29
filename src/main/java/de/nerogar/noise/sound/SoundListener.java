package de.nerogar.noise.sound;

import static org.lwjgl.openal.AL10.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL11;

import de.nerogar.noise.util.Vector3f;

public class SoundListener {

	private Vector3f position;

	private Vector3f directionAt;
	private Vector3f directionUp;
	private FloatBuffer directionBuffer;

	public SoundListener() {
		position = new Vector3f();

		directionAt = new Vector3f();
		directionUp = new Vector3f();
		directionBuffer = BufferUtils.createFloatBuffer(6);

		alListener3f(AL_POSITION, 0.0f, 0.0f, 0.0f);

		AL11.alSpeedOfSound(1.0f);
	}

	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
		alListener3f(AL_POSITION, x, y, z);
	}

	public void setPosition(Vector3f position) {
		this.position.set(position);
		alListener3f(AL_POSITION, position.getX(), position.getY(), position.getZ());
	}

	public void setDirection(float xAt, float yAt, float zAt, float xUp, float yUp, float zUp) {
		directionAt.set(xAt, yAt, zAt);
		directionUp.set(xUp, yUp, zUp);

		setDirection();
	}

	public void setDirection(Vector3f directionAt, Vector3f directionUp) {
		this.directionAt.set(directionAt);
		this.directionUp.set(directionUp);

		setDirection();
	}

	private void setDirection() {
		directionBuffer.position(0);
		directionBuffer.put(directionAt.getX());
		directionBuffer.put(directionAt.getY());
		directionBuffer.put(directionAt.getZ());

		directionBuffer.put(directionUp.getX());
		directionBuffer.put(directionUp.getY());
		directionBuffer.put(directionUp.getZ());

		directionBuffer.flip();

		alListenerfv(AL_ORIENTATION, directionBuffer);
	};
}
