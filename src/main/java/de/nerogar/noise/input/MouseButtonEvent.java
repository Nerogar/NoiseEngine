package de.nerogar.noise.input;

public class MouseButtonEvent {

	public final int button;
	public final int action;
	public final int mods;

	protected boolean processed = false;

	MouseButtonEvent(int button, int action, int mods) {
		this.button = button;
		this.action = action;
		this.mods = mods;
	}

	public void setProcessed() {
		processed = true;
	}

}
