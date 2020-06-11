package de.nerogar.noise.oldGame.client.gui;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.input.MouseButtonEvent;
import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class GImage extends GElementContainer {

	private Color   color;
	private Texture texture;

	private static final VertexBufferObject quad;
	private static final Shader             defaultShader;

	public GImage(Texture texture, int width, int height) {
		this(Color.WHITE, texture, width, height);
	}

	public GImage(Color color, Texture texture, int width, int height) {
		this.color = color;
		this.texture = texture;
		this.width = width;
		this.height = height;
	}

	@Override
	public void processInput(InputHandler inputHandler, float timeDelta) {
		if (mouseInBounds(inputHandler)) {
			for (MouseButtonEvent mouseButtonEvent : inputHandler.getMouseButtonEvents()) {
				mouseButtonEvent.setProcessed();
			}
		}
	}

	@Override
	public void render(Matrix4f projectionMatrix) {

		texture.bind(0);

		defaultShader.activate();
		defaultShader.setUniformMat4f("u_projectionMatrix", projectionMatrix.asBuffer());
		defaultShader.setUniform2f("u_position", posX, posY);
		defaultShader.setUniform2f("u_size", width, height);
		defaultShader.setUniform4f("u_color", color.getR(), color.getG(), color.getB(), color.getA());
		defaultShader.setUniform1i("u_texture", 0);

		glEnable(GL_BLEND);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA, GL_ONE);

		quad.render();

		glDisable(GL_BLEND);

		defaultShader.deactivate();

		super.render(projectionMatrix);
	}

	static {
		quad = new VertexBufferObjectStandard(
				new int[] { 2, 2 },
				new float[] {
						0, 0,
						1, 0,
						1, 1,
						0, 0,
						1, 1,
						0, 1
				},
				new float[] {
						0, 0,
						1, 0,
						1, 1,
						0, 0,
						1, 1,
						0, 1
				}
		);

		defaultShader = ShaderLoader.loadShader(
				"<game/gui/image/image.vert>",
				"<game/gui/image/image.frag>"
		                                       );

	}

}
