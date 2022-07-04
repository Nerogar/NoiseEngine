package de.nerogar.noise.render.deferredRenderer.light;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noise.math.Transformation;
import de.nerogar.noise.render.*;
import de.nerogar.noise.render.deferredRenderer.RenderContext;
import de.nerogar.noise.render.deferredRenderer.SingleWireframeRenderable;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.*;
import de.nerogar.noiseInterface.render.deferredRenderer.*;

import java.util.List;
import java.util.function.Consumer;

public class PointLight implements ILight, IRenderableContainer {

	private static VertexBufferObject sphere;
	private static Shader             shader;
	private static WireframeMesh      debugMesh;

	private final IRenderableGeometry debugRenderable;
	private final ITransformation     debugTransformation;
	private final IMatrix4f           viewProjectionMatrix;

	private IReadOnlyTransformation transformation;
	private Color                   color;
	private float                   radius;
	private float                   intensity;

	public PointLight(IReadOnlyTransformation transformation, Color color, float radius, float intensity) {
		this.transformation = transformation;
		this.viewProjectionMatrix = new Matrix4f();
		setColor(color);
		setIntensity(intensity);
		setRadius(radius);

		// debug display
		this.debugRenderable = new SingleWireframeRenderable(debugMesh, DEBUG_MESH_COLOR, 0.0f, true);
		this.debugTransformation = new Transformation();
		this.debugRenderable.setTransformation(debugTransformation);
		this.debugTransformation.setParent(transformation);
	}

	@Override
	public IReadOnlyTransformation getTransformation() {
		return transformation;
	}

	@Override
	public void setTransformation(IReadOnlyTransformation transformation) {
		this.transformation = transformation;
	}

	public void setColor(Color color)         {this.color = color;}

	public void setRadius(float radius)       {this.radius = radius;}

	public void setIntensity(float intensity) {this.intensity = intensity;}

	private static void renderLight(PointLight light) {
		shader.setUniform3f(
				"u_position",
				light.transformation.getModelMatrix().get(0, 3),
				light.transformation.getModelMatrix().get(1, 3),
				light.transformation.getModelMatrix().get(2, 3)
		                   );
		shader.setUniform3f("u_color", light.color.getR(), light.color.getG(), light.color.getB());
		shader.setUniform1f("u_radius", light.radius);
		shader.setUniform1f("u_intensity", light.intensity);
		sphere.render();
	}

	@Override
	public void renderBatch(IRenderContext renderContext, List<ILight> lights) {
		Ray unitRayCenter = renderContext.getCamera().unproject(0, 0);
		Ray unitRayRight = renderContext.getCamera().unproject(1, 0);
		Ray unitRayTop = renderContext.getCamera().unproject(0, 1);
		unitRayRight.getStart().subtract(unitRayCenter.getStart());
		unitRayRight.getDir().subtract(unitRayCenter.getDir());
		unitRayTop.getStart().subtract(unitRayCenter.getStart());
		unitRayTop.getDir().subtract(unitRayCenter.getDir());

		IMatrix4f projectionMatrix = renderContext.getCamera().getProjectionMatrix();
		viewProjectionMatrix.set(renderContext.getCamera().getViewMatrix());
		viewProjectionMatrix.multiplyLeft(projectionMatrix);

		shader.activate();
		shader.setUniformMat4f("u_vpMat", viewProjectionMatrix.asBuffer());

		// position reconstruction
		shader.setUniform3f("u_unitRayCenterStart", unitRayCenter.getStart().getX(), unitRayCenter.getStart().getY(), unitRayCenter.getStart().getZ());
		shader.setUniform3f("u_unitRayCenterDir", unitRayCenter.getDir().getX(), unitRayCenter.getDir().getY(), unitRayCenter.getDir().getZ());
		shader.setUniform3f("u_unitRayRightStart", unitRayRight.getStart().getX(), unitRayRight.getStart().getY(), unitRayRight.getStart().getZ());
		shader.setUniform3f("u_unitRayRightDir", unitRayRight.getDir().getX(), unitRayRight.getDir().getY(), unitRayRight.getDir().getZ());
		shader.setUniform3f("u_unitRayTopStart", unitRayTop.getStart().getX(), unitRayTop.getStart().getY(), unitRayTop.getStart().getZ());
		shader.setUniform3f("u_unitRayTopDir", unitRayTop.getDir().getX(), unitRayTop.getDir().getY(), unitRayTop.getDir().getZ());
		shader.setUniform2f("u_inverseResolution", 1f / renderContext.getBufferWidth(), 1f / renderContext.getBufferHeight());
		shader.setUniform4f(
				"u_inverseDepthFunction",
				projectionMatrix.get(2, 2),
				projectionMatrix.get(2, 3),
				projectionMatrix.get(3, 2),
				projectionMatrix.get(3, 3)
		                   );

		shader.setUniform1Handle("u_depthBuffer", renderContext.getGBufferDepthTexture().getHandle());
		shader.setUniform1Handle("u_normalBuffer", renderContext.getGBufferNormalTexture().getHandle());
		shader.setUniform1Handle("u_materialBuffer", renderContext.getGBufferMaterialTexture().getHandle());

		for (ILight light : lights) {
			if (light instanceof PointLight) {
				renderLight((PointLight) light);
			}
		}
		shader.deactivate();
	}

	@Override
	public void getGeometry(IRenderContext renderContext, Consumer<IRenderableGeometry> adder) {
		adder.accept(debugRenderable);
	}

	static {
		shader = ShaderLoader.loadShader(FileUtil.get("<deferredRenderer/light/point.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/light/point.frag>", FileUtil.SHADER_SUBFOLDER));

		Mesh mesh = WavefrontLoader.load(FileUtil.get("<icoSphere.obj>", FileUtil.MESH_SUBFOLDER));

		sphere = new VertexBufferObjectIndexed(
				new int[] { 3 },
				mesh.getIndexCount(),
				mesh.getVertexCount(),
				mesh.getIndexArray(),
				mesh.getPositionArray()
		);

		debugMesh = new WireframeMesh(mesh);
	}

}
