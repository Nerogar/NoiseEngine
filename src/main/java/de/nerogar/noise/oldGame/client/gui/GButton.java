package de.nerogar.noise.oldGame.client.gui;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.input.MouseButtonEvent;
import de.nerogar.noise.render.fontRenderer.Font;
import de.nerogar.noise.util.Color;
import org.lwjgl.glfw.GLFW;

public class GButton extends GLabel {

	private Runnable function;

	private Color defaultColor;
	private Color hoverColor;

	public GButton(Font font, String text, int width, int height, Runnable function) {
		super(font, new Color(1f, 1f, 1f, 1f), text);

		this.width = width;
		this.height = height;

		this.function = function;

		defaultColor = color;
		hoverColor = defaultColor;
	}

	public void setColors(Color defaultColor, Color hoverColor) {
		this.defaultColor = defaultColor;
		this.hoverColor = hoverColor;
	}

	@Override
	public void processInput(InputHandler inputHandler, float timeDelta) {
		super.processInput(inputHandler, timeDelta);

		if (mouseInBounds(inputHandler)) {
			setColor(hoverColor);

			for (MouseButtonEvent mouseButtonEvent : inputHandler.getMouseButtonEvents()) {
				if (mouseButtonEvent.button == GLFW.GLFW_MOUSE_BUTTON_LEFT && mouseButtonEvent.action == GLFW.GLFW_RELEASE) {
					function.run();
					mouseButtonEvent.setProcessed();
				}
			}
		} else {
			setColor(defaultColor);
		}

	}

}
