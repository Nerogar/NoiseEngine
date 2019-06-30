package de.nerogar.noise.game.client.gui;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.render.fontRenderer.Font;
import de.nerogar.noise.render.fontRenderer.FontRenderableString;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Matrix4f;
import de.nerogar.noise.util.Matrix4fUtils;

public class GLabel extends GElement {

	protected Font   font;
	protected String text;
	protected Color  color;

	private FontRenderableString renderableString;

	public GLabel(Font font, Color color, String text) {
		this.font = font;
		this.color = color;
		this.text = text;

		setText(text);
	}

	@Override
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

		// the baseline for strings is the baseline of the first row of letters
		//posY += renderableString.getHeight() - font.getBaseline();
		posY += renderableString.getHeight() - font.getPointSize();

		Matrix4fUtils.setPositionMatrix(modelMatrix, posX, posY, 0);
	}

	public void setText(String text) {
		this.text = text;

		if (renderableString != null) renderableString.cleanup();
		renderableString = new FontRenderableString(font, text, color, null, 1, 1);

		width = (int) renderableString.getWidth();
		height = (int) renderableString.getHeight();
	}

	public void setColor(Color color) {
		this.color = color;
		renderableString.setColor(color);
	}

	@Override
	public void processInput(InputHandler inputHandler, float timeDelta) {
	}

	@Override
	public void render(Matrix4f projectionMatrix) {
		renderableString.setRenderDimensions(projectionMatrix, 1, 1);
		renderableString.render(posX, posY);
	}

	@Override
	public void cleanup() {
		renderableString.cleanup();
	}

}
