package de.nerogar.noise.game.client.event;

import de.nerogar.noise.event.Event;

public class WindowSizeChangeEvent implements Event {

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
