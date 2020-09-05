package de.nerogar.noise.render.deferredRenderer.light;

import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Color;
import de.nerogar.noiseInterface.math.IVector3f;
import de.nerogar.noiseInterface.render.deferredRenderer.ILight;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;

import java.util.List;

public class SunLight implements ILight {

	private static VertexBufferObject fullscreenQuad;
	private static Shader             shader;

	private IVector3f direction;
	private Color    color;
	private float    strength;

	public SunLight(IVector3f direction, Color color, float strength) {
		this.direction = direction.normalized();
		this.color = color;
		this.strength = strength;
	}

	@Override
	public RenderProperties3f getRenderProperties() {
		return null;
	}

	@Override
	public void setParentRenderProperties(RenderProperties3f parentRenderProperties) { }

	public void setDirection(IVector3f direction) {
		this.direction.set(direction).normalize();
	}

	public void setDirection(float x, float y, float z) {
		this.direction.set(x, y, z).normalize();
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	@Override
	public void renderBatch(IRenderContext renderContext, List<ILight> lights) {
		shader.activate();
		for (ILight light : lights) {
			if (light instanceof SunLight) {
				renderLight((SunLight) light);
			}
		}
		shader.deactivate();
	}

	private static void renderLight(SunLight light) {
		shader.setUniform3f("u_direction", light.direction.getX(), light.direction.getY(), light.direction.getZ());
		shader.setUniform3f("u_color", light.color.getR(), light.color.getG(), light.color.getB());
		shader.setUniform1f("u_strength", light.strength);
		fullscreenQuad.render();
	}

	static {
		shader = ShaderLoader.loadShader("<deferredRenderer/light/sun.vert>", "<deferredRenderer/light/sun.frag>");

		shader.activate();
		shader.setUniform1i("u_normalBuffer", NORMAL_BUFFER_SLOT);
		shader.setUniform1i("u_materialBuffer", MATERIAL_BUFFER_SLOT);
		shader.deactivate();

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
