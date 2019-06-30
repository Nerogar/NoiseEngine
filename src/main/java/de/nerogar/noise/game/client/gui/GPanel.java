package de.nerogar.noise.game.client.gui;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.input.MouseButtonEvent;
import de.nerogar.noise.render.Shader;
import de.nerogar.noise.render.ShaderLoader;
import de.nerogar.noise.render.VertexBufferObject;
import de.nerogar.noise.render.VertexBufferObjectStandard;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_ALPHA;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class GPanel extends GElementContainer {

	private Color color;

	private static final VertexBufferObject quad;
	private static final Shader             defaultShader;

	private final Shader shader;

	public GPanel(Color color, int width, int height) {
		this.color = color;
		this.width = width;
		this.height = height;
		this.shader = defaultShader;
	}

	public GPanel(Color color, Shader shader, int width, int height) {
		this.color = color;
		this.width = width;
		this.height = height;
		this.shader = shader;
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
		shader.activate();
		shader.setUniformMat4f("projectionMatrix", projectionMatrix.asBuffer());
		shader.setUniform2f("position", posX, posY);
		shader.setUniform2f("size", width, height);
		shader.setUniform4f("color", color.getR(), color.getG(), color.getB(), color.getA());

		glEnable(GL_BLEND);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA, GL_ONE);

		quad.render();

		glDisable(GL_BLEND);

		shader.deactivate();

		super.render(projectionMatrix);
	}

	static {
		quad = new VertexBufferObjectStandard(
				new int[] { 2 },
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
				"shaders/gui/panel/panel.vert",
				"shaders/gui/panel/panel.frag"
		                                       );

	}

}
