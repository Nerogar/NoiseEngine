package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.render.*;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderable;

public class SingleRenderable implements IRenderable {

	private final int[] COMPONENT_COUNTS = { 3, 3, 3, 3, 2 };

	private RenderProperties3f renderProperties;

	private        VertexBufferObject vbo;
	private        Texture2D          albedo;
	private        Texture2D          normal;
	private        Texture2D          material;
	private static Shader             shader;

	public SingleRenderable(Mesh mesh, Texture2D albedo, Texture2D normal) {
		renderProperties = new RenderProperties3f();

		vbo = new VertexBufferObjectIndexed(
				COMPONENT_COUNTS,
				mesh.getIndexCount(),
				mesh.getVertexCount(),
				mesh.getIndexArray(),
				mesh.getPositionArray(),
				mesh.getNormalArray(),
				mesh.getTangentArray(),
				mesh.getBitangentArray(),
				mesh.getUVArray()
		);

		this.albedo = albedo;
		this.normal = normal;
		this.material = albedo;
	}

	@Override
	public RenderProperties3f getRenderProperties() {
		return renderProperties;
	}

	@Override
	public void setParentRenderProperties(RenderProperties3f parentRenderProperties) {
		renderProperties.setParent(parentRenderProperties);
	}

	@Override
	public void renderGeometry(IRenderContext renderContext) {
		shader.activate();
		shader.setUniformMat4f("u_mMat", renderProperties.getModelMatrix().asBuffer());
		shader.setUniformMat4f("u_nMat", renderProperties.getNormalMatrix().asBuffer());
		shader.setUniformMat4f("u_vMat", renderContext.getCamera().getViewMatrix().asBuffer());
		shader.setUniformMat4f("u_pMat", renderContext.getCamera().getProjectionMatrix().asBuffer());

		albedo.bind(0);
		normal.bind(1);
		material.bind(2);
		shader.setUniform1i("u_albedoTexture", 0);
		shader.setUniform1i("u_normalTexture", 1);
		shader.setUniform1i("u_materialTexture", 2);

		vbo.render();

		shader.deactivate();
	}

	static {
		shader = ShaderLoader.loadShader("<deferredRenderer/geometry/singleRenderable.vert>", "<deferredRenderer/geometry/singleRenderable.frag>");
	}

}
