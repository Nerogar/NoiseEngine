package de.nerogar.noise.render.deferredRenderer.light;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Ray;
import de.nerogar.noiseInterface.math.*;
import de.nerogar.noiseInterface.render.deferredRenderer.ILight;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;

import java.util.List;

public class SunLight implements ILight {

	private static VertexBufferObject fullscreenQuad;
	private static Shader             shader;

	private IVector3f direction;
	private Color     color;
	private float     intensity;

	public SunLight(IVector3f direction, Color color, float intensity) {
		this.direction = direction.normalized();
		this.color = color;
		this.intensity = intensity;
	}

	@Override
	public IReadOnlyTransformation getTransformation() {
		return null;
	}

	@Override
	public void setTransformation(IReadOnlyTransformation transformation) {
	}

	public void setDirection(IVector3f direction) {
		this.direction.set(direction).normalize();
	}

	public void setDirection(float x, float y, float z) {
		this.direction.set(x, y, z).normalize();
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
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

		shader.activate();

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

		for (ILight light : lights) {
			if (light instanceof SunLight) {
				renderLight(renderContext, (SunLight) light);
			}
		}
		shader.deactivate();
	}

	private static void renderLight(IRenderContext renderContext, SunLight light) {
		IReadOnlyTransformation cameraTransformation = renderContext.getCamera().getTransformation();

		shader.setUniform3f("u_direction", light.direction.getX(), light.direction.getY(), light.direction.getZ());
		shader.setUniform3f("u_color", light.color.getR(), light.color.getG(), light.color.getB());
		shader.setUniform1f("u_intensity", light.intensity);
		shader.setUniform3f("u_cameraPosition", cameraTransformation.getEffectiveX(), cameraTransformation.getEffectiveY(), cameraTransformation.getEffectiveZ());
		shader.setUniform1Handle("u_depthBuffer", renderContext.getGBufferDepthTexture().getHandle());
		shader.setUniform1Handle("u_albedoBuffer", renderContext.getGBufferAlbedoTexture().getHandle());
		shader.setUniform1Handle("u_normalBuffer", renderContext.getGBufferNormalTexture().getHandle());
		shader.setUniform1Handle("u_materialBuffer", renderContext.getGBufferMaterialTexture().getHandle());
		fullscreenQuad.render();
	}

	static {
		shader = ShaderLoader.loadShader(FileUtil.get("<deferredRenderer/light/sun.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/light/sun.frag>", FileUtil.SHADER_SUBFOLDER));

		fullscreenQuad = new VertexBufferObjectIndexed(
				new int[] { 2, 2 },
				6,
				4,
				new int[] { 0, 1, 2, 2, 3, 0 },
				new float[] { -1.0f, -1.0f,/**/-1.0f, 1.0f, /**/1.0f, 1.0f,/**/1.0f, -1.0f },
				new float[] { 0.0f, 0.0f,/**/0.0f, 1.0f, /**/1.0f, 1.0f,/**/1.0f, 0.0f }
		);
	}

}
