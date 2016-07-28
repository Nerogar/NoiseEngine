package de.nerogar.noise.debug;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import de.nerogar.noise.Noise;
import de.nerogar.noise.render.*;
import de.nerogar.noise.input.KeyboardKeyEvent;
import de.nerogar.noise.render.fontRenderer.Font;
import de.nerogar.noise.render.fontRenderer.FontRenderableString;
import de.nerogar.noise.util.*;

public class DebugWindow {

	private static final int SIDEBAR_WIDTH = 220;
	private static final int PROFILER_HEIGHT = 300;
	private static final int RENDER_PADDING = 10;
	private static final Color PROFILER_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);

	private GLWindow window;

	private List<Profiler> profilerList;
	private int activeProfiler;

	private Matrix4f projectionMatrix;
	private Shader shader;
	private VertexList vertexList;

	private Font font;
	private List<FontRenderableString> stringList;
	private List<FontRenderableString> stringValueList;

	private float scrollOffset;
	private float renderedScrollOffset;

	public DebugWindow(Profiler... profiler) {
		profilerList = new ArrayList<Profiler>();

		for (Profiler p : profiler) {
			addProfiler(p);
		}
		activeProfiler = 0;

		if (!Noise.DEBUG) return;

		window = new GLWindow("debug", 800, 400, true, 0, null, null);

		shader = ShaderLoader.loadShader("<debug/profiler.vert>", "<debug/profiler.frag>");
		vertexList = new VertexList();

		projectionMatrix = new Matrix4f();
		setProjectionMatrix(window.getWidth(), window.getHeight());

		window.setSizeChangeListener(new GLWindowSizeChangeListener() {
			@Override
			public void onChange(int width, int height) {
				setProjectionMatrix(width, height);
			}
		});

		font = new Font("calibri", 14);
		stringList = new ArrayList<FontRenderableString>();
		stringValueList = new ArrayList<FontRenderableString>();
		createFontSidebar();
	}

	private void setProjectionMatrix(int width, int height) {
		Matrix4fUtils.setOrthographicProjection(projectionMatrix, 0f, width, height, 0, 1, -1);
	}

	private void createFontSidebar() {
		for (FontRenderableString s : stringList) {
			if (s != null) s.cleanup();
		}

		stringList.clear();

		for (ProfilerStatisticsCategory category : profilerList.get(activeProfiler).getProfilerCategories()) {
			for (ProfilerStatistic statistic : category.statisticList) {
				String s = statistic.name;

				stringList.add(new FontRenderableString(font, s, statistic.color, projectionMatrix, 1.0f, 1.0f));
			}

			stringList.add(null);
		}
	}

	public void addProfiler(Profiler profiler) {
		profilerList.add(profiler);
	}

	public void removeProfiler(Profiler profiler) {
		profilerList.remove(profiler);

		if (activeProfiler >= profilerList.size()) {
			activeProfiler = 0;
			createFontSidebar();
		}
	}

	public void update() {
		if (!Noise.DEBUG) return;
		if (window.isClosed()) return;
		if (window.shouldClose()) window.cleanup();

		long currentContext = GLWindow.getCurrentContext();
		window.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		scrollOffset += window.getInputHandler().getScrollDeltaY() * 0.2f;
		scrollOffset = MathHelper.clamp(scrollOffset, 0, profilerList.get(activeProfiler).getProfilerCategories().size() - 1);

		renderedScrollOffset += (scrollOffset - renderedScrollOffset) * 0.3f;

		boolean activeProfilerChanged = false;
		for (KeyboardKeyEvent event : window.getInputHandler().getKeyboardKeyEvents()) {
			if (event.action == GLFW.GLFW_PRESS) {
				if (event.key == GLFW.GLFW_KEY_RIGHT) {
					activeProfiler++;
					activeProfilerChanged = true;
					event.setProcessed();
				} else if (event.key == GLFW.GLFW_KEY_LEFT) {
					activeProfiler--;
					activeProfilerChanged = true;
					event.setProcessed();
				}
			}
		}

		if (activeProfilerChanged) {
			activeProfiler = ((activeProfiler % profilerList.size()) + profilerList.size()) % profilerList.size();
			createFontSidebar();
		}

		Profiler profiler = profilerList.get(activeProfiler);

		//Logger.log(Logger.DEBUG, profiler);
		window.setTitle("Debug (Profiler: " + profiler.getName() + ")");

		vertexList.clear();
		stringValueList.clear();
		
		float yOffset = -renderedScrollOffset;
		for (ProfilerStatisticsCategory category : profiler.getProfilerCategories()) {
			for (ProfilerStatistic statistic : category.statisticList) {

				//vbo

				int lastValue = statistic.getValue(0);
				int lastVertex = vertexList.addVertex(0f, (float) lastValue / category.maxHistory + yOffset, 0f, 0f, 0f, statistic.color.getR(), statistic.color.getG(), statistic.color.getB());

				for (int i = 1; i < profiler.getHistorySize() - 1; i++) {

					int value = statistic.getValue(i);
					int nextValue = statistic.getValue(i + 1);

					if (value == lastValue && nextValue == lastValue) continue;

					float x = (float) i / profiler.getHistorySize();
					float y = (float) value / category.maxHistory + yOffset;

					int vertex = vertexList.addVertex(x, y, 0f, 0f, 0f, statistic.color.getR(), statistic.color.getG(), statistic.color.getB());

					vertexList.addIndex(lastVertex, vertex);

					lastVertex = vertex;
					lastValue = value;
				}

				int value = statistic.getValue(profiler.getHistorySize() - 1);
				int vertex = vertexList.addVertex(1f, (float) value / category.maxHistory + yOffset, 0f, 0f, 0f, statistic.color.getR(), statistic.color.getG(), statistic.color.getB());
				vertexList.addIndex(lastVertex, vertex);

				FontRenderableString maxHistoryString = new FontRenderableString(font, String.valueOf(category.maxHistory), PROFILER_COLOR, projectionMatrix, 1.0f, 1.0f);
				maxHistoryString.render(RENDER_PADDING + 3, (int) ((yOffset + 1.0f) * PROFILER_HEIGHT) - font.getPointSize() + RENDER_PADDING);
				maxHistoryString.cleanup();

				FontRenderableString valueString = new FontRenderableString(font, String.valueOf(value), PROFILER_COLOR, projectionMatrix, 1f, 1f);
				stringValueList.add(valueString);
			}

			stringValueList.add(null);

			//render border
			int v1 = vertexList.addVertex(0f, 0f + yOffset, 0f, 0f, 0f, PROFILER_COLOR.getR(), PROFILER_COLOR.getG(), PROFILER_COLOR.getB());
			int v2 = vertexList.addVertex(1f, 0f + yOffset, 0f, 0f, 0f, PROFILER_COLOR.getR(), PROFILER_COLOR.getG(), PROFILER_COLOR.getB());
			int v3 = vertexList.addVertex(1f, 1f + yOffset, 0f, 0f, 0f, PROFILER_COLOR.getR(), PROFILER_COLOR.getG(), PROFILER_COLOR.getB());
			int v4 = vertexList.addVertex(0f, 1f + yOffset, 0f, 0f, 0f, PROFILER_COLOR.getR(), PROFILER_COLOR.getG(), PROFILER_COLOR.getB());

			vertexList.addIndex(v1, v2);
			vertexList.addIndex(v2, v3);
			vertexList.addIndex(v3, v4);
			vertexList.addIndex(v4, v1);

			yOffset += 1.0f;
		}

		VertexBufferObjectIndexed vbo = new VertexBufferObjectIndexed(
				VertexBufferObject.LINES,
				new int[] { 3, 3 },
				vertexList.getIndexCount(),
				vertexList.getVertexCount(),
				vertexList.getIndexArray(),
				vertexList.getPositionArray(),
				vertexList.getNormalArray() //normals used as color
		);

		shader.activate();
		shader.setUniformMat4f("projectionMatrix", projectionMatrix.asBuffer());
		shader.setUniform2f("cornerSmall", RENDER_PADDING, RENDER_PADDING);
		shader.setUniform2f("cornerBig", window.getWidth() - SIDEBAR_WIDTH - RENDER_PADDING, PROFILER_HEIGHT + RENDER_PADDING);
		vbo.render();
		shader.deactivate();
		vbo.cleanup();

		int y = RENDER_PADDING;

		for (int i = 0; i < stringList.size(); i++) {
			FontRenderableString s = stringList.get(i);
			FontRenderableString v = stringValueList.get(i);

			if (s != null) {
				s.render(window.getWidth() - SIDEBAR_WIDTH + 70, y);
				v.render(window.getWidth() - SIDEBAR_WIDTH, y);
				v.cleanup();
			}
			y += 20;
		}

		GLWindow.makeContextCurrent(currentContext);
	}

	public GLWindow getGLWindow() {
		return window;
	}
}
