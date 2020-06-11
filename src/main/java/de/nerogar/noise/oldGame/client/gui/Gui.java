package de.nerogar.noise.oldGame.client.gui;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.input.InputHandler;

public class Gui extends GElementContainer {

	private EventManager eventManager;

	public Gui(EventManager eventManager) {
		this.eventManager = eventManager;

		setPosition(ALIGNMENT_LEFT, ALIGNMENT_BOTTOM, 0, 0);
	}

	public EventManager getEventManager() { return eventManager; }

	@Override
	public void processInput(InputHandler inputHandler, float timeDelta) {
	}

	@Override
	public void recalculatePosition(int parentX, int parentY, int parentWidth, int parentHeight) {
		this.width = parentWidth;
		this.height = parentHeight;

		super.recalculatePosition(parentX, parentY, parentWidth, parentHeight);
	}

	public void startBlendOut() {}

	public boolean isBlendOutDone() {
		return true;
	}

}
