package de.nerogar.noise.render.deferredRenderer.light;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noise.math.Transformation;
import de.nerogar.noise.render.*;
import de.nerogar.noise.render.deferredRenderer.SingleWireframeRenderable;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.IMatrix4f;
import de.nerogar.noiseInterface.math.IVector3f;
import de.nerogar.noiseInterface.render.deferredRenderer.ILight;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderable;

import java.util.List;

public class PointLight implements ILight {

	private static VertexBufferObject sphere;
	private static Shader             shader;
	private static WireframeMesh      debugMesh;

	private       IRenderable debugRenderable;
	private final IMatrix4f   viewProjectionMatrix;

	private Transformation renderProperties;
	private Color          color;
	private float          radius;
	private float          strength;

	public PointLight(IVector3f position, Color color, float radius, float strength) {
		this.renderProperties = new Transformation(0, 0, 0, position.getX(), position.getY(), position.getZ());
		this.viewProjectionMatrix = new Matrix4f();
		setPosition(position.clone());
		setColor(color);
		setStrength(strength);
		setRadius(radius);

		debugRenderable = new SingleWireframeRenderable(debugMesh, DEBUG_MESH_COLOR, 0.0f, true);
		debugRenderable.setParentRenderProperties(renderProperties);
	}

	@Override
	public Transformation getTransformation() {
		return renderProperties;
	}

	@Override
	public void setParentRenderProperties(Transformation parentRenderProperties) {
		renderProperties.setParent(parentRenderProperties);
	}

	public void setPosition(IVector3f position)        { renderProperties.setXYZ(position); }

	public void setPosition(float x, float y, float z) { renderProperties.setXYZ(x, y, z); }

	public void setColor(Color color)                  { this.color = color; }

	public void setRadius(float radius)                { this.radius = radius; renderProperties.setScale(radius, radius, radius); }

	public void setStrength(float strength)            { this.strength = strength; }

	private static void renderLight(PointLight light) {
		shader.setUniform3f(
				"u_position",
				light.renderProperties.getModelMatrix().get(0, 3),
				light.renderProperties.getModelMatrix().get(1, 3),
				light.renderProperties.getModelMatrix().get(2, 3)
		                   );
		shader.setUniform3f("u_color", light.color.getR(), light.color.getG(), light.color.getB());
		shader.setUniform1f("u_radius", light.radius);
		shader.setUniform1f("u_strength", light.strength);
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
		shader.setUniform3f("u_unitRayCenterStart", unitRayCenter.getStart().getX(), unitRayCenter.getStart().getY(), unitRayCenter.getStart().getZ());
		shader.setUniform3f("u_unitRayCenterDir", unitRayCenter.getDir().getX(), unitRayCenter.getDir().getY(), unitRayCenter.getDir().getZ());
		shader.setUniform3f("u_unitRayRightStart", unitRayRight.getStart().getX(), unitRayRight.getStart().getY(), unitRayRight.getStart().getZ());
		shader.setUniform3f("u_unitRayRightDir", unitRayRight.getDir().getX(), unitRayRight.getDir().getY(), unitRayRight.getDir().getZ());
		shader.setUniform3f("u_unitRayTopStart", unitRayTop.getStart().getX(), unitRayTop.getStart().getY(), unitRayTop.getStart().getZ());
		shader.setUniform3f("u_unitRayTopDir", unitRayTop.getDir().getX(), unitRayTop.getDir().getY(), unitRayTop.getDir().getZ());

		shader.setUniform2f("u_inverseResolution", 1f / renderContext.getgBufferWidth(), 1f / renderContext.getgBufferHeight());
		shader.setUniform4f(
				"u_inverseDepthFunction",
				projectionMatrix.get(2, 2),
				projectionMatrix.get(2, 3),
				projectionMatrix.get(3, 2),
				projectionMatrix.get(3, 3)
		                   );
		for (ILight light : lights) {
			if (light instanceof PointLight) {
				renderLight((PointLight) light);
			}
		}
		shader.deactivate();
	}

	@Override
	public void renderGeometry(IRenderContext renderContext) {
		debugRenderable.renderGeometry(renderContext);
	}

	static {
		shader = ShaderLoader.loadShader("<deferredRenderer/light/point.vert>", "<deferredRenderer/light/point.frag>");

		shader.activate();
		shader.setUniform1i("u_depthBuffer", DEPTH_BUFFER_SLOT);
		shader.setUniform1i("u_normalBuffer", NORMAL_BUFFER_SLOT);
		shader.setUniform1i("u_materialBuffer", MATERIAL_BUFFER_SLOT);
		shader.deactivate();

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
