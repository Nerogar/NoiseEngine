package de.nerogar.noise.render.fontRenderer;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Color;
import de.nerogar.noiseInterface.math.IMatrix4f;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

/**
 * a class for easy text rendering
 */
public class FontRenderableString {

	private static final int TAB_SIZE = 4;

	private static HashMap<Long, Shader> glContextShaderMap;

	private String text;

	private Font  font;
	private Color color;

	private VertexList         vertexList;
	private VertexBufferObject vbo;

	private IMatrix4f projectionMatrix;
	private float     pointSizeX;
	private float     pointSizeY;

	private int width, height;

	/**
	 * pointSizeX and pointSizeY are the sizes of a single point in viewSpace. Example:
	 * <p>
	 * on a 800x600 pixel screen with an orthographic projection matrix that maps
	 * (0/0) to (0/0) and (1/1) to (800/600) the point sizes should be:
	 * <ul>
	 * <li>pointSizeX = 1/800</li>
	 * <li>pointSizeY = 1/600</li>
	 * </ul>
	 *
	 * @param font             the {@link Font Font} for this string
	 * @param text             the text to display
	 * @param color            the {@link Color color}
	 * @param projectionMatrix the projection matrix that will be used to display this string
	 * @param pointSizeX       the size of a point
	 * @param pointSizeY       the size of a point
	 */
	public FontRenderableString(Font font, String text, Color color, IMatrix4f projectionMatrix, float pointSizeX, float pointSizeY) {
		this.font = font;
		this.color = color;

		setRenderDimensions(projectionMatrix, pointSizeX, pointSizeY);

		vbo = createVBO(text);
	}

	public void setText(String text) {
		if (this.text != null && this.text.equals(text)) {
			return;
		}

		this.text = text;

		vbo.cleanup();
		vbo = createVBO(text);
	}

	private VertexBufferObject createVBO(String text) {
		if (vertexList == null) {
			vertexList = new VertexList();
		} else {
			vertexList.clear();
		}

		width = 0;
		height = 0;

		int offsetX = 0;
		int offsetY = -font.getBaseline();

		for (char c : text.toCharArray()) {
			if (c == '\n') {
				offsetX = 0;
				offsetY -= font.getLineSpace();
			} else if (c == '\t') {
				offsetX = ((offsetX / font.getPointSize() / TAB_SIZE) + 1) * TAB_SIZE * font.getPointSize();
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

			width = Math.max(width, offsetX);
		}
		height = font.getPointSize() - (offsetY + font.getBaseline());

		return new VertexBufferObjectIndexed(
				new int[] { 3, 2 },
				vertexList.getIndexCount(),
				vertexList.getVertexCount(),
				vertexList.getIndexArray(),
				vertexList.getPositionArray(),
				vertexList.getUVArray()
		);

	}

	/**
	 * updates the color set at creation time
	 *
	 * @param color the new color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * updates the projection settings set at creation time
	 *
	 * @param projectionMatrix the new projectionMatrix
	 * @param pointSizeX       the new pointSizeX
	 * @param pointSizeY       the new pointSizeY
	 */
	public void setRenderDimensions(IMatrix4f projectionMatrix, float pointSizeX, float pointSizeY) {
		this.projectionMatrix = projectionMatrix;
		this.pointSizeX = pointSizeX;
		this.pointSizeY = pointSizeY;
	}

	/**
	 * parameters are in units set by the projection matrix
	 *
	 * @param left   the left border of the string
	 * @param bottom the baseline for the first line in the string
	 */
	public void render(int left, int bottom) {
		long currentContext = GLWindow.getCurrentContext();

		font.getTexture().bind(0);

		Shader shader = glContextShaderMap.computeIfAbsent(currentContext, k -> loadFontShader());

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA, GL_ONE);

		shader.activate();
		shader.setUniformMat4f("projectionMatrix", projectionMatrix.asBuffer());
		shader.setUniform2f("pointSize", pointSizeX, pointSizeY);
		shader.setUniform2f("offset", left - font.getTexturePadding(), bottom);
		shader.setUniform4f("fontColor", color.getR(), color.getG(), color.getB(), color.getA());
		vbo.render();
		shader.deactivate();

		glDisable(GL_BLEND);
	}

	/**
	 * returns the width of this string in view space
	 *
	 * @return the width
	 */
	public float getWidth() {
		return (float) width * pointSizeX;
	}

	/**
	 * returns the height of this string in view space
	 *
	 * @return the height
	 */
	public float getHeight() {
		return (float) height * pointSizeY;
	}

	private static Shader loadFontShader() {
		Shader shader = ShaderLoader.loadShader(FileUtil.get("<font/font.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<font/font.frag>", FileUtil.SHADER_SUBFOLDER));
		shader.activate();
		shader.setUniform1i("fontSheet", 0);
		shader.deactivate();

		return shader;
	}

	/**
	 * Cleans all internal opengl resources. This string is unusable after this call.
	 */
	public void cleanup() {
		vbo.cleanup();
	}

	static {
		glContextShaderMap = new HashMap<Long, Shader>();
	}
}
