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

public class ConeLight implements ILight, IRenderableContainer {

	private static VertexBufferObject sphere;
	private static Shader             shader;
	private static WireframeMesh      debugMesh;

	private       IRenderableGeometry debugRenderable;
	private final ITransformation     debugTransformation;
	private final IMatrix4f           viewProjectionMatrix;

	private IReadOnlyTransformation transformation;
	private Color                   color;
	private float                   radius;
	private float                   strength;
	private float                   angle;
	private float                   cosAngle;
	private float                   invertedCosAngle;
	private float                   scale;

	public ConeLight(IReadOnlyTransformation transformation, Color color, float radius, float strength, float angle) {
		this.viewProjectionMatrix = new Matrix4f();
		setColor(color);
		setStrength(strength);
		setRadius(radius);
		setAngle(angle);

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

	public void setColor(Color color) {this.color = color;}

	public void setRadius(float radius) {
		this.radius = radius;
		float tan = (float) Math.tan((angle / 360.0) * Math.PI);
		this.scale = tan * radius;
	}

	public void setStrength(float strength) {this.strength = strength;}

	public void setAngle(float angle) {
		this.angle = angle;
		this.cosAngle = (float) Math.cos((angle / 360.0) * Math.PI);
		this.invertedCosAngle = (float) (1.0 / (1.0 - cosAngle));
		float tan = (float) Math.tan((angle / 360.0) * Math.PI);
		this.scale = tan * radius;
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
		shader.setUniformMat4f("u_mMat", transformation.getModelMatrix().asBuffer());
		shader.setUniformMat4f("u_unitRayCenterStart", viewProjectionMatrix.asBuffer());
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
		for (ILight light : lights) {
			if (light instanceof ConeLight) {
				renderLight(renderContext, (ConeLight) light);
			}
		}
		shader.deactivate();
	}

	private static void renderLight(IRenderContext renderContext, ConeLight light) {
		IMatrix4f modelMatrix = light.transformation.getModelMatrix();
		shader.setUniform3f("u_position", modelMatrix.get(0, 3), modelMatrix.get(1, 3), modelMatrix.get(2, 3));
		shader.setUniform3f("u_direction", -modelMatrix.get(0, 2), -modelMatrix.get(1, 2), -modelMatrix.get(2, 2));
		shader.setUniform3f("u_color", light.color.getR(), light.color.getG(), light.color.getB());
		shader.setUniform1f("u_radius", light.radius);
		shader.setUniform1f("u_strength", light.strength);
		shader.setUniform3f("u_angleData", light.cosAngle, light.invertedCosAngle, light.scale); // TODO: use the scale to scale the cone mesh in x and y direction
		shader.setUniform1Handle("u_depthBuffer", renderContext.getGBufferDepthTexture().getHandle());
		shader.setUniform1Handle("u_normalBuffer", renderContext.getGBufferNormalTexture().getHandle());
		shader.setUniform1Handle("u_materialBuffer", renderContext.getGBufferMaterialTexture().getHandle());
		sphere.render();
	}

	@Override
	public void getGeometry(IRenderContext renderContext, Consumer<IRenderableGeometry> adder) {
		adder.accept(debugRenderable);
	}

	static {
		shader = ShaderLoader.loadShader(FileUtil.get("<deferredRenderer/light/cone.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/light/cone.frag>", FileUtil.SHADER_SUBFOLDER));

		Mesh mesh = WavefrontLoader.load(FileUtil.get("<cone.obj>", FileUtil.MESH_SUBFOLDER));

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
