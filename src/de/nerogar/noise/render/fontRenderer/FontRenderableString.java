package de.nerogar.noise.render.fontRenderer;

import static org.lwjgl.opengl.GL11.*;

import java.util.HashMap;

import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Matrix4f;

public class FontRenderableString {

	private static final int tabSize = 4;

	private static HashMap<Long, Shader> glContextShaderMap;

	private Font font;
	private Color color;

	private VertexBufferObject vbo;

	private Matrix4f projectionMatrix;
	private float pointSizeX;
	private float pointSizeY;

	public FontRenderableString(Font font, String text, Color color, Matrix4f projectionMatrix, float pointSizeX, float pointSizeY) {
		this.font = font;
		this.color = color;

		setRenderDimensions(projectionMatrix, pointSizeX, pointSizeY);

		vbo = createVBO(text);
	}

	private VertexBufferObject createVBO(String text) {
		VertexList vertexList = new VertexList();

		int offsetX = 0;
		int offsetY = -font.getBaseline();

		for (char c : text.toCharArray()) {
			if (c == '\n') {
				offsetX = 0;
				offsetY -= font.getLineSpace();
			} else if (c == '\t') {
				offsetX = ((offsetX / font.getPointSize() / tabSize) + 1) * tabSize * font.getPointSize();
			} else {

				float left = font.getCharLeft(c);
				float bottom = font.getCharBottom(c);
				float width = font.getCharWidth(c);
				float height = font.getCharHeight(c);

				int v1 = vertexList.addVertex(offsetX, offsetY, 0, left, bottom, 0, 0, 0);
				int v2 = vertexList.addVertex(offsetX + font.getSize(), offsetY, 0, left + width, bottom, 0, 0, 0);
				int v3 = vertexList.addVertex(offsetX + font.getSize(), offsetY + font.getSize(), 0, left + width, bottom + height, 0, 0, 0);
				int v4 = vertexList.addVertex(offsetX, offsetY + font.getSize(), 0, left, bottom + height, 0, 0, 0);

				vertexList.addIndex(v1, v2, v3);
				vertexList.addIndex(v3, v4, v1);

				offsetX += font.getCharPixelWidth(c);
			}
		}

		return new VertexBufferObjectIndexed(
				new int[] { 3, 2 },
				vertexList.getIndexCount(),
				vertexList.getVertexCount(),
				vertexList.getIndexArray(),
				vertexList.getPositionArray(),
				vertexList.getUVArray());

	}

	public void setRenderDimensions(Matrix4f projectionMatrix, float pointSizeX, float pointSizeY) {
		this.projectionMatrix = projectionMatrix;
		this.pointSizeX = pointSizeX;
		this.pointSizeY = pointSizeY;
	}

	public void render(int left, int bottom) {
		long currentContext = GLWindow.getCurrentContext();

		font.getTexture().bind(0);

		Shader shader = glContextShaderMap.get(currentContext);

		if (shader == null) {
			shader = getFontShader();
			glContextShaderMap.put(currentContext, shader);
		}

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		shader.activate();
		shader.setUniformMat4f("projectionMatrix", projectionMatrix.asBuffer());
		shader.setUniform2f("pointSize", pointSizeX, pointSizeY);
		shader.setUniform2f("offset", left, bottom);
		shader.setUniform4f("fontColor", color.getR(), color.getG(), color.getB(), color.getA());
		vbo.render();
		shader.deactivate();

		glDisable(GL_BLEND);
	}

	private static Shader getFontShader() {
		Shader shader = ShaderLoader.loadShader("<font/font.vert>", "<font/font.frag>");
		shader.activate();
		shader.setUniform1i("fontSheet", 0);
		shader.deactivate();

		return shader;
	}

	public void cleanup() {
		vbo.cleanup();
	}

	static {
		glContextShaderMap = new HashMap<Long, Shader>();
	}
}
