package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.math.Transformation;
import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Color;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderable;

public class SingleWireframeRenderable implements IRenderable {

	private final  int[]  COMPONENT_COUNTS = { 3 };
	private static Shader shader;

	private Transformation renderProperties;

	private VertexBufferObject vbo;
	private Color              color;
	private float              emission;
	private boolean            shadeless;

	public SingleWireframeRenderable(WireframeMesh wireframeMesh, Color color, float emission, boolean shadeless) {
		renderProperties = new Transformation();

		vbo = new VertexBufferObjectIndexed(
				VertexBufferObject.LINES,
				COMPONENT_COUNTS,
				wireframeMesh.getIndexCount(),
				wireframeMesh.getVertexCount(),
				wireframeMesh.getIndexArray(),
				wireframeMesh.getPositionArray()
		);

		this.color = color;
		this.emission = emission;
		this.shadeless = shadeless;
	}

	public void setColor(Color color)           { this.color = color; }

	public void setEmission(float emission)     { this.emission = emission; }

	public void setShadeless(boolean shadeless) { this.shadeless = shadeless; }

	@Override
	public Transformation getTransformation() {
		return renderProperties;
	}

	@Override
	public void setParentRenderProperties(Transformation parentRenderProperties) {
		renderProperties.setParent(parentRenderProperties);
	}

	@Override
	public void renderGeometry(IRenderContext renderContext) {
		shader.activate();
		shader.setUniformMat4f("u_mMat", renderProperties.getModelMatrix().asBuffer());
		shader.setUniformMat4f("u_vMat", renderContext.getCamera().getViewMatrix().asBuffer());
		shader.setUniformMat4f("u_pMat", renderContext.getCamera().getProjectionMatrix().asBuffer());

		shader.setUniform3f("u_color", color.getR(), color.getG(), color.getB());
		shader.setUniform1f("u_emission", emission);
		shader.setUniform1f("u_shadeless", shadeless ? 1 : 0);

		vbo.render();

		shader.deactivate();
	}

	static {
		shader = ShaderLoader.loadShader("<deferredRenderer/geometry/singleWireframeRenderable.vert>", "<deferredRenderer/geometry/singleWireframeRenderable.frag>");
	}

}
