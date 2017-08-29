package de.nerogar.noise.input;

public class KeyboardKeyEvent {

	public final int key;
	public final int scancode;
	public final int action;
	public final int mods;

	protected boolean processed = false;

	KeyboardKeyEvent(int key, int scancode, int action, int mods) {
		this.key = key;
		this.scancode = scancode;
		this.action = action;
		this.mods = mods;
	}

	public void setProcessed() {
		processed = true;
	}

}
