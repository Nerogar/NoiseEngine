package de.nerogar.noise.oldGame.client.event;

import de.nerogar.noise.event.IEvent;

public class WindowSizeChangeEvent implements IEvent {

	private final int width;
	private final int height;

	private float aspect;

	public WindowSizeChangeEvent(int width, int height) {

		this.width = width;
		this.height = height;

		this.aspect = (float) width / height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getAspect() {
		return aspect;
	}

}
