package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.math.Vector3f;
import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Color;
import de.nerogar.noiseInterface.math.*;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderableGeometry;

import java.util.ArrayList;
import java.util.List;

public class DebugLinesRenderable implements IRenderableGeometry {

	private static final int[]                     COMPONENT_COUNTS = { 1 };
	private static final Shader                    shader;
	private static       VertexBufferObjectIndexed vbo;

	private List<Line> lines;

	public DebugLinesRenderable() {
		this.lines = new ArrayList<>();

	}

	public void clear() {
		lines.clear();
	}

	public void addLine(IReadonlyVector3f v0, IReadonlyVector3f v1, Color color) {
		lines.add(new Line(v0, v1, color));
	}

	public void addAxes(IReadonlyMatrix4f modelMatrix, float size) {
		addLine(new Vector3f(
				modelMatrix.get(0, 3),
				modelMatrix.get(1, 3),
				modelMatrix.get(2, 3)
		), new Vector3f(
				modelMatrix.get(0, 3) + modelMatrix.get(0, 0) * size,
				modelMatrix.get(1, 3) + modelMatrix.get(1, 0) * size,
				modelMatrix.get(2, 3) + modelMatrix.get(2, 0) * size
		), Color.RED);

		addLine(new Vector3f(
				modelMatrix.get(0, 3),
				modelMatrix.get(1, 3),
				modelMatrix.get(2, 3)
		), new Vector3f(
				modelMatrix.get(0, 3) + modelMatrix.get(0, 1) * size,
				modelMatrix.get(1, 3) + modelMatrix.get(1, 1) * size,
				modelMatrix.get(2, 3) + modelMatrix.get(2, 1) * size
		), Color.GREEN);

		addLine(new Vector3f(
				modelMatrix.get(0, 3),
				modelMatrix.get(1, 3),
				modelMatrix.get(2, 3)
		), new Vector3f(
				modelMatrix.get(0, 3) + modelMatrix.get(0, 2) * size,
				modelMatrix.get(1, 3) + modelMatrix.get(1, 2) * size,
				modelMatrix.get(2, 3) + modelMatrix.get(2, 2) * size
		), Color.BLUE);
	}

	@Override
	public IReadOnlyTransformation getTransformation() {
		return null;
	}

	@Override
	public void setTransformation(IReadOnlyTransformation transformation) {
	}

	@Override
	public void renderGeometry(IRenderContext renderContext) {
		shader.activate();

		for (Line line : lines) {
			shader.setUniform3f("u_v0", line.v0.getX(), line.v0.getY(), line.v0.getZ());
			shader.setUniform3f("u_v1", line.v1.getX(), line.v1.getY(), line.v1.getZ());
			shader.setUniformMat4f("u_vMat", renderContext.getCamera().getViewMatrix().asBuffer());
			shader.setUniformMat4f("u_pMat", renderContext.getCamera().getProjectionMatrix().asBuffer());

			shader.setUniform3f("u_color", line.color.getR(), line.color.getG(), line.color.getB());
			shader.setUniform1f("u_emission", 1);

			vbo.render();
		}

		shader.deactivate();
	}

	static {
		shader = ShaderLoader.loadShader(
				FileUtil.get("<deferredRenderer/geometry/debugLinesRenderable.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/geometry/debugLinesRenderable.frag>", FileUtil.SHADER_SUBFOLDER)
		                                );

		vbo = new VertexBufferObjectIndexed(
				VertexBufferObject.LINES,
				COMPONENT_COUNTS,
				2,
				2,
				new int[] { 0, 1 },
				new float[] { 0, 1 }
		);
	}

	private record Line(IReadonlyVector3f v0, IReadonlyVector3f v1, Color color) {
	}

}
