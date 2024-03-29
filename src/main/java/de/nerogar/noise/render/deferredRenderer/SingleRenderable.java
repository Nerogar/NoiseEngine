package de.nerogar.noise.render.deferredRenderer;

import de.nerogar.noise.file.FileUtil;
import de.nerogar.noise.math.Matrix4f;
import de.nerogar.noise.render.*;
import de.nerogar.noiseInterface.math.IReadOnlyTransformation;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderContext;
import de.nerogar.noiseInterface.render.deferredRenderer.IRenderableGeometry;

public class SingleRenderable implements IRenderableGeometry {

	private static final int[] COMPONENT_COUNTS = { 3, 3, 3, 3, 2 };

	private static Shader shader;

	private boolean                 isInitialized = false;
	private IReadOnlyTransformation transformation;
	private VertexBufferObject      vbo;
	private Texture2D               albedo;
	private Texture2D               normal;
	private Texture2D               ambientOcclusion;
	private Texture2D               metalness;
	private Texture2D               roughness;
	private Texture2D               reflectance;
	private Mesh                    mesh;

	public SingleRenderable(VertexBufferObject vbo, Texture2D albedo, Texture2D normal, Texture2D ambientOcclusion, Texture2D metalness, Texture2D roughness, Texture2D reflectance) {
		this.vbo = vbo;
		this.isInitialized = true;
		this.albedo = albedo;
		this.normal = normal;
		this.ambientOcclusion = ambientOcclusion;
		this.metalness = metalness;
		this.roughness = roughness;
		this.reflectance = reflectance;
	}

	public SingleRenderable(Mesh mesh, Texture2D albedo, Texture2D normal, Texture2D ambientOcclusion, Texture2D metalness, Texture2D roughness, Texture2D reflectance) {
		this.mesh = mesh;
		this.albedo = albedo;
		this.normal = normal;
		this.ambientOcclusion = ambientOcclusion;
		this.metalness = metalness;
		this.roughness = roughness;
		this.reflectance = reflectance;
	}

	@Override
	public IReadOnlyTransformation getTransformation() {
		return transformation;
	}

	@Override
	public void setTransformation(IReadOnlyTransformation transformation) {
		this.transformation = transformation;
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
				mesh.getBiTangentArray(),
				mesh.getUVArray()
		);
	}

	private void tryInitialize() {
		if (shader == null) {
			shader = ShaderLoader.loadShader(FileUtil.get("<deferredRenderer/geometry/singleRenderable.vert>", FileUtil.SHADER_SUBFOLDER), FileUtil.get("<deferredRenderer/geometry/singleRenderable.frag>", FileUtil.SHADER_SUBFOLDER));
		}

		if (!isInitialized) {
			vbo = createVbo(mesh);
			this.mesh = null;
			isInitialized = true;
		}
	}

	@Override
	public void renderGeometry(IRenderContext renderContext) {
		tryInitialize();

		shader.activate();
		if (transformation != null) {
			shader.setUniformMat4f("u_mMat", transformation.getModelMatrix().asBuffer());
			shader.setUniformMat4f("u_nMat", transformation.getNormalMatrix().asBuffer());
		} else {
			shader.setUniformMat4f("u_mMat", Matrix4f.UNIT_MATRIX.asBuffer());
			shader.setUniformMat4f("u_nMat", Matrix4f.UNIT_MATRIX.asBuffer());
		}
		shader.setUniformMat4f("u_vMat", renderContext.getCamera().getViewMatrix().asBuffer());
		shader.setUniformMat4f("u_pMat", renderContext.getCamera().getProjectionMatrix().asBuffer());

		shader.setUniform1Handle("u_albedoTexture", albedo.getHandle());
		shader.setUniform1Handle("u_normalTexture", normal.getHandle());
		shader.setUniform1Handle("u_ambientOcclusionTexture", ambientOcclusion.getHandle());
		shader.setUniform1Handle("u_metalnessTexture", metalness.getHandle());
		shader.setUniform1Handle("u_roughnessTexture", roughness.getHandle());
		shader.setUniform1Handle("u_reflectanceTexture", reflectance.getHandle());

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
		if (ambientOcclusion != null) {
			ambientOcclusion.cleanup();
			ambientOcclusion = null;
		}
		if (metalness != null) {
			metalness.cleanup();
			metalness = null;
		}
		if (roughness != null) {
			roughness.cleanup();
			roughness = null;
		}
		if (reflectance != null) {
			reflectance.cleanup();
			reflectance = null;
		}
	}

}
