package de.nerogar.noise.game.client.gui;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.util.Matrix4f;
import de.nerogar.noise.util.Matrix4fUtils;

public abstract class GElement {

	public static final int ALIGNMENT_CENTER = 0;
	public static final int ALIGNMENT_LEFT   = 1;
	public static final int ALIGNMENT_RIGHT  = 2;
	public static final int ALIGNMENT_TOP    = 3;
	public static final int ALIGNMENT_BOTTOM = 4;

	protected GuiContainer container;

	protected int alignmentX;
	protected int alignmentY;

	protected int distanceX;
	protected int distanceY;

	protected Matrix4f modelMatrix;
	protected int      posX, posY, width, height;

	public GElement() {
		modelMatrix = new Matrix4f();
	}

	public void setPosition(int alignmentX, int alignmentY, int distanceX, int distanceY) {
		this.alignmentX = alignmentX;
		this.alignmentY = alignmentY;

		this.distanceX = distanceX;
		this.distanceY = distanceY;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void recalculatePosition(int parentX, int parentY, int parentWidth, int parentHeight) {
		if (alignmentX == ALIGNMENT_LEFT) {
			posX = distanceX;
		} else if (alignmentX == ALIGNMENT_RIGHT) {
			posX = parentWidth - distanceX - width;
		}

		if (alignmentY == ALIGNMENT_BOTTOM) {
			posY = distanceY;
		} else if (alignmentY == ALIGNMENT_TOP) {
			posY = parentHeight - distanceY - height;
		}

		posX += parentX;
		posY += parentY;

		Matrix4fUtils.setPositionMatrix(modelMatrix, posX, posY, 0);
	}

	public int getWidth()  { return width; }

	public int getHeight() { return height; }

	protected void setContainer(GuiContainer container) {
		this.container = container;
	}

	public void update(InputHandler inputHandler, float timeDelta) {
		processInput(inputHandler, timeDelta);
	}

	public boolean mouseInBounds(InputHandler inputHandler) {
		int mouseX = (int) inputHandler.getCursorPosX();
		int mouseY = (int) inputHandler.getCursorPosY();

		return (mouseX >= posX && mouseX < posX + width)
				&& (mouseY >= posY && mouseY < posY + height);
	}

	public abstract void processInput(InputHandler inputHandler, float timeDelta);

	public abstract void render(Matrix4f projectionMatrix);

	public abstract void cleanup();

}
