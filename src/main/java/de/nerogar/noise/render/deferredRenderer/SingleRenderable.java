package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.math.Transformation;
import de.nerogar.noise.render.*;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderable;

public class SingleRenderable implements IRenderable {

	private static final int[] COMPONENT_COUNTS = { 3, 3, 3, 3, 2 };

	private static Shader shader;

	private boolean            isInitialized = false;
	private Transformation     renderProperties;
	private VertexBufferObject vbo;
	private Texture2D          albedo;
	private Texture2D          normal;
	private Texture2D          material;
	private Mesh               mesh;

	public SingleRenderable(VertexBufferObject vbo, Texture2D albedo, Texture2D normal) {
		this.renderProperties = new Transformation();

		this.vbo = vbo;
		this.isInitialized = true;
		this.albedo = albedo;
		this.normal = normal;
		this.material = albedo;
	}

	public SingleRenderable(Mesh mesh, Texture2D albedo, Texture2D normal) {
		this.renderProperties = new Transformation();

		this.mesh = mesh;
		this.albedo = albedo;
		this.normal = normal;
		this.material = albedo;
	}

	public static VertexBufferObject createVbo(Mesh mesh) {
		return new VertexBufferObjectIndexed(
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
	}

	private void tryInitialize() {
		if (shader == null) {
			shader = ShaderLoader.loadShader("<deferredRenderer/geometry/singleRenderable.vert>", "<deferredRenderer/geometry/singleRenderable.frag>");
		}

		if (!isInitialized) {
			vbo = createVbo(mesh);
			this.mesh = null;
			isInitialized = true;
		}
	}

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
		tryInitialize();

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

	public void cleanupVbo() {
		if (vbo != null) {
			vbo.cleanup();
			vbo = null;
		}
	}

	public void cleanupTextures() {
		if (albedo != null) {
			albedo.cleanup();
			albedo = null;
		}
		if (normal != null) {
			normal.cleanup();
			normal = null;
		}
		if (material != null) {
			material.cleanup();
			material = null;
		}
	}

}
